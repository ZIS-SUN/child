package com.kindergarten.common.enums;

/**
 * 用户角色枚举
 */
public enum UserRole {
    PRINCIPAL("PRINCIPAL", "园长"),
    TEACHER("TEACHER", "教师"),
    PARENT("PARENT", "家长");

    private final String code;
    private final String desc;

    UserRole(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
