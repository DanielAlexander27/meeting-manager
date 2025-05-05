package org.intiandes.common.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

public class Meeting implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String topic;
    private final Set<String> guestEmployees;
    private final String place;
    private final Long startTimeTimestamp;
    private final Long endTimeTimestamp;
    private final String organizerName;

    public Meeting(String topic, Set<String> guestEmployees, String place, Long startTimeTimestamp, Long endTimeTimestamp, String organizerName) {
        this(0, topic, guestEmployees, place, startTimeTimestamp, endTimeTimestamp, organizerName);
    }

    public Meeting(String topic, Set<String> guestEmployees, String place, LocalDateTime startDateTime, LocalDateTime endDateTime, String organizerName) {
        this(0, topic, guestEmployees, place, startDateTime, endDateTime, organizerName);
    }

    public Meeting(int id, String topic, Set<String> guestEmployees, String place, Long startTimeTimestamp, Long endTimeTimestamp, String organizerName) {
        this.id = id;
        this.topic = topic;
        this.guestEmployees = guestEmployees;
        this.place = place;
        this.startTimeTimestamp = startTimeTimestamp;
        this.endTimeTimestamp = endTimeTimestamp;
        this.organizerName = organizerName;
    }

    public Meeting(int id, String topic, Set<String> guestEmployees, String place, LocalDateTime startDateTime, LocalDateTime endDateTime, String organizerName) {
        this(id, topic, guestEmployees, place, toEpochSecond(startDateTime), toEpochSecond(endDateTime), organizerName);
    }

    public Meeting(int id, Meeting other) {
        this(id, other.getTopic(), other.getGuestEmployees(), other.getPlace(), other.getStartTimeTimestamp(), other.getEndTimeTimestamp(), other.getOrganizerName());
    }

    public int getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public Set<String> getGuestEmployees() {
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

    public LocalDateTime getStartTime() {
        return toLocalDateTime(startTimeTimestamp);
    }

    public LocalDateTime getEndTime() {
        return toLocalDateTime(endTimeTimestamp);
    }

    public static LocalDateTime toLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp),
                ZoneId.systemDefault()
        );
    }

    @Override
    public String toString() {
        return "Topic: " + topic + "\n" +
                "Place: " + place + "\n" +
                "Start time: " + getStartTime() + "\n" +
                "End time: " + getEndTime() + "\n" +
                "Organizer: " + organizerName + "\n" +
                "Guests: " + String.join(", ", guestEmployees) + "\n";
    }

    public static String getMeetingsString(List<Meeting> meetings) {
        StringBuilder sb = new StringBuilder();

        for (Meeting meeting : meetings) {
            sb.append("-------------------------------------------------\n");
            sb.append(meeting.toString());
            sb.append("-------------------------------------------------\n");
        }

        return sb.toString();
    }

    private static Long toEpochSecond(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }
}
