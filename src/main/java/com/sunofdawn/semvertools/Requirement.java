package com.sunofdawn.semvertools;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Requirement {
    private String expression;
    private List<RangeGroup> rangeGroups;

    public Semver choiceVersion(List<Semver> versions, boolean needStability) {
        versions.sort(Semver::compareTo);
        Semver candidate = null;
        for (RangeGroup rangeGroup : rangeGroups) {
            if (rangeGroup.isEmptyRange()) {
                continue;
            }

            candidate = rangeGroup.choiceVersion(versions, needStability);
            if (candidate != null) {
                break;
            }
        }
        return candidate;
    }
}
