package com.sunofdawn.semvertools.packages;

import com.sunofdawn.semvertools.Range;
import com.sunofdawn.semvertools.RangeGroup;
import com.sunofdawn.semvertools.Semver;
import com.sunofdawn.semvertools.SemverParser;
import com.sunofdawn.semvertools.model.OperateType;
import com.sunofdawn.semvertools.model.Priority;

import java.util.Arrays;

public class Cargo extends Operator {
    // https://blog.itdevwu.com/po#

    @Override
    protected RangeGroup parseCaret(Semver semver) {
        // 范围到版本号中最左边的非0数字的右侧一位+1, 若未找到, 最后一位+1
        String[] parts = semver.getOriginVersion().split("\\.");
        Semver highest;
        if (semver.getMajor() == 0) {
            if (parts.length == 2) {
                // ^0 := >=0.0.0 <1.0.0
                highest = new Semver(semver.getOriginVersion(), 1, 0, 0);
            } else if (semver.getMinor() == 0) {
                if (parts.length == 3) {
                    // ^0.0 := >=0.0.0 <0.1.0
                    highest = new Semver(semver.getOriginVersion(), 0, 1, 0);
                } else {
                // ^0.0.3 := >=0.0.3 <0.0.4
                highest = semver.nextPatch(1);
                }
            } else {
                // ^0.2.3 := >=0.2.3 <0.3.0
                highest = semver.nextMinor(1);
            }
        } else {
            highest = semver.nextMajor(1);
        }
        return new RangeGroup(Arrays.asList(
                new Range(semver.currentPatch(), OperateType.GREATER_THAN),
                new Range(highest, OperateType.LITTlE)
        ), Priority.GRATE_FIRST);
    }

    @Override
    protected RangeGroup parseTilde(Semver semver) {
        // 只接受最小范围的版本
        // md5 ="~0" <==> [0.0.0---1.0.0 (不包含)]
        // md5 ="~0.6" <==> [0.6.0---0.7.0 (不包含)]
        // md5 ="~0.6.1" <==> [0.6.0---0.7.0 (不包含)]
        String[] parts = semver.getOriginVersion().split("\\.");
        Semver highest;
        if (parts.length == 1) {
            // ~1 等价于 >= 1.0.0 < 2.0.0
            highest = semver.nextMajor(1);
        } else {
            // ~1.2 等价于 >=1.2 <1.3.0
            // ~1.2.3等价于>=1.2.3 <1.3.0
            highest = semver.nextMinor(1);
        }
        return new RangeGroup(Arrays.asList(
                new Range(semver.currentPatch(), OperateType.GREATER_THAN),
                new Range(highest, OperateType.LITTlE)
        ), Priority.GRATE_FIRST);
    }

    @Override
    protected RangeGroup parseBaseVersion(String expression) {
        // 不包含符号的情况，同^语义相同
        return parseCaret(SemverParser.parse(expression));
    }
}
