package com.sih.traffic.domain.enums;

/**
 * Physical track type of a section between two stations.
 * SINGLE  - one line shared by both directions; mutually exclusive occupancy.
 * DOUBLE  - one line per direction; opposing trains do not conflict.
 */
public enum SectionType {
    SINGLE,
    DOUBLE
}
