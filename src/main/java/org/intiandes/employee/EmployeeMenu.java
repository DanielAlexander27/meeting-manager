package org.intiandes.employee;

import org.intiandes.common.model.Meeting;
import org.intiandes.common.request.CreateMeetingRequest;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeeMenu {

    private static String employeeName;
    private static final Scanner scanner = new Scanner(System.in);
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9091;

    public static void main(String[] args) {
        showWelcome();
        login();
        showMenu();
    }

    private static void showWelcome() {
        System.out.println("          ==========================================          ");
        System.out.println("          MEETING MANAGEMENT SYSTEM - EMPLOYEE               ");
        System.out.println("          ==========================================          \n");
    }

    private static void login() {
        System.out.print("Enter your username (e.g., Alice_White): ");
        employeeName = scanner.nextLine().trim();

        if (employeeName.isEmpty()) {
            System.out.println("Invalid username. Please try again.");
            login();
        } else {
            System.out.println("Logged in as: " + employeeName + "\n");
        }
    }

    private static void showMenu() {
        int choice = -1;

        while (choice != 7) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("Employee: " + employeeName);
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
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 7.");
                continue;
            }

            switch (choice) {
                case 1 -> viewMeetings();
                case 2 -> createMeeting();
                case 3 -> modifyMeeting();
                case 4 -> cancelMeeting();
                case 5 -> syncMeetings();
                case 6 -> retrieveMeetings();
                case 7 -> exitSystem();
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void retrieveMeetings() {
        String fileName = "meetings_serialized_" + employeeName + ".txt";
        File file = new File(fileName);

        System.out.println("\nRetrieving past meetings for " + employeeName + ":");

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

            for (Meeting meeting : meetings) {
                System.out.println("-------------------------------------------------");
                System.out.println("Topic: " + meeting.getTopic());
                System.out.println("Place: " + meeting.getPlace());
                System.out.println("Start Time: " + LocalDateTime.ofEpochSecond(meeting.getStartTimeTimestamp(), 0, ZoneOffset.UTC));
                System.out.println("End Time: " + LocalDateTime.ofEpochSecond(meeting.getEndTimeTimestamp(), 0, ZoneOffset.UTC));
                System.out.println("Organizer: " + meeting.getOrganizerName());
                System.out.println("Guests: " + String.join(", ", meeting.getGuestEmployees()));
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error retrieving meetings: " + e.getMessage());
        }
    }

    private static void viewMeetings() {
        String fileName = "meetings_" + employeeName + ".txt";
        File file = new File(fileName);

        System.out.println("\nScheduled meetings for " + employeeName + ":");

        if (!file.exists()) {
            System.out.println("No meetings found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                System.out.println("-------------------------------------------------");
                System.out.println(line);
                count++;
            }

            if (count == 0) {
                System.out.println("🔍 No meetings found.");
            }

        } catch (IOException e) {
            System.err.println("Error reading the meetings file: " + e.getMessage());
        }
    }

    private static void createMeeting() {
        System.out.println("=== Create Meeting ===");

        System.out.print("Meeting topic: ");
        String topic = scanner.nextLine();

        System.out.print("Meeting description: ");
        String description = scanner.nextLine();

        System.out.print("Meeting place: ");
        String place = scanner.nextLine();

        System.out.print("Start date and time (yyyy-MM-dd HH:mm:ss): ");
        String startInput = scanner.nextLine();

        System.out.print("End date and time (yyyy-MM-dd HH:mm:ss): ");
        String endInput = scanner.nextLine();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        try {
            startDateTime = LocalDateTime.parse(startInput, formatter);
            endDateTime = LocalDateTime.parse(endInput, formatter);

            if (!endDateTime.isAfter(startDateTime)) {
                System.err.println("\nError: The end date and time must be after the start date and time.");
                return;
            }

            List<String> guestEmployees = new ArrayList<>();
            System.out.print("Enter guest employees (comma-separated): ");
            String guestsInput = scanner.nextLine();
            if (!guestsInput.isEmpty()) {
                String[] guests = guestsInput.split(",");
                for (String guest : guests) {
                    guestEmployees.add(guest.trim());
                }
            }

            Meeting meeting = new Meeting(topic, guestEmployees, place, startDateTime.toEpochSecond(ZoneOffset.UTC), endDateTime.toEpochSecond(ZoneOffset.UTC), employeeName);

            try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                CreateMeetingRequest request = new CreateMeetingRequest(meeting, new ArrayList<>());
                out.writeObject(request);
                System.out.println("\nMeeting created successfully!");

            } catch (IOException e) {
                System.err.println("Error connecting to the server: " + e.getMessage());
            }

        } catch (DateTimeParseException e) {
            System.err.println("\nError: Invalid date and time format.");
            System.err.println("Valid example: 2025-05-04 10:30:00");
        }
    }

    private static void modifyMeeting() {
        System.out.println("Modify meeting functionality is not yet implemented.");
    }

    private static void cancelMeeting() {
        System.out.println("Cancel meeting functionality is not yet implemented.");
    }

    private static void syncMeetings() {
        String fileName = "meetings_" + employeeName + ".txt";

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            out.println("SYNC " + employeeName);

            String line;
            while ((line = in.readLine()) != null) {
                writer.write(line + "\n");
                if (line.equals("EOF")) break;
            }
            System.out.println("Meetings successfully synced with the server.");

        } catch (IOException e) {
            System.err.println("Error syncing with the central server: " + e.getMessage());
        }
    }

    private static void exitSystem() {
        System.out.println("\nLogging out...");
        System.out.println("Goodbye, " + employeeName + "!");
    }
}