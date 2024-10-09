package com.sunofdawn.semvertools;

import com.sunofdawn.semvertools.model.Priority;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RangeGroup {
    private List<Range> ranges;
    private Priority priority;

    public boolean isEmptyRange() {
        return ranges.isEmpty();
    }

    public Semver choiceVersion(List<Semver> versions, boolean needStability) {
        // make sure versions has been sorted
//        versions.sort(Semver::compareTo);
        Semver candidate = null;
        for (Semver current : versions) {
            if (ranges.stream().allMatch(r -> r.isSatisfy(current))) {
                if (candidate == null) {
                    candidate = current;
                } else {
                    if (Priority.GRATE_FIRST.equals(priority) && (!needStability || current.isStable())) {
                        candidate = current;
                    }
                }
            }
        }
        return candidate;
    }
}
