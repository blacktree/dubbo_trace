package lz.dubbo.trace.model;

public enum IdTypeEnum {

    SPAN_ID(1, "生成SPAN的ID"),

    TRACE_ID(2, "生成TRACE的ID");

    private int    type;

    private String desc;

    IdTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
