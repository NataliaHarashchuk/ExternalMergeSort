package myproject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String RESULTS_DIRECTORY = "E:\\ASD\\Laboratorna1\\Lab\\results"; // Вкажіть ваш шлях тут

    public static void generateFile(String fileName, int sizeInMB) throws IOException {
        ensureDirectoryExists();
        String filePath = RESULTS_DIRECTORY + File.separator + fileName;

        int elementsPerMB = 1024 * 1024 / Integer.BYTES;
        int totalElements = elementsPerMB * sizeInMB;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < totalElements; i++) {
                writer.write((int) (Math.random() * 5000) + "\n");
            }
        }
        System.out.println("File is generated with size: " + sizeInMB + " MB");
    }

    public static void directMergeSort(String inputFile, String tempFileB, String tempFileC) throws IOException {
        int chunkSize = 1;
        boolean sorted = false;

        long startTime = System.nanoTime();

        while (!sorted) {
            splitFile(inputFile, tempFileB, tempFileC, chunkSize); // Розподіл у файли B і C
            mergeFiles(tempFileB, tempFileC, inputFile, chunkSize); // Злиття у файл A
            chunkSize *= 2; // Збільшення розміру блоку
            sorted = isSorted(inputFile); // Перевірка на сортування
        }

        long endTime = System.nanoTime();


        long elapsedTime = endTime - startTime;
        double elapsedMinutes = elapsedTime / 1e9 / 60;

        deleteFile(tempFileB);
        deleteFile(tempFileC);

        System.out.printf("Sorting completed. Time: %.2f minutes", elapsedMinutes);
    }
 private static void splitFile(String inputFile, String fileB, String fileC, int chunkSize) throws IOException {
        String inputPath = RESULTS_DIRECTORY + File.separator + inputFile;
        String filePathB = RESULTS_DIRECTORY + File.separator + fileB;
        String filePathC = RESULTS_DIRECTORY + File.separator + fileC;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath));
             BufferedWriter writerB = new BufferedWriter(new FileWriter(filePathB));
             BufferedWriter writerC = new BufferedWriter(new FileWriter(filePathC))) {

            String line;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                if ((count / chunkSize) % 2 == 0) {
                    writerB.write(line + "\n");
                } else {
                    writerC.write(line + "\n");
                }
                count++;
            }
        }
    }

    private static void mergeFiles(String fileB, String fileC, String outputFile, int chunkSize) throws IOException {
        String filePathB = RESULTS_DIRECTORY + File.separator + fileB;
        String filePathC = RESULTS_DIRECTORY + File.separator + fileC;
        String outputPath = RESULTS_DIRECTORY + File.separator + outputFile;

        try (BufferedReader readerB = new BufferedReader(new FileReader(filePathB));
             BufferedReader readerC = new BufferedReader(new FileReader(filePathC));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {

            List<Integer> bufferB = new ArrayList<>();
            List<Integer> bufferC = new ArrayList<>();

            String lineB, lineC;

            while (true) {
                for (int i = 0; i < chunkSize && (lineB = readerB.readLine()) != null; i++) {
                    bufferB.add(Integer.parseInt(lineB));
                }
                for (int i = 0; i < chunkSize && (lineC = readerC.readLine()) != null; i++) {
                    bufferC.add(Integer.parseInt(lineC));
                }

                if (bufferB.isEmpty() && bufferC.isEmpty()) {
                    break;
                }

                int i = 0, j = 0;
                while (i < bufferB.size() && j < bufferC.size()) {
                    if (bufferB.get(i) <= bufferC.get(j)) {
                        writer.write(bufferB.get(i++) + "\n");
                    } else {
                        writer.write(bufferC.get(j++) + "\n");
                    }
                }

                while (i < bufferB.size()) {
                    writer.write(bufferB.get(i++) + "\n");
                }
                while (j < bufferC.size()) {
                    writer.write(bufferC.get(j++) + "\n");
                }

                bufferB.clear();
                bufferC.clear();
            }
        }
    }

    private static boolean isSorted(String file) throws IOException {
        String filePath = RESULTS_DIRECTORY + File.separator + file;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int previous = Integer.MIN_VALUE;

            while ((line = reader.readLine()) != null) {
                int current = Integer.parseInt(line);
                if (current < previous) {
                    return false;
                }
                previous = current;
            }
        }
        return true;
    }

    private static void ensureDirectoryExists() throws IOException {
        File directory = new File(RESULTS_DIRECTORY);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Failed to create directory: " + RESULTS_DIRECTORY);
            }
        }
    }
    private static void deleteFile(String fileName) {
        String filePath = RESULTS_DIRECTORY + File.separator + fileName;
        File file = new File(filePath);

        if (file.exists()) {
            file.delete();
        } else {
            System.err.println("Temporary file " + fileName + " not found.");
        }
    }

    public static void main(String[] args) throws IOException {
        String fileA = "A.txt";
        String fileB = "B.txt";
        String fileC = "C.txt";
        int sizeInMB = 100;

        ensureDirectoryExists();

        generateFile(fileA, sizeInMB);
        directMergeSort(fileA, fileB, fileC);
    }
}