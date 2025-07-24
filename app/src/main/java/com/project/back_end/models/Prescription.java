package com.project.back_end.models;

import org.springframework.data.mongodb.core.mapping.Document; // marks this class as a MongoDB document
import org.springframework.data.annotation.Id; // primary key identifier for MongoDB
import jakarta.validation.constraints.NotNull; // enforce non-null constraints
import jakarta.validation.constraints.Size; // enforce string length constraints

@Document(collection = "prescriptions") // map this class to the 'prescriptions' collection in MongoDB
public class Prescription {

    @Id // primary key identifier
    private String id;

    @NotNull(message = "patientName cannot be null") // enforce non-null constraint on patientName
    @Size(min = 3, max = 100, message = "patientName must be between 3 and 100 characters") // enforce length constraints on patientName
    private String patientName;

    @NotNull(message = "appointmentId cannot be null") // enforce non-null constraint on appointmentId
    private Long appointmentId;

    @NotNull(message = "medication cannot be null") // enforce non-null constraint on medication
    @Size(min = 3, max = 100, message = "medication must be between 3 and 100 characters") // enforce length constraints on medication
    private String medication;

    @NotNull(message = "dosage cannot be null") // enforce non-null constraint on dosage
    private String dosage;

    @Size(max = 200, message = "doctorNotes must not exceed 200 characters") // enforce max length on doctorNotes
    private String doctorNotes;

    public Prescription() {
        // default constructor required by Spring Data MongoDB
    }

    public Prescription(
            String id,
            String patientName,
            Long appointmentId,
            String medication,
            String dosage,
            String doctorNotes
    ) { // parameterized constructor
        this.id = id; // set unique identifier
        this.patientName = patientName; // set patient's name
        this.appointmentId = appointmentId; // set associated appointment ID
        this.medication = medication; // set prescribed medication
        this.dosage = dosage; // set dosage information
        this.doctorNotes = doctorNotes; // set doctor's notes
    }

    public String getId() {
        return id; // returns the unique identifier of the prescription
    }

    public void setId(String id) {
        this.id = id; // sets the unique identifier of the prescription
    }

    public String getPatientName() {
        return patientName; // returns the patient's name
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName; // sets the patient's name
    }

    public Long getAppointmentId() {
        return appointmentId; // returns the associated appointment ID
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId; // sets the associated appointment ID
    }

    public String getMedication() {
        return medication; // returns the prescribed medication
    }

    public void setMedication(String medication) {
        this.medication = medication; // sets the prescribed medication
    }

    public String getDosage() {
        return dosage; // returns the dosage information
    }

    public void setDosage(String dosage) {
        this.dosage = dosage; // sets the dosage information
    }

    public String getDoctorNotes() {
        return doctorNotes; // returns the doctor's notes
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes; // sets the doctor's notes
    }
}
