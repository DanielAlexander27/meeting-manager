package org.intiandes.central.repository.meeting;

import org.intiandes.common.model.Meeting;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeetingRepositoryMemoryImpl implements MeetingRepository {
    private static int currentId = 0;
    private final Map<Integer, Meeting> meetingsTable = new HashMap<>();
    private ObjectOutputStream objectOutputStream;

    public MeetingRepositoryMemoryImpl() {
        loadMeetingsFromFile();

        try {
            setObjectOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Meeting createMeeting(Meeting meeting) {
        currentId++;
        meetingsTable.put(currentId, new Meeting(currentId, meeting));
        final Meeting meetingCreated = meetingsTable.get(currentId);
        saveMeeting(meetingCreated);
        return meetingCreated;
    }

    @Override
    public Meeting updateMeeting(Meeting meetingToUpdate) {
        if (meetingsTable.containsKey(meetingToUpdate.getId())) {
            meetingsTable.put(meetingToUpdate.getId(), meetingToUpdate);
            saveAllMeetings();
            return meetingsTable.get(meetingToUpdate.getId());
        } else {
            throw new IllegalArgumentException("Meeting with id " + meetingToUpdate.getId() + " does not exist!");
        }
    }

    @Override
    public List<Meeting> getMeetingsByEmployeeName(String employeeName) {
        final List<Meeting> employeeAsociatedMeetings = new ArrayList<>();

        for (Meeting meeting : meetingsTable.values()) {
            if (meeting.getGuestEmployees().contains(employeeName)) {
                employeeAsociatedMeetings.add(meeting);
            }
        }

        return employeeAsociatedMeetings;
    }

    private void saveMeeting(Meeting meeting) {
        try {
            objectOutputStream.writeObject(meeting);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Clears all the content from the .txt file and writes all the meetings again.
     */
    private void saveAllMeetings() {
        try (FileOutputStream ignored = new FileOutputStream(getStorageFile(), false)) {
            setObjectOutputStream();
            for (Meeting meeting : meetingsTable.values()) {
                objectOutputStream.writeObject(meeting);
            }
            objectOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadMeetingsFromFile() {
        currentId = 1;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getStorageFile()))) {
            while (true) {
                final Object object = ois.readObject();

                if (object instanceof Meeting meeting) {
                    meetingsTable.put(meeting.getId(), meeting);
                    currentId++;
                }
            }

        } catch (EOFException e) {
            System.out.println("End of file detected!");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (meetingsTable.isEmpty()) {
            currentId = 0;
        }
    }

    private File getStorageFile() throws IOException {
        String storePath = "server-output/meetings.txt";
        File storageFile = new File(storePath);
        File parentDir = storageFile.getParentFile();

        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        storageFile.createNewFile();
        return storageFile;
    }

    public void close() {
        try {
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setObjectOutputStream() throws IOException {
        File storageFile = getStorageFile();

        FileOutputStream fileOutputStream = new FileOutputStream(getStorageFile(), true);
        this.objectOutputStream = storageFile.length() == 0
                ? new ObjectOutputStream(fileOutputStream)
                : new CustomObjectOutputStream(fileOutputStream);
    }

    private static class CustomObjectOutputStream extends ObjectOutputStream {
        public CustomObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() {
        }
    }
}
