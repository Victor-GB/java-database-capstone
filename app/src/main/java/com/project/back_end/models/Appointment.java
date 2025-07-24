package com.project.back_end.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity // marks this class as a JPA entity
public class Appointment {

    @Id // primary key identifier
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-generate PK using IDENTITY strategy
    private Long id;

    @ManyToOne // many appointments can be linked to one doctor
    @NotNull(message = "doctor cannot be null") // enforce non-null constraint on doctor
    private Doctor doctor;

    @ManyToOne // many appointments can be linked to one patient
    @NotNull(message = "patient cannot be null") // enforce non-null constraint on patient
    private Patient patient;

    @Future // ensure appointment time is in the future
    private LocalDateTime appointmentTime;

    @NotNull(message = "status cannot be null") // enforce non-null constraint on status
    private Integer status;

    public Appointment() {
        // default constructor required by JPA
    }

    public Appointment(
            Long id,
            Doctor doctor,
            Patient patient,
            LocalDateTime appointmentTime,
            Integer status
    ) { // parameterized constructor
        this.id = id; // set unique identifier
        this.doctor = doctor; // set assigned doctor
        this.patient = patient; // set assigned patient
        this.appointmentTime = appointmentTime; // set appointment start time
        this.status = status; // set appointment status
    }

    public Long getId() {
        return id; // returns the unique identifier of the appointment
    }

    public void setId(Long id) {
        this.id = id; // sets the unique identifier of the appointment
    }

    public Doctor getDoctor() {
        return doctor; // returns the assigned doctor
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor; // sets the assigned doctor
    }

    public Patient getPatient() {
        return patient; // returns the assigned patient
    }

    public void setPatient(Patient patient) {
        this.patient = patient; // sets the assigned patient
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime; // returns the appointment start time
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime; // sets the appointment start time
    }

    public Integer getStatus() {
        return status; // returns the current status of the appointment
    }

    public void setStatus(Integer status) {
        this.status = status; // sets the current status of the appointment
    }

    @Transient // not persisted in the database
    public LocalDateTime getEndTime() {
        return appointmentTime.plusHours(1); // calculates end time one hour after start
    }

    @Transient // not persisted in the database
    public LocalDate getAppointmentDate() {
        return appointmentTime.toLocalDate(); // extracts the date part
    }

    @Transient // not persisted in the database
    public LocalTime getAppointmentTimeOnly() {
        return appointmentTime.toLocalTime(); // extracts the time part
    }
}
