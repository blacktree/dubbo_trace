package lz.dubbo.trace;

import lz.dubbo.trace.TraceClientConst.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lz.dubbo.trace.model.BinaryAnnotation;
import lz.dubbo.trace.model.Endpoint;
import lz.dubbo.trace.model.IdTypeEnum;
import lz.dubbo.trace.model.Span;
import lz.dubbo.trace.model.TraceContext;


public class Trace {


	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	private static final Trace               instance        = new Trace();
	private String ip;
	private String appName;

	// 针对service的上下文，只能满足顺序调用的场景。
	private static ThreadLocal<TraceContext> serviceContext  = new ThreadLocal<TraceContext>();


	private Trace() {
		init();
	}

	public static Trace getInstance() {
		return instance;
	}

	public void init() {

		synchronized (this) {
			try {
				InetAddress address=InetAddress.getLocalHost();
				this.ip=address.getHostAddress();
				// 初始化ID工厂
				IdFactory.getInstance().init(appName,ip);
			} catch (Exception e) {
				logger.error("init trace config failed.", e);
			}
		}
	}

	public String getAppName() {
		return this.appName;
	}


	public TraceContext getServiceContext() {
		return serviceContext.get();
	}

	public void setServiceContext(TraceContext traceContext) {
		serviceContext.set(traceContext);
	}

	public void removeServiceContext() {
		serviceContext.remove();
	}


	/**
	 * 新建span
	 * 
	 * @param spanName
	 * @return
	 */
	public Span newSpan(String spanName,String traceId) {
		Span span = new Span();
		span.setSpanName(spanName);
		span.setRpcId(TraceClientConst.INIT_RPC_ID);
		span.setTraceId(traceId);

		span.setId(IdFactory.getInstance().getNextId(IdTypeEnum.SPAN_ID.getType(), spanName));


		return span;
	}

	/**
	 * 生成子span
	 * 
	 * @param traceId
	 * @param parentId
	 * @param spanName
	 * @param isSample
	 * @return
	 */
	public Span genSpan(String traceId, String spanId, String parentId, String rpcId, String spanName) {

		Span span = new Span();
		span.setTraceId(traceId);
		span.setRpcId(rpcId);
		span.setId(spanId);
		span.setParentId(parentId);
		span.setSpanName(spanName);
		return span;
	}
 
	/**
	 * 记录异常Annotation
	 * 
	 * @param exception 异常
	 */
	public void logException(Span span, Throwable exception) {
		String exceptionInfo = exception == null ? "exception is null " : formatExpStackTrace(exception);
		addBinaryAnnotation(span, TraceClientConst.EXCEPTION_KEY, exceptionInfo, TraceClientConst.EXCEPTION_TYPE);
		String exceptionName = (exception == null ? "null" : exception.getClass().getName());
		addBinaryAnnotation(span, TraceClientConst.EXCEPTION_NAME_KEY, exceptionName, TraceClientConst.EXCEPTION_TYPE);
	}

	/**
	 * 将异常的堆栈处理成可读的String ,偷懒不搞了
	 * 
	 * @param exception 异常
	 */
	private String formatExpStackTrace(Throwable e) {
        
		return e.getMessage();
	}

	/**
	 * 记录并发数Annotation
	 * 
	 * @param span
	 * @param concurrent
	 */
	public void logConcurrent(Span span, int concurrent) {
		addBinaryAnnotation(span, TraceClientConst.CONCURRENT_KEY, String.valueOf(concurrent), TraceClientConst.INFO_TYPE);
	}

	private void addBinaryAnnotation(Span span, String key, String value, String type) {
		BinaryAnnotation annotation = new BinaryAnnotation();
		annotation.setKey(key);
		annotation.setValue(value);
		annotation.setType(type);


		span = getServiceContext().getSpan();

		if (span != null) {
			span.addBinaryAnnotation(annotation);
			return;
		}

	}

	public String getIPAddress() {
		// TODO Auto-generated method stub
		return this.ip;
	}
}
