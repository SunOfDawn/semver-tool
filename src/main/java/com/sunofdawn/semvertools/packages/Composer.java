package com.sunofdawn.semvertools.packages;

import com.sunofdawn.semvertools.Range;
import com.sunofdawn.semvertools.RangeGroup;
import com.sunofdawn.semvertools.Semver;
import com.sunofdawn.semvertools.model.OperateType;
import com.sunofdawn.semvertools.model.Priority;

import java.util.Arrays;

public class Composer extends Operator {
    // https://getcomposer.org/doc/articles/versions.md
    // https://overtrue.me/about-composer-version-constraint

    @Override
    protected RangeGroup parseCaret(Semver semver) {
        // 锁定第一位不变，但当major为0时锁定minor
        // ^0.3.2 表示 >=0.3.2 <0.4.0
        if (semver.getMajor() == 0) {
            return new RangeGroup(Arrays.asList(
                    new Range(semver.currentPatch(), OperateType.GREATER_THAN),
                    new Range(semver.nextMinor(1), OperateType.LITTlE)
            ), Priority.GRATE_FIRST);
        }
        // ^1.1.2 表示 >=1.1.2 <2.0.0
        return new RangeGroup(Arrays.asList(
                new Range(semver.currentPatch(), OperateType.GREATER_THAN),
                new Range(semver.nextMajor(1), OperateType.LITTlE)
        ), Priority.GRATE_FIRST);
    }

    @Override
    protected RangeGroup parseTilde(Semver semver) {
        // 最后一个位指定的版本号可以是任意数
        // ~1 等价于 *
        // ~1.2 等价于 >=1.2 <2.0.0
        // ~1.2.3 等价于 >=1.2.3 <1.3.0
        String[] parts = semver.getOriginVersion().split("\\.");
        Semver highest;
        if (parts.length == 1) {
            return new RangeGroup(Arrays.asList(new Range(new Semver(semver.getOriginVersion()), OperateType.GREATER_THAN)), Priority.GRATE_FIRST);
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
