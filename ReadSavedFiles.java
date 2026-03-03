import java.util.HashSet;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ReadSavedFiles {
    
    public static Set<MedicalRecord> readSavedRecords(String filename) {
        Set<MedicalRecord> medicalRecords = new HashSet<>();
        
        File file = new File(filename);
        try (Scanner scan = new Scanner(file)) {
            // Skips the first row of the csv file that explains the values
            scan.nextLine();
            while(scan.hasNextLine()) {
                String recordString = scan.nextLine();
                String[] recordStringSplit = recordString.split(",");

                // Removing whitespace at start and end
                for (int i = 0; i <  recordStringSplit.length; i++) {
                    recordStringSplit[i] = recordStringSplit[i].strip();
                }
                String patientName = recordStringSplit[0];
                String doctor = recordStringSplit[1];
                String nurse = recordStringSplit[2];
                String division = recordStringSplit[3];
                String medicalData = recordStringSplit[4];
                int recordID = Integer.parseInt(recordStringSplit[5]);
                MedicalRecord newMedicalRecord = new MedicalRecord(patientName, doctor, nurse, division, medicalData, recordID);
                medicalRecords.add(newMedicalRecord);
            }
        } catch(FileNotFoundException e) {
            System.out.println("File is not found");
        }
        return medicalRecords;
    }


    public static Set<User> readSavedUsers(String filename) {
        Set<User> users = new HashSet<>();

        File file = new File(filename);
        try (Scanner scan = new Scanner(file)) {
            // Skips the first row of the csv file that explains the values
            scan.nextLine();
            while(scan.hasNextLine()) {
                String userString = scan.nextLine();
                String[] userStringSplit = userString.split(",");

                // Removing whitespace at start and end
                for (int i = 0; i <  userStringSplit.length; i++) {
                    userStringSplit[i] = userStringSplit[i].strip();
                }
                String name = userStringSplit[0];
                String role = userStringSplit[1];
                String division = userStringSplit[2];
                User user = new User(name, role, division);
                users.add(user);
            }
        } catch(FileNotFoundException e) {
            System.out.println("File is not found");
        }
        return users;
    }


    public static void writeRecords(Set<MedicalRecord> records) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("records.csv"))) {
            writer.write("patient name, doctor, nurse, division, medical data, recordID");
            writer.newLine();
            int index = 0;
            for (MedicalRecord record : records) {
                record.setRecordID(index);
                index++;
                writer.write(record.toStringCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error!!!!");
        }
    }


    public static void log(String message) {
        try {
            FileWriter writer = new FileWriter("auditLog.txt", true);
            writer.write(message);
            writer.write("\n");
            writer.close();
        } catch(IOException e) {
            System.out.println(e);
        }
    }
}
