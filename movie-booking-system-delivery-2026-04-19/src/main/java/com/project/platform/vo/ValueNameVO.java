package com.project.platform.vo;

public class ValueNameVO {
    private Object value;
    private Object name;

    public ValueNameVO() {
    }

    public ValueNameVO(Object value, Object name) {
        this.value = value;
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }
}
