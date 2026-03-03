
public class MedicalRecord {
    private String patientName;
    private String doctor;
    private String nurse;
    private String division;
    private String medicalData;
    private int recordID;
    
    public MedicalRecord(String patientName, String doctor, String nurse, String division, String medicalData, int recordID) {
        this.patientName = patientName;
        this.doctor = doctor;
        this.nurse = nurse;
        this.division = division;
        this.medicalData = medicalData;
        this.recordID = recordID;
    }


    public String getPatientName() {
        return patientName;
    }


    public String getDoctor() {
        return doctor;
    }


    public String getNurse() {
        return nurse;
    }


    public String getDivision() {
        return division;
    }


    public String getMedicalData() {
        return medicalData;
    }



    public void changeMedicalData(String newMedicalData) {
        medicalData = newMedicalData;
    }


    public int getRecordID() {
        return recordID;
    }


    public void setRecordID(int newID) {
        recordID = newID;
    }


    public String toString() {
        return ("Name: " + patientName + " Doctor: " + doctor + " Nurse: " + nurse
            + " Division: " + division + " Medical Data: " + medicalData + " Record ID: " + recordID);
    }


    public String toStringCSV() {
        return (patientName + ", " + doctor + ", " + nurse + ", " + division + ", " + medicalData + ", " + recordID);
    }
}
