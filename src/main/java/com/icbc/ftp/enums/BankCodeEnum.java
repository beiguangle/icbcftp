package com.icbc.ftp.enums;

public enum BankCodeEnum implements EnumCodeList.CodeListItem{

     ICBC("icbc", "中国工商银行"),

    RCB("rcb", "农村信用社"),

    PSBC("psbc","中国邮政储蓄银行");

    private String value = null;
    private String code = null;

    private BankCodeEnum(String _code, String _value) {
        this.value = _value;
        this.code = _code;
    }

    public static BankCodeEnum getEnumByKey(String key) {
        for (BankCodeEnum e : BankCodeEnum.values()) {
            if (e.getCode().equals(key)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getValue() {
        return value;
    }
}
