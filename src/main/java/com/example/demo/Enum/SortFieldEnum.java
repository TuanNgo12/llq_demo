package com.example.demo.Enum;

public enum SortFieldEnum {

    PARAMNAME("paramName", "PARAM_NAME"),
    PARAMVALUE("paramValue", "PARAM_VALUE"),
    PARAMTYPE("paramType", "PARAM_TYPE"),
    DESCRIPTION("description", "DESCRIPTION"),
    COMPONENTCODE("componentCode", "COMPONENT_CODE"),
    NEWDATA("newData", "NEW_DATA"),
    STATUS("status", "STATUS"),
    ISACTIVE("isActive", "IS_ACTIVE"),
    ISDISPLAY("isDisplay", "IS_DISPLAY"),
    EFFECTIVE_DATE("effectiveDate", "EFFECTIVE_DATE"),
    END_EFFECTIVE_DATE("endEffectiveDate", "END_EFFECTIVE_DATE");

    private final String field;
    private final String dbField;

    SortFieldEnum(String field, String dbField) {
        this.field = field;
        this.dbField = dbField;
    }

    public String getField() {
        return field;
    }

    public String getDbField() {
        return dbField;
    }

    // Hàm tiện ích convert từ field string sang enum
    public static String fromField(String field) {
        for (SortFieldEnum e : SortFieldEnum.values()) {
            if (e.getField().equalsIgnoreCase(field)) {
                return e.getDbField(); // trả về luôn dbField
            }
        }
        throw new IllegalArgumentException("No enum constant with field " + field);
    }

}
