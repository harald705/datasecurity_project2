import java.util.HashSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class ReadSavedFiles {
    
    public static HashSet<MedicalRecord> readSavedRecords(String filename) {
        HashSet<MedicalRecord> medicalRecords = new HashSet<>();
        
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
}
