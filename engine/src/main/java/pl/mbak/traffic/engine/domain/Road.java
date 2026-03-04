package pl.mbak.traffic.engine.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Road {
    north, south, east, west;

    @JsonCreator
    public static Road fromString(String value) {
        return Road.valueOf(value.toLowerCase());
    }
}
