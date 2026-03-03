import java.util.Set;
import java.util.HashSet;
import java.lang.StringBuilder;

public class RecordRepository {
    

    private Set<MedicalRecord> records;
    private Set<User> users;
    private int highestID;

    public RecordRepository(Set<MedicalRecord> records, Set<User> users) {
        this.records = records;
        this.users = users;
        highestID = records.size() - 1;
    }

    public synchronized String handleRequest(String name, Request req) {
        Action action = req.action();
        String information = req.information();
        User user = getUser(name);
        switch (action) {
            case READ:
                int recordID = Integer.parseInt(information);
                MedicalRecord record = getMedicalRecord(recordID);
                if (allowedRead(user, record)) {
                    return record.toString();
                } else {
                    return "Ej tillåtet!";
                }
            case WRITE:
                String[] splitInformation = information.split("€");
                recordID = Integer.parseInt(splitInformation[0]);
                String newMedicalData = splitInformation[1];
                record = getMedicalRecord(recordID);
                if (allowedWrite(user, record)) {
                    record.changeMedicalData(newMedicalData);
                    return "Medical data har uppdaterats";
                } else {
                    return "Ej tillåtet!";
                }
                
            case VIEW_ALL:
                Set<MedicalRecord> authorizedRecords = getAllAuthorizedRecords(user);
                StringBuilder sb = new StringBuilder();
                for (MedicalRecord rec : authorizedRecords) {
                    sb.append(rec);
                    sb.append("€");
                }
                return sb.toString();
            case CREATE:
                String[] createInformationSplit = information.split("€");
                String patientName = createInformationSplit[0];
                String nurse = createInformationSplit[1];
                String division = createInformationSplit[2];
                String medicalData = createInformationSplit[3];
                recordID = highestID + 1;
                highestID++;
                if (allowedCreate(user, patientName)) {
                    records.add(new MedicalRecord(patientName, user.getName(), nurse, division, medicalData, recordID));
                    return "Record lades till!";
                } else {
                    return "Ej tillåtet!";
                }
                
            case DELETE:
                recordID = Integer.parseInt(information);
                if (allowedDelete(user)) {
                    for (MedicalRecord rec : records) {
                        if (rec.getRecordID() == recordID) {
                            records.remove(rec);
                            return "Record har tagits bort";
                        }
                    }
                    return "Record hittades inte";
                }
                return "Ej tillåtet!";
        }
        return null;
    }


    // Returns all records that the user has at least read permissions for
    private Set<MedicalRecord> getAllAuthorizedRecords(User user) {
        Set<MedicalRecord> authorizedRecords = new HashSet<>();
        for (MedicalRecord record : records) {
            if (allowedRead(user, record)) {
                authorizedRecords.add(record);
            }
        }
        return authorizedRecords;
    }


    private User getUser(String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }


    public Set<MedicalRecord> getAllRecords() {
        return records;
    }


    private MedicalRecord getMedicalRecord(int recordID) {
        for (MedicalRecord record : records) {
            if (record.getRecordID() == recordID) return record;
        }
        return null;
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
