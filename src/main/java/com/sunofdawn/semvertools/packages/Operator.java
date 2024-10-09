package com.sunofdawn.semvertools.packages;

import com.sunofdawn.semvertools.*;
import com.sunofdawn.semvertools.model.Constant;
import com.sunofdawn.semvertools.model.OperateType;
import com.sunofdawn.semvertools.model.Priority;
import com.sunofdawn.semvertools.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Operator {

    public List<RangeGroup> parseRangeGroup(String expression) {
        expression = expression.trim();
        if (isAnyVersion(expression)) {
            return Collections.singletonList(parseAnyVersion(expression));
        }

        // |分隔符将拆开后依次解析子串
        if (expression.contains(OperateType.OR.getStr())) {
            return parseOperateOr(expression);
        }

        if (isBrackets(expression)) {
            return Collections.singletonList(parseBrackets(expression));
        }

        // ,的解析顺序要放在括号判定之后，且它们的结果集将设置为交集而非并集
        if (expression.contains(OperateType.COMMA.getStr())) {
            return Collections.singletonList(parseOperateComma(expression));
        }

        return Collections.singletonList(parseIndependentExpression(expression));
    }

    protected RangeGroup parseAnyVersion(String expression) {
        return new RangeGroup(Arrays.asList(new Range(new Semver(expression), OperateType.GREATER_THAN)), Priority.GRATE_FIRST);
    }

    /**
     * 解析|表达式，结果取并集
     * @param expression 表达式
     * @return 区间组
     */
    protected List<RangeGroup> parseOperateOr(String expression) {
        List<RangeGroup> groups = new ArrayList<>();
        Arrays.stream(expression.split("\\|"))
                .filter(e -> !StringUtils.isBlank(e))
                .map(this::parseRangeGroup)
                .forEach(groups::addAll);
        return groups;
    }

    /**
     * 解析带,的表达式，结果取交集
     * @param expression 表达式
     * @return 区间组
     */
    protected RangeGroup parseOperateComma(String expression) {
        List<RangeGroup> tmp = Arrays.stream(expression.split(","))
                .filter(e -> !StringUtils.isBlank(e))
                .map(this::parseIndependentExpression)
                .collect(Collectors.toList());
        RangeGroup mergedGroup = new RangeGroup(new ArrayList<>(), Priority.NORMAL);
        tmp.forEach(group -> mergeOtherRangeGroup(mergedGroup, group));
        return mergedGroup;
    }

    /**
     * 解析带括号的表达式
     *
     * @param expression 表达式
     * @return 区间组
     */
    protected RangeGroup parseBrackets(String expression) {
        String[] partsWithoutBracket = expression.substring(1, expression.length() - 1).split(OperateType.COMMA.getStr());
        // example: (2)
        if (partsWithoutBracket.length == 1) {
            return new RangeGroup(Collections.singletonList(new Range(SemverParser.parse(partsWithoutBracket[0]), OperateType.EQUIP)), Priority.NORMAL);
        }
        // example: (2,3,4)
        if (partsWithoutBracket.length > 2) {
            return new RangeGroup(new ArrayList<>(), Priority.NORMAL);
        }

        String left = partsWithoutBracket[0].trim();
        String right = partsWithoutBracket[1].trim();
        Priority priority = Priority.LITTLE_FIRST;
        List<Range> ranges = new ArrayList<>();
        if (StringUtils.isBlank(left)) {
            // example: (,5.0) 将解析到最小的可接受的稳定版本
            ranges.add(new Range(new Semver(expression), OperateType.GREATER_THAN));
        } else if (left.contains("*")) {
            return new RangeGroup(new ArrayList<>(), Priority.NORMAL);
        } else {
            // 将解析到最小的可接受的稳定版本
            Semver s = SemverParser.parse(left);
            if (s.isValid()) {
                ranges.add(new Range(s, OperateType.parseByStr(expression.substring(0, 1))));
            } else {
                return new RangeGroup(new ArrayList<>(), Priority.NORMAL);
            }
        }

        // example: [2.1.0, )
        if (right.contains("*")) {
            return new RangeGroup(new ArrayList<>(), Priority.NORMAL);
        } else if (!StringUtils.isBlank(right)) {
            Semver s = SemverParser.parse(left);
            if (s.isValid()) {
                ranges.add(new Range(s, OperateType.parseByStr(expression.substring(expression.length() - 1))));
                if (StringUtils.isBlank(left)) {
                    priority = Priority.GRATE_FIRST;
                }
            } else {
                return new RangeGroup(new ArrayList<>(), Priority.NORMAL);
            }
        }
        return new RangeGroup(ranges, priority);
    }

    /**
     * 解析拆分过后的独立表达式
     * @param expression 表达式
     * @return 区间组
     */
    protected RangeGroup parseIndependentExpression(String expression) {
        // 如果有 >, >= 之类的比较符，则可能包含多组数据(example: 0.x >=0.0.4)，按空格拆分后分段解析
        List<String> expressionParts = Arrays.stream(expression.trim().split(" "))
                .filter(p -> !StringUtils.isBlank(p))
                .map(String::trim)
                .collect(Collectors.toList());

        // 如果拆分后发现存在单独的比较符(example: >= 3.1)，尝试去掉空格后解析
        if (expressionParts.stream().anyMatch(this::isStartWithOperate)) {
            expressionParts = Arrays.asList(String.join("", expressionParts));
        }

        // # 部分字符串与版本号合并后意义会改变，加上空格尝试解析
        if (expressionParts.stream().anyMatch(Constant.BLACK_REQUIRED_CHARS::contains)) {
            expressionParts = Arrays.asList(String.join(" ", expressionParts));
        }

        RangeGroup group = new RangeGroup(new ArrayList<>(), Priority.NORMAL);
        for (String part : expressionParts) {
            RangeGroup result;
            OperateType op = OperateType.parseByStr(part.substring(0, 1));
            if (op == null) {
                if (part.contains(".*") || part.contains(".x")) {
                    result = parseStarPart(part);
                } else {
                    result = parseBaseVersion(part);
                }
            } else if (op.isCalcAble()) {
                result = parseBasicOperate(part, op);
            } else if (OperateType.CARET.equals(op) || OperateType.TILDE.equals(op)) {
                result = parseComplexOperate(part, op);
            } else if (part.contains(" - ")) {
                result = parseHyphenate(part);
            } else {
                result = null;
            }

            // 合并在独立表达式里的所有组合，取并集集合
            if (result != null) {
                mergeOtherRangeGroup(group, result);
            }
        }
        return group;
    }

    /**
     * 解析基本表达式
     *
     * @param expression 表达式
     * @return 区间组
     */
    protected RangeGroup parseBaseVersion(String expression) {
        return new RangeGroup(Arrays.asList(new Range(SemverParser.parse(expression), OperateType.EQUIP)), Priority.NORMAL);
    }

    /**
     * 解析基本比较符表达式
     *
     * @param expression 表达式
     * @return 区间组
     */
    protected RangeGroup parseBasicOperate(String expression, OperateType op) {
        String baseVersion = expression.substring(op.getStr().length());
        if (OperateType.EQUIP.equals(op)) {
            // ==3.4.* -> >=3.4.0
            if (baseVersion.contains(".*") || baseVersion.contains(".x")) {
                return parseStarPart(baseVersion);
            }
        }
        return new RangeGroup(Arrays.asList(new Range(SemverParser.parse(baseVersion), op)), Priority.GRATE_FIRST);
    }

    /**
     * 解析复合含义比较符
     * @param expression 表达式
     * @param op 比较符
     * @return
     */
    protected RangeGroup parseComplexOperate(String expression, OperateType op) {
        Semver semver = SemverParser.parse(expression.substring(op.getStr().length()));
        if (!semver.isValid()) {
            return new RangeGroup(new ArrayList<>(), Priority.NORMAL);
        }

        switch (op) {
            case CARET:
                return parseCaret(semver);
            case TILDE:
                return parseTilde(semver);
            default:
                return new RangeGroup(new ArrayList<>(), Priority.NORMAL);
        }
    }

    /**
     * 解析带^的表达式
     *
     * @param semver 表达式
     * @return 区间组
     */
    protected abstract RangeGroup parseCaret(Semver semver);

    /**
     * 解析带~的表达式
     *
     * @param semver 表达式
     * @return 区间组
     */
    protected abstract RangeGroup parseTilde(Semver semver);

    /**
     * 解析带 - 的表达式
     *
     * @param expression 表达式
     * @return 区间组
     */
    protected RangeGroup parseHyphenate(String expression) {
        String[] parts = expression.split(" - ");
        if (parts.length != 2) {
            return new RangeGroup(new ArrayList<>(), Priority.NORMAL);
        }
        return new RangeGroup(Arrays.asList(
                new Range(SemverParser.parse(parts[0]), OperateType.GREATER_THAN),
                new Range(SemverParser.parse(parts[1]), OperateType.LITTlE_THAN)
        ), Priority.GRATE_FIRST);
    }

    /**
     * 解析带*号的表达式, 如2.1.*
     *
     * @param expression 表达式
     * @return 区间组
     */
    protected RangeGroup parseStarPart(String expression) {
        Semver s = SemverParser.parse(expression);
        List<Range> ranges;

        String[] parts = expression.trim().split("\\.");
        if (parts[0].equals("*") || parts[0].equals("x")) {
            ranges = Arrays.asList(new Range(new Semver(expression), OperateType.GREATER));
        } else if (parts.length > 1 && (parts[1].equals("*") || parts[1].equals("x"))) {
            ranges = Arrays.asList(new Range(s.currentMajor(), OperateType.GREATER_THAN), new Range(s.nextMajor(1), OperateType.LITTlE));
        } else if (parts.length > 2 && (parts[2].equals("*") || parts[2].equals("x"))) {
            ranges = Arrays.asList(new Range(s.currentMinor(), OperateType.GREATER_THAN), new Range(s.nextMinor(1), OperateType.LITTlE));
        } else if (parts.length > 3 && (parts[3].equals("*") || parts[3].equals("x"))) {
            ranges = Arrays.asList(new Range(s, OperateType.GREATER_THAN), new Range(s.nextPatch(1), OperateType.LITTlE));
        } else {
            ranges = new ArrayList<>();
        }
        return new RangeGroup(ranges, Priority.GRATE_FIRST);
    }

    protected boolean isAnyVersion(String expression) {
        return StringUtils.isBlank(expression) || expression.equals("*") || expression.equals("latest");
    }

    protected boolean isStartWithOperate(String expression) {
        return OperateType.parseByStr(expression.substring(0, 1)) != null;
    }

    protected boolean isBrackets(String expression) {
        return (expression.startsWith(OperateType.PARENTHESES_OPEN.getStr()) || expression.startsWith(OperateType.BRACKET_OPEN.getStr()))
                && (expression.endsWith(OperateType.PARENTHESES_CLOSE.getStr()) || expression.endsWith(OperateType.BRACKET_CLOSE.getStr()));
    }

    protected void mergeOtherRangeGroup(RangeGroup group, RangeGroup other) {
        group.getRanges().addAll(other.getRanges());
        // 优先使用其他解析到的优先级
        if (group.getPriority().equals(Priority.NORMAL)) {
            group.setPriority(other.getPriority());
        }
    }
}
