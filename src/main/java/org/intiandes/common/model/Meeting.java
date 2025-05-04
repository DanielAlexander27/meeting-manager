package org.intiandes.common.model;

import org.intiandes.central.repository.MeetingRepository;

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
    private final Long endTimeTimes;

    public Meeting(String topic, List<String> guestEmployees, String place, Long startTimeTimestamp, Long endTimeTimes) {
        this(0, topic, guestEmployees, place, startTimeTimestamp, endTimeTimes);
    }

    public Meeting(int id, String topic, List<String> guestEmployees, String place, Long startTimeTimestamp, Long endTimeTimes) {
        this.id = id;
        this.topic = topic;
        this.guestEmployees = guestEmployees;
        this.place = place;
        this.startTimeTimestamp = startTimeTimestamp;
        this.endTimeTimes = endTimeTimes;
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

    public Long getEndTimeTimes() {
        return endTimeTimes;
    }
}
