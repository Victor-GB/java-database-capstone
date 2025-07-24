package com.project.back_end.services;

import com.project.back_end.entity.Appointment;
import com.project.back_end.repository.AppointmentRepository;
import com.project.back_end.repository.PatientRepository;
import com.project.back_end.repository.DoctorRepository;
import com.project.back_end.service.SharedService;
import com.project.back_end.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final SharedService sharedService;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            SharedService sharedService,
            TokenService tokenService,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.sharedService = sharedService;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // 4. Book Appointment Method:
    //    - Responsible for saving the new appointment to the database.
    //    - If the save operation fails, it returns 0; otherwise, it returns 1.
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            // log error
            return 0;
        }
    }

    // 5. Update Appointment Method:
    //    - Updates an existing appointment based on its ID.
    //    - Validates patient ownership, ensures doctor availability, and handles errors.
    @Transactional
    public String updateAppointment(Long appointmentId, Appointment updatedAppointment) {
        Optional<Appointment> existingOpt = appointmentRepository.findById(appointmentId);
        if (!existingOpt.isPresent()) {
            return "Appointment not found";
        }
        Appointment existing = existingOpt.get();
        // Check patient ownership
        if (!existing.getPatient().getId().equals(updatedAppointment.getPatient().getId())) {
            return "Unauthorized: patient mismatch";
        }
        // Check doctor availability for the new time slot
        LocalDateTime start = updatedAppointment.getAppointmentTime();
        LocalDateTime end = start.plusHours(1);
        List<Appointment> conflicts = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(
                        updatedAppointment.getDoctor().getId(), start, end);
        boolean hasConflict = conflicts.stream()
                .anyMatch(a -> !a.getId().equals(appointmentId));
        if (hasConflict) {
            return "Doctor not available at this time";
        }
        existing.setAppointmentTime(updatedAppointment.getAppointmentTime());
        existing.setDoctor(updatedAppointment.getDoctor());
        existing.setStatus(updatedAppointment.getStatus());
        appointmentRepository.save(existing);
        return "Appointment updated successfully";
    }

    // 6. Cancel Appointment Method:
    //    - Cancels an appointment by deleting it from the database.
    //    - Verifies the patient ownership before deletion.
    @Transactional
    public String cancelAppointment(Long appointmentId, Long patientId) {
        Optional<Appointment> existingOpt = appointmentRepository.findById(appointmentId);
        if (!existingOpt.isPresent()) {
            return "Appointment not found";
        }
        Appointment existing = existingOpt.get();
        if (!existing.getPatient().getId().equals(patientId)) {
            return "Unauthorized: patient mismatch";
        }
        appointmentRepository.delete(existing);
        return "Appointment canceled successfully";
    }

    // 7. Get Appointments Method:
    //    - Retrieves appointments for a specific doctor on a given date,
    //      optionally filtered by patient name.
    @Transactional(readOnly = true)
    public List<Appointment> getAppointments(Long doctorId, LocalDate date, String patientName) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        if (patientName == null || patientName.isEmpty()) {
            return appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        } else {
            return appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                            doctorId, patientName, start, end);
        }
    }

    // 8. Change Status Method:
    //    - Updates the status of an appointment.
    @Transactional
    public String changeStatus(Long appointmentId, int status) {
        Optional<Appointment> existingOpt = appointmentRepository.findById(appointmentId);
        if (!existingOpt.isPresent()) {
            return "Appointment not found";
        }
        Appointment existing = existingOpt.get();
        existing.setStatus(status);
        appointmentRepository.save(existing);
        return "Status updated successfully";
    }
}
