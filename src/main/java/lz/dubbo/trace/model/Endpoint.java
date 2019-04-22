package lz.dubbo.trace.model;

import java.io.Serializable;

/**
 * User: hzwangxx Date: 14-8-8 Time: 11:36
 */
public class Endpoint implements Serializable {

    private static final long serialVersionUID = 2559829332806818244L;
    private String            ip;
    private String            host;
    private Integer           port;

    public Endpoint() {
    }

    public Endpoint(String ip, String host, Integer port) {

        this.ip = ip;
        this.host = host;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Endpoint{" + "ip='" + ip + '\'' + ", host='" + host + '\'' + ", port=" + port + '}';
    }
}
