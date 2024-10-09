package com.sunofdawn.semvertools.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperateType {
    EQUIP("=", true),
    LITTlE("<", true),
    GREATER(">", true),
    LITTlE_THAN("≤", true),
    GREATER_THAN("≥", true),

    TILDE("~", false),
    CARET("^", false),
    PARENTHESES_OPEN("(", false),
    PARENTHESES_CLOSE(")", false),
    BRACKET_OPEN("[", false),
    BRACKET_CLOSE("]", false),
    OR("|", false),
    COMMA(",", false);

    // 操作符枚举字符串长度固定为1位, 如果目标符号长度大于1位, 使用一个专用的符号来代替
    // example: >= -> ≥, || -> |, == -> =
    private final String str;
    private final boolean calcAble;

    public static OperateType parseByStr(String value) {
        for (OperateType operateType : values()) {
            if (operateType.str.equals(value)) {
                return operateType;
            }
        }
        return null;
    }
}
