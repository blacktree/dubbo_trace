package lz.dubbo.trace;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;

import lz.dubbo.trace.model.Annotation;

public class Spans {

    /**
     * 生成span
     * 
     * @param type
     * @param endpoint
     * @param start
     * @return
     */
    public static Annotation genAnnotation(Annotation.AnnType type, long start) {
        Annotation annotation = new Annotation();
        annotation.setValue(type);
        annotation.setTimestamp(start);
        // annotation.setSize(size);
        return annotation;
    }
    
    /**
     * 生成spanName by RpcContext
     * 
     * @param context
     * @return
     */
    public static String getSpanName(RpcContext context) {

        String serviceName = context.getUrl().getServiceInterface(); // serviceName
        String methodName = context.getMethodName(); // 方法名
        String generic = context.getUrl().getParameter("generic");  // 处理泛化调用
        if ("true".equals(generic)) {
            Object[] rpcArguments = context.getArguments();
            if (rpcArguments != null && rpcArguments.length > 0) {
                methodName = String.valueOf(rpcArguments[0]);
            }
        }
        return serviceName + "." + methodName;
    }

    /**
     * 生成spanName by Invoker and Invocation
     *
     * @param invoker
     * @param invocation
     * @return
     */
    public static String getSpanName(Invoker<?> invoker, Invocation invocation) {

        return invoker.getInterface().getName() + "." + invocation.getMethodName();
    }
}
