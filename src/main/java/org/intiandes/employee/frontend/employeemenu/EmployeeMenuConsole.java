package org.intiandes.employee.frontend.employeemenu;

import org.intiandes.common.model.Meeting;
import org.intiandes.common.utils.FileUtils;
import org.intiandes.employee.EmployeeMain;

import java.io.*;
import java.time.LocalDateTime;
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
            System.out.println("1. View scheduled meetings");
            System.out.println("2. Create a new meeting");
            System.out.println("3. Modify a meeting");
            System.out.println("4. Sync meetings with the server");
            System.out.println("5. Retrieve meetings from local storage.");
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
                case 5 -> retrieveMeetingsFromLocalStorage();
                case 6 -> exitSystem();
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
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
            System.out.println("\t - " + employeeName);
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
                startDateTime,
                endDateTime,
                EmployeeMain.EMPLOYEE_USERNAME
        );

        controller.sendCreateMeetingRequest(meeting);
        writeMeetingsToFile();
    }

    private LocalDateTime validateDateTimeInput(String dateTimeInput) {
        try {
            return LocalDateTime.parse(dateTimeInput, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void modifyMeeting() {
        List<Meeting> meetings = controller.getMyMeetings();

        if (meetings.isEmpty()) {
            System.out.println("You have no meetings to modify.\n");
            return;
        }

        System.out.println("===== Meeting List =====");

        for (int i = 1; i <= meetings.size(); i++) {
            final Meeting meeting = meetings.get(i - 1);
            System.out.printf("(%d) Topic: %s - Place: %s%n", i, meeting.getTopic(), meeting.getPlace());
        }

        System.out.print("\nEnter the number of the meeting you want to modify: ");
        int meetingIndex;

        while (true) {
            try {
                meetingIndex = Integer.parseInt(scanner.nextLine().trim());

                if (meetingIndex < 1 || meetingIndex > meetings.size()) {
                    System.out.println("Invalid selection. Please try again.");
                    continue;
                }

                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        final Meeting selectedMeeting = meetings.get(meetingIndex - 1);

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

        LocalDateTime newStartDateTime = selectedMeeting.getStartTime();

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
            System.err.println("Invalid date and time format. Please try again.");
        }

        LocalDateTime newEndDateTime = selectedMeeting.getEndTime();

        while (true) {
            System.out.print("Enter new end date and time (" + dateTimePattern + ", leave blank to keep current: " + newEndDateTime + "): ");
            String endDateTimeInput = scanner.nextLine().trim();
            if (endDateTimeInput.isEmpty()) {
                break;
            }
            LocalDateTime parsedDateTime = validateDateTimeInput(endDateTimeInput);
            if (parsedDateTime != null) {
                if (newStartDateTime != null && parsedDateTime.isBefore(newStartDateTime)) {
                    System.err.println("End date and time must be after the start date and time.");
                    continue;
                }
                newEndDateTime = parsedDateTime;
                break;
            }
            System.err.println("Invalid date and time format. Please try again.");
        }

        System.out.println("Current guests: " + String.join(", ", selectedMeeting.getGuestEmployees()));

        System.out.println("\nNow, you will see the list of all the employees you can invite to the meeting: ");
        List<String> employeeNames = controller.getEmployeeNames();

        for (String employeeName : employeeNames) {
            System.out.println("\t - " + employeeName);
        }

        System.out.println();

        System.out.print("Enter new guest employees (comma-separated, leave blank to keep current): ");
        String newGuestsInput = scanner.nextLine().trim();
        Set<String> newGuestEmployees = selectedMeeting.getGuestEmployees();

        if (!newGuestsInput.isEmpty()) {
            String[] guests = newGuestsInput.split(",");
            for (String guest : guests) {
                newGuestEmployees.add(guest.trim());
            }
        }
        // Create the updated meeting object
        Meeting updatedMeeting = new Meeting(
                selectedMeeting.getId(),
                newTopic,
                newGuestEmployees,
                newPlace,
                newStartDateTime,
                newEndDateTime,
                selectedMeeting.getOrganizerName()
        );

        System.out.println("Updating meeting...");
        // Send the updated meeting to the server
        controller.sendUpdateMeetingRequest(updatedMeeting);
        writeMeetingsToFile();
    }

    private void syncMeetings() {
        writeMeetingsToFile();
    }

    private void writeMeetingsToFile() {
        try {
            FileUtils.writeObjectsToFile(controller.getMyMeetings(), EmployeeMain.MEETINGS_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void retrieveMeetingsFromLocalStorage() {
        try {
            FileUtils.readAndPrintFile(EmployeeMain.MEETINGS_FILE_PATH);
        } catch (IOException e) {
            System.out.println("The file could not be read.");
        }
    }

    private void viewMyMeetings() {
        System.out.println("\nRetrieving your scheduled meetings from server...");

        List<Meeting> myMeetings = controller.getMyMeetings();

        if (myMeetings.isEmpty()) {
            System.out.println("There are no meetings associated with you!");
            return;
        }

        myMeetings.sort(Comparator.comparing(Meeting::getStartTimeTimestamp));
        System.out.println("\nYour scheduled meetings:");
        System.out.println(Meeting.getMeetingsString(myMeetings));
    }

    private static void exitSystem() {
        System.out.println("\nLogging out...");
        System.out.println("Goodbye, " + EmployeeMain.EMPLOYEE_NAME + "!");
        System.exit(0);
    }
}