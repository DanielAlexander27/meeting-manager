package org.intiandes.employee.frontend.employeemenu;

import org.intiandes.common.model.Meeting;
import org.intiandes.employee.EmployeeMain;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

//    private static void login() {
//        System.out.print("Enter your username (e.g., Alice_White): ");
//        employeeName = scanner.nextLine().trim();
//
//        if (employeeName.isEmpty()) {
//            System.out.println("Invalid username. Please try again.");
//            login();
//        } else {
//            System.out.println("Logged in as: " + employeeName + "\n");
//        }
//    }

    public void showMenu() {
        int choice = -1;

        while (choice != 7) {
            System.out.println("\n===== MAIN MENU =====");
//            System.out.println("Employee: " + EmployeeMain.EMPLOYEE_NAME);
            System.out.println("1. View scheduled meetings");
            System.out.println("2. Create a new meeting");
            System.out.println("3. Modify a meeting");
            System.out.println("4. Cancel a meeting");
            System.out.println("5. Sync meetings with the server");
            System.out.println("6. Retrieve past meetings");
            System.out.println("7. Exit");
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

    private static void retrieveMeetings() {
        String fileName = "meetings_serialized_" + EmployeeMain.EMPLOYEE_USERNAME + ".txt";
        File file = new File(fileName);

        System.out.println("\nRetrieving past meetings for " + EmployeeMain.EMPLOYEE_NAME + ":");

        if (!file.exists()) {
            System.out.println("No serialized meetings found.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<Meeting> meetings = (List<Meeting>) ois.readObject();

            if (meetings.isEmpty()) {
                System.out.println("No meetings found.");
                return;
            }

            Meeting.getMeetingsString(meetings);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error retrieving meetings: " + e.getMessage());
        }
    }

    private void viewMyMeetings() {
        List<Meeting> myMeetings = controller.getMyMeetings();

        if (myMeetings.isEmpty()) {
            System.out.println("There are no meetings associated with you!");
        } else {
            System.out.println(Meeting.getMeetingsString(myMeetings));
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
            System.out.println("\t -" + employeeName);
        }

        System.out.println();

        List<String> guestEmployees = new ArrayList<>();
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

    private static void modifyMeeting() {
        System.out.println("Modify meeting functionality is not yet implemented.");
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