package com.sunofdawn.semvertools.packages;

import com.sunofdawn.semvertools.Range;
import com.sunofdawn.semvertools.RangeGroup;
import com.sunofdawn.semvertools.Semver;
import com.sunofdawn.semvertools.model.OperateType;
import com.sunofdawn.semvertools.model.Priority;

import java.util.ArrayList;
import java.util.Arrays;

public class Pip extends Operator {
    // https://peps.python.org/pep-0440

    @Override
    protected RangeGroup parseCaret(Semver semver) {
        return new RangeGroup(new ArrayList<>(), Priority.NORMAL);
    }

    @Override
    protected RangeGroup parseTilde(Semver semver) {
        // 匹配预期与指定版本兼容的任何候选版本，即最后一位指定版本可是大于等于该数值的任意值
        // ~= 2.2 -> >= 2.2, < 3.0
        // ~= 1.4.5a4 -> >= 1.4.5, < 1.5.0
        // ~= 1.4.5.0 -> >= 1.4.5.0, == 1.4.5.*
        String[] parts = semver.getOriginVersion().split("\\.");
        Semver highest;
        if (parts.length == 1) {
            // 仅包含major的版本不可用于此表达式
            return new RangeGroup(new ArrayList<>(), Priority.NORMAL);
        } else if (parts.length == 2) {
            highest = semver.nextMajor(1);
        } else if (parts.length == 3) {
            highest = semver.nextMinor(1);
        } else {
            highest = semver.nextPatch(1);
        }
        return new RangeGroup(Arrays.asList(
                new Range(semver.currentPatch(), OperateType.GREATER_THAN),
                new Range(highest, OperateType.LITTlE)
        ), Priority.GRATE_FIRST);
    }
}
