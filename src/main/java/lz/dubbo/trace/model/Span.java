package lz.dubbo.trace.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Span implements Serializable {

    public static final char RPC_ID_SEPARATOR = '.';

    private static final long      serialVersionUID  = -7216852734354202598L;
    private String                 traceId;
    private String                 id;
    private String                 parentId;
    private String                 rpcId;
    private String                 spanName;
    private String                 appName;                                              
    private transient int          rpcCounter;

    private String                hostIp;

    private List<Annotation>       annotations       = new ArrayList<Annotation>();
    private List<BinaryAnnotation> binaryAnnotations = new ArrayList<BinaryAnnotation>();

    public synchronized String newSubRpcId() {
        ++rpcCounter;
        return rpcId + RPC_ID_SEPARATOR + rpcCounter;
    }

    public String getRpcId() {
        return rpcId;
    }

    public void setRpcId(String rpcId) {
        this.rpcId = rpcId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSpanName() {
        return spanName;
    }

    public void setSpanName(String spanName) {
        this.spanName = spanName;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public List<BinaryAnnotation> getBinaryAnnotations() {
        return binaryAnnotations;
    }

    public void setBinaryAnnotations(List<BinaryAnnotation> binaryAnnotations) {
        this.binaryAnnotations = binaryAnnotations;
    }

    public void addBinaryAnnotation(BinaryAnnotation binaryAnnotation) {
        binaryAnnotations.add(binaryAnnotation);
    }

    public void addAnnotation(Annotation annotation) {
        annotations.add(annotation);
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
 
    @Override
    public String toString() {
        return "Span{" + "traceId='" + traceId + '\'' + ", id='" + id + '\'' + ", parentId='" + parentId + '\''
               + ", rpcId='" + rpcId + '\'' + ", spanName='" + spanName + '\'' + ", appName='" + appName + '\''
               + ", annotations=" + annotations + ", binaryAnnotations=" + binaryAnnotations + '}';
    }

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
}
