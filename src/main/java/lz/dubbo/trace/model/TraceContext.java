package lz.dubbo.trace.model;

import lz.dubbo.trace.model.Span;
 
public class TraceContext {

    private boolean isRoot;  // 是否根路径

    private boolean isSmaple;

    private Span    span;

    public TraceContext() {
    }

    public static void main(String[] args) {
    }

    public Span getSpan() {
        return span;
    }

    public void setSpan(Span span) {
        this.span = span;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public boolean isSmaple() {
        return isSmaple;
    }

    public void setSmaple(boolean isSmaple) {
        this.isSmaple = isSmaple;
    }
}
