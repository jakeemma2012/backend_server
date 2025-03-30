package com.ProjectMovie.entities;

public enum MovieStatus {
    UPCOMING("Upcoming"),
    RELEASED("Released"),
    CANCELED("Canceled"),
    ONGOING("Ongoing");

    private final String value;

    MovieStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MovieStatus fromValue(String value) {
        for (MovieStatus status : MovieStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}