package com.han.model.enums;

/**
 * 队伍状态枚举
 */
public enum TeamStatusEnums {
    PUBLIC(0, "公开"),
    PRIVATE(1, "私有"),
    SECRET(2, "加密");

    private int value;
    private String text;

    TeamStatusEnums(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public static TeamStatusEnums getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        TeamStatusEnums[] values = TeamStatusEnums.values();
        for (TeamStatusEnums enums : values) {
            if (enums.getValue() == value) {
                return enums;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
