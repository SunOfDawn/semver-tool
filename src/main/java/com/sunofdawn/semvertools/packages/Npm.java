package com.sunofdawn.semvertools.packages;

import com.sunofdawn.semvertools.Range;
import com.sunofdawn.semvertools.RangeGroup;
import com.sunofdawn.semvertools.Semver;
import com.sunofdawn.semvertools.model.OperateType;
import com.sunofdawn.semvertools.model.Priority;

import java.util.Arrays;

public class Npm extends Operator {
    // https://www.xncoding.com/2018/05/07/web/npm-version.html

    @Override
    protected RangeGroup parseCaret(Semver semver) {
        // 版本号中最左边的非0数字的右侧可以任意(即范围为最高非0版本号+1以内的版本), 如果所有数字都是0, 取值范围为最后一位版本号+1以内的版本
        // ^0, 表示 >=0.0.0 <1.0.0
        // ^0.0, 表示 >=0.0.0 <0.1.0
        // ^0.2.3 ，表示>=0.2.3 <0.3.0
        // ^1.1.2 ，表示>=1.1.2 <2.0.0

        Semver highest;
        String[] parts = semver.getOriginVersion().split("\\.");
        if (semver.getMajor() == 0) {
            if (parts.length == 1) {
                // ^0，表示 >=0.0.0 <1.0.0
                highest = semver.nextMajor(1);
            } else if (parts.length == 2) {
                // ^0.0，表示 >=0.0.0 <0.1.0
                // ^0.2.3 ，表示>=0.2.3 <0.3.0
                highest = semver.nextMinor(1);
            } else {
                highest = semver.nextPatch(1);
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
        // 最后一个位指定的版本号可以任意
        // ~1 / ~0 表示 *
        // ~1.2 表示 >=1.2 <2.0.0
        // ~1.2.3 表示 >=1.2.3 <1.3.0
        String[] parts = semver.getOriginVersion().split("\\.");
        Semver highest;
        if (parts.length == 1) {
            return new RangeGroup(Arrays.asList(new Range(semver.currentPatch(), OperateType.GREATER_THAN)), Priority.GRATE_FIRST);
            // ~1 表示 *
        } else if (parts.length == 2) {
            // ~1.2 表示 >=1.2 <2.0.0
            highest = semver.nextMajor(1);
        } else {
            highest = semver.nextMinor(1);
        }
        return new RangeGroup(Arrays.asList(
                new Range(semver.currentPatch(), OperateType.GREATER_THAN),
                new Range(highest, OperateType.LITTlE)
        ), Priority.GRATE_FIRST);
    }
}
