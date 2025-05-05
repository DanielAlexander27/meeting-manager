package org.intiandes.common.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Meeting implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String topic;
    private final List<String> guestEmployees;
    private final String place;
    private final Long startTimeTimestamp;
    private final Long endTimeTimestamp;
    private final String organizerName;

    public Meeting(String topic, List<String> guestEmployees, String place, Long startTimeTimestamp, Long endTimeTimestamp, String organizerName) {
        this(0, topic, guestEmployees, place, startTimeTimestamp, endTimeTimestamp, organizerName);
    }

    public Meeting(int id, String topic, List<String> guestEmployees, String place, Long startTimeTimestamp, Long endTimeTimestamp, String organizerName) {
        this.id = id;
        this.topic = topic;
        this.guestEmployees = guestEmployees;
        this.place = place;
        this.startTimeTimestamp = startTimeTimestamp;
        this.endTimeTimestamp = endTimeTimestamp;
        this.organizerName = organizerName;
    }

    public Meeting(int id, Meeting other) {
        this(id, other.getTopic(), other.getGuestEmployees(), other.getPlace(), other.getStartTimeTimestamp(), other.getEndTimeTimestamp(), other.organizerName);
    }

    public int getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public List<String> getGuestEmployees() {
        return guestEmployees;
    }

    public String getPlace() {
        return place;
    }

    public Long getStartTimeTimestamp() {
        return startTimeTimestamp;
    }

    public Long getEndTimeTimestamp() {
        return endTimeTimestamp;
    }

    public String getOrganizerName() {
        return organizerName;
    }
}
