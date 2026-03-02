import java.util.Set;
import java.util.HashSet;

public class RecordRepository {
    

    private Set<MedicalRecord> records;
    private Set<User> users;

    public RecordRepository(Set<MedicalRecord> records, Set<User> users) {
        this.records = records;
        this.users = users;
    }


    private boolean allowedRead(User user, MedicalRecord record) {
        // Agency can read all records
        if (user.getRole().equals("agency")) return true;
        
        // Patients can read their own records
        if (user.getName().equals(record.getPatientName())) return true;

        // Nurses can read records associated with them and those in their division
        if (user.getRole().equals("nurse")) {
            if (user.getName().equals(record.getNurse())) return true;
            if (user.getDivision().equals(record.getDivision())) return true;
        }

        // Doctors can read records associated with them and those in their division
        if (user.getRole().equals("doctor")) {
            if (user.getName().equals(record.getDoctor())) return true;
            if (user.getDivision().equals(record.getDivision())) return true;
        }

        return false;
    }


    private boolean allowedWrite(User user, MedicalRecord record) {
        // Agency can write to all records
        if (user.getRole().equals("agency")) return true;
        
        // Nurses can write to their associated records
        if (user.getRole().equals("nurse") && user.getName().equals(record.getNurse())) return true;

        if (user.getRole().equals("doctor") && user.getName().equals(record.getDoctor())) return true;
        
        return false;
    }


    private boolean allowedCreate(User user, String patientName) {
        if (user.getRole().equals("doctor")) {
            for (MedicalRecord record : records) {
                if (record.getPatientName().equals(patientName)) {
                    if (user.getName().equals(record.getDoctor())) return true;
                }
            }
        }
        return false;
    }


    private boolean allowedDelete(User user) {
        if (user.getRole().equals("agency")) return true;
        return false;
    }

    /*private boolean allowedRead(String name, int recordID) {
        String role = "";
        String userDivision = "";

        boolean foundUser = false;

        for (User user : users) {
            if (user.getName().equals(name)) {
                role = user.getRole();
                userDivision = user.getDivision();
                foundUser = true;
                break;
            }
        }
        if (!foundUser) {
            return false;
        }

        // Agency can read all records
        if (role.equals("agency")) {
            return true;
        }


        String patientName = "";
        String doctorName = "";
        String nurseName = "";
        String recordDivision = "";
        String medicalData = "";

        boolean foundRecord = false;
        
        for (MedicalRecord record : records) {
            if (record.getRecordID() == recordID) {
                patientName = record.getPatientName();
                doctorName = record.getDoctor();
                nurseName = record.getNurse();
                recordDivision = record.getDivision();
                medicalData = record.getMedicalData();
                foundRecord = true;
                break;
            }
        }
        if (!foundRecord) {
            return false;
        }

        // Patients can read their own records
        if (name.equals(patientName)) return true;

        if (role.equals("nurse")) {
            if (name.equals(nurseName)) return true;
            if (userDivision.equals(recordDivision)) return true;
        }


        if (role.equals("doctor")) {
            if (name.equals(doctorName)) return true;
            if (userDivision.equals(recordDivision)) return true;
        }

        return false;
    }*/
}
