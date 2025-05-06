package org.intiandes.common.utils;

import java.io.*;
import java.util.List;

public class FileUtils {
    public static void writeObjectsToFile(List<?> objects, String filePath) throws IOException {
        File file = new File(filePath);

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                throw new IOException("Failed to create directory: " + parentDir);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Object obj : objects) {
                if (obj != null) {
                    writer.write(obj.toString());
                    writer.write("\n");
                }
                writer.newLine();
            }
        }
    }

    public static void readAndPrintFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            System.out.println("There are no registers in the file!");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String printableLine = line.replaceAll("[^\\p{Print}]", "");
                System.out.println(printableLine);
            }
        }
    }
}