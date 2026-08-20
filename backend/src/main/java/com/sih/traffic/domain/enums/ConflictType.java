package com.sih.traffic.domain.enums;

public enum ConflictType {
    /** Two trains moving in opposite directions overlap on the same SINGLE track section. */
    HEAD_ON_SINGLE_LINE,
    /** Two trains moving in the same direction overlap on the same section (implies an overtake is needed). */
    SAME_DIRECTION_OVERLAP,
    /** Two trains' halt windows overlap on the same platform. */
    PLATFORM_OVERLAP,
    /** A train is planned onto a platform whose compatibleTrainTypes does not include the train's type. */
    PLATFORM_TYPE_INCOMPATIBLE
}
