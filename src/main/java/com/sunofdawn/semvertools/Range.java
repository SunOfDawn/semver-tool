package com.sunofdawn.semvertools;

import com.sunofdawn.semvertools.model.OperateType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Range {
    private final Semver version;
    private final OperateType op;

    public boolean isSatisfy(Semver other) {
        if (!version.isValid()) {
            return false;
        }
        switch (op) {
            case EQUIP:
                return other.isEquipTo(version);
            case LITTlE:
                return other.isLetterThan(version);
            case GREATER:
                return other.isGreaterThan(version);
            case LITTlE_THAN:
                return other.isLetterThan(version) || other.isEquipTo(version);
            case GREATER_THAN:
                return other.isGreaterThan(version) || other.isEquipTo(version);
            default:
                return false;
        }
    }
}
