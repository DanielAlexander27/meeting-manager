package org.intiandes.employee;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeeMenu {

    private static String employeeName;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        showWelcome();
        login();
        showMenu();
    }

    private static void showWelcome() {
        System.out.println("          ==========================================          ");
        System.out.println("          SISTEMA DE GESTION DE REUNIONES - EMPLEADO          ");
        System.out.println("          ==========================================          \n");
    }

    private static void login() {
        System.out.print("Ingrese su nombre de usuario (Ej: Alice_White): ");
        employeeName = scanner.nextLine().trim();

        if (employeeName.isEmpty()) {
            System.out.println("Nombre de usuario no valido. Intente nuevamente.");
            login();
        } else {
            System.out.println("Sesion iniciada como: " + employeeName + "\n");
        }
    }

    private static void showMenu() {
        int choice = -1;

        while (choice != 6) {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("Empleado: " + employeeName);
            System.out.println("1. Ver reuniones programadas");
            System.out.println("2. Crear nueva reunion");
            System.out.println("3. Modificar una reunion");
            System.out.println("4. Cancelar una reunion");
            System.out.println("5. Sincronizar reuniones con el servidor");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opcion: ");

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada no valida. Intente con un número entre 1 y 6.");
                continue;
            }

            switch (choice) {
                case 1 -> viewMeetings();
                case 2 -> createMeeting();
                case 3 -> modifyMeeting();
                case 4 -> cancelMeeting();
                case 5 -> syncMeetings();
                case 6 -> exitSystem();
                default -> System.out.println("Opcinn invalida. Intente nuevamente.");
            }
        }
    }

    private static void viewMeetings() {
        String fileName = "reuniones_" + employeeName + ".txt";
        File file = new File(fileName);

        System.out.println("\nReuniones programadas para " + employeeName + ":");

        if (!file.exists()) {
            System.out.println("No hay reuniones registradas.");
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
                System.out.println("🔍 No hay reuniones registradas.");
            }

        } catch (IOException e) {
            System.err.println("Error al leer el archivo de reuniones: " + e.getMessage());
        }
    }


    private static void createMeeting() {
        System.out.println("=== Crear Reunion ===");

        System.out.print("Título de la reunion: ");
        String title = scanner.nextLine();

        System.out.print("Descripción de la reunion: ");
        String description = scanner.nextLine();

        System.out.print("Lugar de la reunion: ");
        String location = scanner.nextLine();

        System.out.print("Fecha y hora de inicio (yyyy-MM-dd HH:mm:ss): ");
        String startInput = scanner.nextLine();

        System.out.print("Fecha y hora de fin (yyyy-MM-dd HH:mm:ss): ");
        String endInput = scanner.nextLine();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        try {
            startDateTime = LocalDateTime.parse(startInput, formatter);
            endDateTime = LocalDateTime.parse(endInput, formatter);

            if (!endDateTime.isAfter(startDateTime)) {
                System.err.println("\nError: La fecha y hora de fin debe ser posterior a la de inicio.");
                System.err.println("Inicio ingresado: " + startDateTime);
                System.err.println("Fin ingresado:    " + endDateTime);
                return;
            }
            

            // En esta etapa podrías construir un objeto Meeting y enviarlo al servidor.
            System.out.println("\n✅ Reunion creada correctamente:");
            System.out.println("Titulo: " + title);
            System.out.println("Descripcion: " + description);
            System.out.println("Lugar: " + location);
            System.out.println("Inicio: " + startDateTime);
            System.out.println("Fin: " + endDateTime);

            // Aquí podrías llamar a una función para enviar esta información al servidor.

        } catch (DateTimeParseException e) {
            System.err.println("\nError: El formato de fecha y hora es inválido.");
            System.err.println("Ejemplo válido: 2025-05-04 10:30:00");
        }
    }


    private static void modifyMeeting() {
        String fileName = "reuniones_" + employeeName + ".txt";
        File file = new File(fileName);

        if (!file.exists()) {
            System.out.println("\nNo hay reuniones que modificar.");
            return;
        }

        List<List<String>> meetings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            List<String> currentMeeting = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.equals("-------------------------------------------------")) {
                    meetings.add(new ArrayList<>(currentMeeting));
                    currentMeeting.clear();
                } else {
                    currentMeeting.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer las reuniones: " + e.getMessage());
            return;
        }

        if (meetings.isEmpty()) {
            System.out.println("\nNo hay reuniones que modificar.");
            return;
        }

        // Mostrar reuniones
        System.out.println("\n📋 Reuniones disponibles:");
        for (int i = 0; i < meetings.size(); i++) {
            System.out.println("\n[#"+(i+1)+"]");
            for (String line : meetings.get(i)) {
                System.out.println(line);
            }
        }

        System.out.print("\nIngrese el número de la reunión que desea modificar: ");
        int index;
        try {
            index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index < 0 || index >= meetings.size()) {
                System.out.println("Índice inválido.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada no válida.");
            return;
        }

        List<String> selected = meetings.get(index);
        System.out.println("\nEditando reunión seleccionada...");

        // Extraer campos actuales
        String[] newData = new String[5];
        String[] fieldLabels = {"Título", "Descripción", "Lugar", "Inicio (yyyy-MM-dd HH:mm:ss)", "Fin (yyyy-MM-dd HH:mm:ss)"};

        for (int i = 0; i < 5; i++) {
            String currentLine = selected.get(i);
            String oldValue = currentLine.substring(currentLine.indexOf(":") + 2);
            System.out.print(fieldLabels[i] + " [" + oldValue + "]: ");
            String input = scanner.nextLine().trim();
            newData[i] = input.isEmpty() ? oldValue : input;
        }

        // Validar fechas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            LocalDateTime start = LocalDateTime.parse(newData[3], formatter);
            LocalDateTime end = LocalDateTime.parse(newData[4], formatter);

            if (!end.isAfter(start)) {
                System.err.println("La fecha de fin debe ser posterior a la de inicio.");
                return;
            }
        } catch (DateTimeParseException e) {
            System.err.println("Formato de fecha inválido.");
            return;
        }

        // Guardar modificación
        List<String> updatedMeeting = new ArrayList<>();
        updatedMeeting.add("Título: " + newData[0]);
        updatedMeeting.add("Descripción: " + newData[1]);
        updatedMeeting.add("Lugar: " + newData[2]);
        updatedMeeting.add("Inicio: " + newData[3]);
        updatedMeeting.add("Fin: " + newData[4]);

        meetings.set(index, updatedMeeting); // Reemplaza la reunión vieja

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (List<String> meeting : meetings) {
                for (String line : meeting) {
                    writer.write(line + "\n");
                }
                writer.write("-------------------------------------------------\n");
            }
            System.out.println("\nReunión modificada exitosamente.");
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }


    private static void cancelMeeting() {
        String fileName = "reuniones_" + employeeName + ".txt";
        File file = new File(fileName);

        if (!file.exists()) {
            System.out.println("\n📭 No hay reuniones que cancelar.");
            return;
        }

        List<List<String>> meetings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            List<String> currentMeeting = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.equals("-------------------------------------------------")) {
                    meetings.add(new ArrayList<>(currentMeeting));
                    currentMeeting.clear();
                } else {
                    currentMeeting.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer las reuniones: " + e.getMessage());
            return;
        }

        if (meetings.isEmpty()) {
            System.out.println("\nNo hay reuniones que cancelar.");
            return;
        }

        // Mostrar reuniones
        System.out.println("\n📋 Reuniones disponibles para cancelar:");
        for (int i = 0; i < meetings.size(); i++) {
            System.out.println("\n[#"+(i+1)+"]");
            for (String line : meetings.get(i)) {
                System.out.println(line);
            }
        }

        System.out.print("\nIngrese el número de la reunión que desea cancelar: ");
        int index;
        try {
            index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index < 0 || index >= meetings.size()) {
                System.out.println("Índice inválido.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada no válida.");
            return;
        }

        // Confirmación
        System.out.print("¿Está seguro de que desea cancelar esta reunión? (s/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("s")) {
            System.out.println("❎ Cancelación abortada.");
            return;
        }

        meetings.remove(index); // Eliminar reunión seleccionada

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (List<String> meeting : meetings) {
                for (String line : meeting) {
                    writer.write(line + "\n");
                }
                writer.write("-------------------------------------------------\n");
            }
            System.out.println("\nReunión cancelada exitosamente.");
        } catch (IOException e) {
            System.err.println("Error al guardar los cambios: " + e.getMessage());
        }
    }


    private static void syncMeetings() {
        String serverHost = "localhost";
        int serverPort = 9090;

        try (Socket socket = new Socket(serverHost, serverPort);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

        // Enviar solicitud de sincronización
        out.println("SYNC " + employeeName);

        String fileName = "reuniones_" + employeeName + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            String line;
            while ((line = in.readLine()) != null) {
                writer.write(line + "\n");
                if (line.equals("EOF")) break;
            }
            System.out.println("Reuniones sincronizadas correctamente con el servidor.");
        }

        } catch (IOException e) {
            System.err.println("Error al sincronizar con el servidor central: " + e.getMessage());
        }
    }


    private static void exitSystem() {
        System.out.println("\n Cerrando sesion...");
        System.out.println("¡Hasta luego! " + employeeName );
    }
}
