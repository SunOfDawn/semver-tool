package com.sunofdawn.semvertools;

import com.sunofdawn.semvertools.model.SemverType;
import com.sunofdawn.semvertools.packages.*;
import com.sunofdawn.semvertools.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequirementParser {

    private final Map<SemverType, Operator> operatorMap;

    private static volatile RequirementParser singleton;

    private RequirementParser() {
        operatorMap = new HashMap<>();
        operatorMap.put(SemverType.MAVEN, new Cargo());
        operatorMap.put(SemverType.CARGO, new Cargo());
        operatorMap.put(SemverType.COMPOSER, new Composer());
        operatorMap.put(SemverType.NPM, new Npm());
        operatorMap.put(SemverType.PIP, new Pip());
    }

    public static RequirementParser newInstance() {
        if (singleton == null) {
            synchronized (RequirementParser.class) {
                if (singleton == null) {
                    singleton = new RequirementParser();
                }
            }
        }
        return singleton;
    }

    public Requirement parse(SemverType semverType, String expression) {
        String prettyExpression = StringUtils.prettyExpression(expression);
        List<RangeGroup> rangeGroups = operatorMap.get(semverType).parseRangeGroup(prettyExpression);
        rangeGroups = rangeGroups.stream().filter(this::isValidGroup).collect(Collectors.toList());
        return new Requirement(expression, rangeGroups);
    }

    private boolean isValidGroup(RangeGroup group) {
        // 当前仅判断是否包含，区间本身的有效性不做检查
        return !group.isEmptyRange();
    }
}
