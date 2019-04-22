package lz.dubbo.trace.model;

import java.io.Serializable;

/**
 * User: hzwangxx Date: 14-8-4 Time: 11:16
 */
public class Annotation implements Serializable {

    private static final long serialVersionUID = -3992952665397853384L;
    private Long              timestamp;
    private Integer           size;
    private Endpoint          host;
    private AnnType           value;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Endpoint getHost() {
        return host;
    }

    public void setHost(Endpoint host) {
        this.host = host;
    }

    public AnnType getValue() {
        return value;
    }

    public void setValue(AnnType value) {
        this.value = value;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Annotation{" + "timestamp=" + timestamp + ", size=" + size + ", host=" + host + ", value=" + value
                + '}';
    }

    public enum AnnType {
        CS, CR, SS, SR
    }
}
