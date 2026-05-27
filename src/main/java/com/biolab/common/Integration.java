package com.biolab.common;

public enum Integration {
    BLAST_MOTION_API_DEMO("blast-motion-api-demo");
    // future: DIAMOND_KINETICS, TRACKMAN, RAPSODO, HEATREX, ...

    public final String id;

    Integration(String id) { this.id = id; }
}
