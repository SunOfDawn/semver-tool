package com.sunofdawn.semvertools.util;

import com.sunofdawn.semvertools.model.OperateType;

public class StringUtils {

    public static String prettyExpression(String expression) {
        return expression
                .trim()
                .replace("===", OperateType.EQUIP.getStr())
                .replace("==", OperateType.EQUIP.getStr())
                .replace(">=", OperateType.GREATER_THAN.getStr())
                .replace("<=", OperateType.LITTlE_THAN.getStr())
                .replace("~=", OperateType.TILDE.getStr())
                .replace("^=", OperateType.CARET.getStr())
                .replace("||", OperateType.OR.getStr())
                .toLowerCase();
    }

    public static boolean isDigit(Character ch) {
        return ch >= 48 && ch <= 57;
    }

    public static boolean isAlphaChar(Character ch) {
        return ch >= 65 && ch <= 90 || ch >= 97 && ch <= 122;
    }

    public static boolean isBlank(String value) {
        // 暂时只能识别ascii编码的空格和换行符
        int left = 0;
        for (char ch : value.toCharArray()) {
            if (ch != 40 && ch != '\t') {
                break;
            }
            left ++;
        }
        return left == value.length();
    }
}
