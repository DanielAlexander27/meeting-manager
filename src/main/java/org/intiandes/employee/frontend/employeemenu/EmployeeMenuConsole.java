package org.intiandes.employee.frontend.employeemenu;

import org.intiandes.common.model.Meeting;
import org.intiandes.employee.EmployeeMain;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class EmployeeMenuConsole {
    private static final String dateTimePattern = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);

    private final EmployeeController controller;

    public EmployeeMenuConsole(EmployeeController controller) {
        this.controller = controller;
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void showWelcome() {
        System.out.println("          ==========================================          ");
        System.out.println("             MEETING MANAGEMENT SYSTEM - EMPLOYEE             ");
        System.out.println("          ==========================================          ");
        System.out.println(
                String.format("Welcome %s (username: %s)!", EmployeeMain.EMPLOYEE_NAME, EmployeeMain.EMPLOYEE_USERNAME)
        );
    }

    public void showMenu() {
        int choice = -1;

        while (choice != 7) {
            System.out.println("\n======== MAIN MENU ========");
//            System.out.println("Employee: " + EmployeeMain.EMPLOYEE_NAME);
            System.out.println("1. View scheduled meetings");
            System.out.println("2. Create a new meeting");
            System.out.println("3. Modify a meeting");
            System.out.println("4. Sync meetings with the server");
            System.out.println("5. Retrieve past meetings");
            System.out.println("6. Exit");
            System.out.print("Select an option: ");

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                System.out.println();
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 6.");
                continue;
            }

            switch (choice) {
                case 1 -> viewMyMeetings();
                case 2 -> createMeeting();
                case 3 -> modifyMeeting();
                case 4 -> syncMeetings();
                case 5 -> retrieveMeetings();
                case 6 -> exitSystem();
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void writeMeetingsToFile(List<Meeting> meetings, String fileName) {
        if (meetings == null || meetings.isEmpty()) {
            System.out.println("No meetings to write to the file.");
            return;
        }

        File file = new File(fileName);
        File parentDir = file.getParentFile();

        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        // Usar CustomObjectOutputStream para archivos existentes
        try {
            ObjectOutputStream oos;
            if (file.exists() && file.length() > 0) {
                oos = new CustomObjectOutputStream(new FileOutputStream(file, true));
            } else {
                oos = new ObjectOutputStream(new FileOutputStream(file));
            }

            try (oos) {
                for (Meeting meeting : meetings) {
                    oos.writeObject(meeting);
                }
                oos.flush();
                System.out.println("Meetings have been serialized and written to " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Error writing meetings to file: " + e.getMessage());
        }
    }

    // Clase CustomObjectOutputStream necesaria para agregar al archivo existente
    private static class CustomObjectOutputStream extends ObjectOutputStream {
        public CustomObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            // No escribir el encabezado para evitar InvalidClassException
        }
    }

    private static void retrieveMeetings() {
        // Definir las posibles rutas donde podría estar el archivo
        String[] possiblePaths = {
                "server-output/meetings.txt",
                "/app/server-output/meetings.txt",
                "../server-output/meetings.txt"
        };

        File serializedFile = null;
        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists()) {
                serializedFile = file;
                break;
            }
        }

        System.out.println("\nRetrieving meetings from storage:");

        if (serializedFile == null || !serializedFile.exists()) {
            System.out.println("No meetings file found. Checked paths:");
            for (String path : possiblePaths) {
                System.out.println(" - " + new File(path).getAbsolutePath());
            }
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializedFile))) {
            List<Meeting> meetings = new ArrayList<>();

            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof Meeting meeting) {
                        // Solo mostrar reuniones relacionadas con el empleado actual
                        if (meeting.getGuestEmployees().contains(EmployeeMain.EMPLOYEE_USERNAME) ||
                                meeting.getOrganizerName().equals(EmployeeMain.EMPLOYEE_USERNAME)) {
                            meetings.add(meeting);
                        }
                    }
                } catch (EOFException e) {
                    break; // Fin del archivo
                }
            }

            if (meetings.isEmpty()) {
                System.out.println("No meetings found for " + EmployeeMain.EMPLOYEE_NAME);
                return;
            }

            // Ordenar las reuniones por fecha de inicio
            meetings.sort((m1, m2) -> m1.getStartTimeTimestamp().compareTo(m2.getStartTimeTimestamp()));

            // Mostrar las reuniones usando el método toString de Meeting
            System.out.println("\nYour meetings:");
            System.out.println(Meeting.getMeetingsString(meetings));

        } catch (StreamCorruptedException e) {
            System.err.println("Error: El archivo de reuniones está corrupto o mal formado.");
            e.printStackTrace();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading meetings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewMyMeetings() {
        System.out.println("\nRetrieving your scheduled meetings from server...");

        List<Meeting> myMeetings = controller.getMyMeetings();

        if (myMeetings.isEmpty()) {
            System.out.println("There are no meetings associated with you!");
            return;
        }

        // Ordenar las reuniones por fecha de inicio
        myMeetings.sort((m1, m2) -> m1.getStartTimeTimestamp().compareTo(m2.getStartTimeTimestamp()));

        // Mostrar las reuniones
        System.out.println("\nYour scheduled meetings:");
        System.out.println(Meeting.getMeetingsString(myMeetings));
    }

    public void createMeeting() {
        System.out.println("===== Create Meeting =====");

        System.out.print("Meeting topic: ");
        String topic = scanner.nextLine();

        System.out.print("Meeting place: ");
        String place = scanner.nextLine();

        LocalDateTime startDateTime;

        while (true) {
            System.out.print("Start date and time (" + dateTimePattern + "): ");
            String startDateTimeInput = scanner.nextLine();
            LocalDateTime localDateTime = validateDateTimeInput(startDateTimeInput);

            if (localDateTime != null) {
                startDateTime = localDateTime;
                break;
            }

            System.err.println("\nError: Invalid date and time format. Try again");
            System.err.println("Valid example: 2025-05-04 10:30:00");
        }

        LocalDateTime endDateTime;

        while (true) {
            System.out.print("End date and time (" + dateTimePattern + "): ");
            String endDateTimeInput = scanner.nextLine();
            LocalDateTime localDateTime = validateDateTimeInput(endDateTimeInput);

            if (localDateTime != null) {
                endDateTime = localDateTime;

                if (endDateTime.isBefore(startDateTime)) {
                    System.err.println("\nError: The end date and time must be after the start date and time.");
                    continue;
                }

                break;
            }

            System.err.println("\nError: Invalid date and time format. Try again");
            System.err.println("Valid example: 2025-05-04 10:30:00");
        }

        System.out.println("\nNow, you will see the list of all the employees you can invite to the meeting: ");
        List<String> employeeNames = controller.getEmployeeNames();

        for (String employeeName : employeeNames) {
            System.out.println("\t -" + employeeName);
        }

        System.out.println();

        Set<String> guestEmployees = new HashSet<>();
        System.out.print("Enter guest employees (comma-separated and with the same values you saw above): ");
        String guestsInput = scanner.nextLine();

        if (!guestsInput.isEmpty()) {
            String[] guests = guestsInput.split(",");
            for (String guest : guests) {
                if (employeeNames.contains(guest)) {
                    guestEmployees.add(guest.trim());
                }
            }
        }
        guestEmployees.add(EmployeeMain.EMPLOYEE_USERNAME);

        System.out.println("\nCreating meeting...\n");

        Meeting meeting = new Meeting(
                topic,
                guestEmployees,
                place,
                startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond(),
                endDateTime.atZone(ZoneId.systemDefault()).toEpochSecond(),
                EmployeeMain.EMPLOYEE_USERNAME
        );

        controller.sendCreateMeetingRequest(meeting);
    }

    private LocalDateTime validateDateTimeInput(String dateTimeInput) {
        try {
            return LocalDateTime.parse(dateTimeInput, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void modifyMeeting() {
        String serializedFileName = "meetings_serialized_" + EmployeeMain.EMPLOYEE_USERNAME + ".txt";
        File serializedFile = new File(serializedFileName);

        System.out.println("\nModifying a meeting for " + EmployeeMain.EMPLOYEE_NAME + ":");

        if (!serializedFile.exists()) {
            System.out.println("No serialized meetings found.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializedFile))) {
            List<Meeting> meetings = (List<Meeting>) ois.readObject();

            if (meetings.isEmpty()) {
                System.out.println("No meetings found.");
                return;
            }

            // Display the list of meetings
            System.out.println("\nAvailable meetings:");
            for (int i = 0; i < meetings.size(); i++) {
                Meeting meeting = meetings.get(i);
                System.out.printf("%d. Topic: %s, Place: %s, Start Time: %s, End Time: %s\n",
                        i + 1,
                        meeting.getTopic(),
                        meeting.getPlace(),
                        LocalDateTime.ofEpochSecond(meeting.getStartTimeTimestamp(), 0, ZoneOffset.UTC),
                        LocalDateTime.ofEpochSecond(meeting.getEndTimeTimestamp(), 0, ZoneOffset.UTC)
                );
            }

            // Let the user select a meeting to modify
            System.out.print("\nEnter the number of the meeting you want to modify: ");
            int meetingIndex;
            try {
                meetingIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                return;
            }

            if (meetingIndex < 0 || meetingIndex >= meetings.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            Meeting selectedMeeting = meetings.get(meetingIndex);

            // Modify the meeting details
            System.out.println("\nModifying meeting: " + selectedMeeting.getTopic());

            System.out.print("Enter new topic (leave blank to keep current: " + selectedMeeting.getTopic() + "): ");
            String newTopic = scanner.nextLine().trim();
            if (newTopic.isEmpty()) {
                newTopic = selectedMeeting.getTopic();
            }

            System.out.print("Enter new place (leave blank to keep current: " + selectedMeeting.getPlace() + "): ");
            String newPlace = scanner.nextLine().trim();
            if (newPlace.isEmpty()) {
                newPlace = selectedMeeting.getPlace();
            }

            LocalDateTime newStartDateTime = selectedMeeting.getStartTimeTimestamp() != null
                    ? LocalDateTime.ofEpochSecond(selectedMeeting.getStartTimeTimestamp(), 0, ZoneOffset.UTC)
                    : null;
            while (true) {
                System.out.print("Enter new start date and time (" + dateTimePattern + ", leave blank to keep current: " + newStartDateTime + "): ");
                String startDateTimeInput = scanner.nextLine().trim();
                if (startDateTimeInput.isEmpty()) {
                    break;
                }
                LocalDateTime parsedDateTime = validateDateTimeInput(startDateTimeInput);
                if (parsedDateTime != null) {
                    newStartDateTime = parsedDateTime;
                    break;
                }
                System.out.println("Invalid date and time format. Please try again.");
            }

            LocalDateTime newEndDateTime = selectedMeeting.getEndTimeTimestamp() != null
                    ? LocalDateTime.ofEpochSecond(selectedMeeting.getEndTimeTimestamp(), 0, ZoneOffset.UTC)
                    : null;
            while (true) {
                System.out.print("Enter new end date and time (" + dateTimePattern + ", leave blank to keep current: " + newEndDateTime + "): ");
                String endDateTimeInput = scanner.nextLine().trim();
                if (endDateTimeInput.isEmpty()) {
                    break;
                }
                LocalDateTime parsedDateTime = validateDateTimeInput(endDateTimeInput);
                if (parsedDateTime != null) {
                    if (newStartDateTime != null && parsedDateTime.isBefore(newStartDateTime)) {
                        System.out.println("End date and time must be after the start date and time.");
                        continue;
                    }
                    newEndDateTime = parsedDateTime;
                    break;
                }
                System.out.println("Invalid date and time format. Please try again.");
            }

            System.out.println("Current guests: " + String.join(", ", selectedMeeting.getGuestEmployees()));
            System.out.print("Enter new guest employees (comma-separated, leave blank to keep current): ");
            String newGuestsInput = scanner.nextLine().trim();
            Set<String> newGuestEmployees = new HashSet<>();
            if (!newGuestsInput.isEmpty()) {
                String[] guests = newGuestsInput.split(",");
                for (String guest : guests) {
                    newGuestEmployees.add(guest.trim());
                }
            } else {
                newGuestEmployees = selectedMeeting.getGuestEmployees();
            }

            // Create the updated meeting object
            Meeting updatedMeeting = new Meeting(
                    selectedMeeting.getId(),
                    newTopic,
                    newGuestEmployees,
                    newPlace,
                    newStartDateTime != null ? newStartDateTime.toEpochSecond(ZoneOffset.UTC) : selectedMeeting.getStartTimeTimestamp(),
                    newEndDateTime != null ? newEndDateTime.toEpochSecond(ZoneOffset.UTC) : selectedMeeting.getEndTimeTimestamp(),
                    selectedMeeting.getOrganizerName()
            );

            // Update the meeting in the list
            meetings.set(meetingIndex, updatedMeeting);

            // Save the updated meetings list back to the serialized file
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serializedFile))) {
                oos.writeObject(meetings);
                System.out.println("Meeting updated successfully!");
            }

            // Send the updated meeting to the server
            controller.sendCreateMeetingRequest(updatedMeeting);

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error modifying meeting: " + e.getMessage());
        }
    }

    private static void syncMeetings() {
        String fileName = "meetings_" + EmployeeMain.EMPLOYEE_NAME + ".txt";

//        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
//
//            out.println("SYNC " + employeeName);
//
//            String line;
//            while ((line = in.readLine()) != null) {
//                writer.write(line + "\n");
//                if (line.equals("EOF")) break;
//            }
//            System.out.println("Meetings successfully synced with the server.");
//
//        } catch (IOException e) {
//            System.err.println("Error syncing with the central server: " + e.getMessage());
//        }
    }

    private static void exitSystem() {
        System.out.println("\nLogging out...");
        System.out.println("Goodbye, " + EmployeeMain.EMPLOYEE_NAME + "!");
    }
}