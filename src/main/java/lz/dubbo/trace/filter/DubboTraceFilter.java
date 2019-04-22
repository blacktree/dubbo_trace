package lz.dubbo.trace.filter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;

import com.alibaba.dubbo.rpc.*;
import com.alibaba.fastjson.JSON;

import lz.dubbo.trace.IdFactory;
import lz.dubbo.trace.Spans;
import lz.dubbo.trace.Trace;
import lz.dubbo.trace.TraceClientConst;
import lz.dubbo.trace.model.Annotation;
import lz.dubbo.trace.model.IdTypeEnum;
import lz.dubbo.trace.model.Span;
import lz.dubbo.trace.model.TraceContext;

@Activate(group = { Constants.CONSUMER, Constants.PROVIDER })
public class DubboTraceFilter implements Filter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Trace                                      trace       = Trace.getInstance();

	private final ConcurrentMap<String, AtomicInteger> concurrents = new ConcurrentHashMap<String, AtomicInteger>();

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

		RpcContext context = RpcContext.getContext();
		Span span = null;// 本次调用的span

		String spanName = Spans.getSpanName(context);

		getConcurrent(invoker, invocation).incrementAndGet(); // 并发计数

		String traceId=invocation.getAttachment(TraceClientConst.TRACE_ID);


		boolean consumerSide = context.isConsumerSide();

		if(consumerSide) {

			Span parentSpan=null;
			if(trace.getServiceContext()!=null) {
				parentSpan=trace.getServiceContext().getSpan();
			}

			if(parentSpan==null) {
				span=trace.newSpan(spanName,traceId);
			}else {
				span=trace.genSpan(parentSpan.getTraceId(),
						IdFactory.getInstance().getNextId(IdTypeEnum.SPAN_ID.getType(), spanName),
						parentSpan.getId(), parentSpan.newSubRpcId(), spanName);
			}
		}else {
			String parentId=invocation.getAttachment(TraceClientConst.PARENT_ID);
			String spanId=invocation.getAttachment(TraceClientConst.SPAN_ID);
			String rpcId=invocation.getAttachment(TraceClientConst.RPC_ID);
			if(parentId!=null&&spanId!=null&&rpcId!=null) {
				span=trace.genSpan(traceId, spanId, parentId, rpcId, spanName);
			}else {
				span=trace.newSpan(spanName,traceId);
			}
		}

		span.setAppName(trace.getAppName());
		span.setHostIp(trace.getIPAddress());
		startInvoke(span,consumerSide);

		RpcInvocation rpcInv=(RpcInvocation) invocation;
		this.setAttachment(span, rpcInv);
		Result result = null;
		try {
			result=invoker.invoke(invocation);
			return result;
		} catch (Exception e) {
			trace.logException(span, e);
			throw new RpcException(e);
		} finally {
			exceptionHandler(span, result);
			if (span != null) {
				endInvoke(consumerSide, span);
				// 记录并发数据
				int concurrent = getConcurrent(invoker, invocation).get(); // 当前并发数
				trace.logConcurrent(span, concurrent);
				logger.info(JSON.toJSONString(span));
			}
			getConcurrent(invoker, invocation).decrementAndGet(); // 并发计数
		}

	}

	private void exceptionHandler(Span span, Result result) {
		if (result != null && result.hasException()) {
			trace.logException(span, result.getException());
		}
	}

	/**
	 * 获取并发计数器
	 */
	private AtomicInteger getConcurrent(Invoker<?> invoker, Invocation invocation) {
		String key = Spans.getSpanName(invoker, invocation);
		AtomicInteger concurrent = concurrents.get(key);
		if (concurrent == null) {
			final AtomicInteger atomicInteger = new AtomicInteger();
			concurrent = concurrents.putIfAbsent(key, atomicInteger);
			if (concurrent == null) {
				concurrent = atomicInteger;
			}
		}
		return concurrent;
	}


	/**
	 * 设置下游各参数
	 * 
	 * @param span span
	 * @param invocation invocation
	 */
	private void setAttachment(Span span, RpcInvocation invocation) {
		invocation.setAttachment(TraceClientConst.SPAN_ID, String.valueOf(span.getId()));
		invocation.setAttachment(TraceClientConst.PARENT_ID, String.valueOf(span.getParentId()));
		invocation.setAttachment(TraceClientConst.RPC_ID, String.valueOf(span.getRpcId()));
		invocation.setAttachment(TraceClientConst.TRACE_ID, String.valueOf(span.getTraceId()));
	}

	/**
	 * s 调用开始，生成CS|SR Annotation
	 * 
	 * @param span span
	 * @param consumerSide 是否消费端
	 */
	private void startInvoke(Span span, boolean consumerSide) {

		Long startTime = System.currentTimeMillis();
		if (consumerSide ) {
			// CS Annotation
			Annotation annotation = Spans.genAnnotation(Annotation.AnnType.CS, startTime);
			span.addAnnotation(annotation);
		} else {
			TraceContext traceContext = new TraceContext();
			// SR Annotation
			Annotation annotation = Spans.genAnnotation(Annotation.AnnType.SR, startTime);
			span.addAnnotation(annotation);
			traceContext.setSpan(span);

			trace.setServiceContext(traceContext);
		}
	}

	/**
	 * 调用结束，生成CR|SS Annotation
	 * 
	 * @param consumerSide 是否消费端
	 * @param span span
	 */
	private void endInvoke(boolean consumerSide, Span span) {

		long endTime = System.currentTimeMillis();
		// int contextSize = getRpcContextSize();

		if (consumerSide) {
			// CR Annotation
			Annotation annotation = Spans.genAnnotation(Annotation.AnnType.CR, endTime);
			span.addAnnotation(annotation);
		} else {
			// SS Annotation
			Annotation annotation = Spans.genAnnotation(Annotation.AnnType.SS, endTime);
			span.addAnnotation(annotation);
			trace.removeServiceContext();

		}
	}
}
