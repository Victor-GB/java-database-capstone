package com.project.back_end.services;

import com.project.back_end.entity.Appointment;
import com.project.back_end.entity.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

// 1. Add @Service Annotation:
//    - Marks this class as a Spring-managed service component for business logic.
@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    // 2. Constructor Injection for Dependencies:
    //    - Injects repositories and token service via constructor for immutability and testability.
    @Autowired
    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService
    ) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 4. getDoctorAvailability Method:
    //    - Retrieves available slots by comparing all slots against booked appointments.
    @Transactional(readOnly = true)
    public List<String> getAvailability(Long doctorId, LocalDate date) {
        LocalDateTime startDay = date.atStartOfDay();
        LocalDateTime endDay = date.atTime(LocalTime.MAX);
        List<Appointment> booked = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, startDay, endDay);
        // Example: generate hourly slots from 9 AM to 5 PM
        List<LocalTime> allSlots = LocalTime.of(9, 0).until(LocalTime.of(17, 0), java.time.Duration.ofHours(1))
                .stream().map(t -> LocalTime.of(9, 0).plusHours(t)).collect(Collectors.toList());
        return allSlots.stream()
                .map(LocalTime::toString)
                .filter(slot -> booked.stream()
                        .map(a -> a.getAppointmentTime().toLocalTime().toString())
                        .noneMatch(bookedTime -> bookedTime.equals(slot)))
                .collect(Collectors.toList());
    }

    // 5. saveDoctor Method:
    //    - Saves a new doctor after checking for existing email to handle conflicts.
    @Transactional
    public int saveDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()).isPresent()) {
            return -1; // conflict
        }
        try {
            doctorRepository.save(doctor);
            return 1;  // success
        } catch (Exception e) {
            return 0;  // error
        }
    }

    // 6. updateDoctor Method:
    //    - Updates an existing doctor; returns -1 if not found.
    @Transactional
    public int updateDoctor(Doctor doctor) {
        if (!doctorRepository.existsById(doctor.getId())) {
            return -1; // not found
        }
        doctorRepository.save(doctor);
        return 1;
    }

    // 7. getDoctors Method:
    //    - Retrieves all doctors; ensures lazy relationships are loaded if needed.
    @Transactional(readOnly = true)
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // 8. deleteDoctor Method:
    //    - Deletes a doctor and all related appointments; returns -1 if not found.
    @Transactional
    public int deleteDoctor(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            return -1;
        }
        appointmentRepository.deleteAllByDoctorId(doctorId);
        doctorRepository.deleteById(doctorId);
        return 1;
    }

    // 9. validateDoctor Method:
    //    - Authenticates a doctor and returns token on success.
    public Map<String, Object> authenticate(String email, String password) {
        return doctorRepository.findByEmail(email)
                .filter(doc -> doc.getPassword().equals(password))
                .map(doc -> {
                    String token = tokenService.generateToken(email);
                    Map<String, Object> result = new HashMap<>();
                    result.put("status", "success");
                    result.put("token", token);
                    return result;
                })
                .orElseGet(() -> {
                    Map<String, Object> error = new HashMap<>();
                    error.put("status", "error");
                    error.put("message", "Invalid credentials");
                    return error;
                });
    }

    // 10. findDoctorByName Method:
    //    - Finds doctors by partial name match.
    @Transactional(readOnly = true)
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameContainingIgnoreCase(name);
    }

    // 11. filterDoctorsByNameSpecilityandTime Method:
    //    - Filters by name, specialty, and availability period (AM/PM).
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByNameSpecialityAndTime(
            String name, String speciality, String period
    ) {
        List<Doctor> filtered = doctorRepository.findByNameContainingIgnoreCaseAndSpeciality(name, speciality);
        return filterByTime(filtered, period);
    }

    // 12. filterDoctorByTime Method:
    //    - Filters doctors by availability period (AM/PM).
    public List<Doctor> filterByTime(List<Doctor> doctors, String period) {
        return doctors.stream().filter(doc ->
                doc.getAvailability().stream().anyMatch(time -> {
                    int hour = LocalTime.parse(time).getHour();
                    if ("AM".equalsIgnoreCase(period)) return hour < 12;
                    else return hour >= 12;
                })
        ).collect(Collectors.toList());
    }

    // 13. filterDoctorByNameAndTime Method:
    //    - Filters doctors by name and availability period.
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndTime(String name, String period) {
        List<Doctor> byName = doctorRepository.findByNameContainingIgnoreCase(name);
        return filterByTime(byName, period);
    }

    // 14. filterDoctorByNameAndSpecility Method:
    //    - Filters doctors by name and specialty.
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndSpeciality(String name, String speciality) {
        return doctorRepository.findByNameContainingIgnoreCaseAndSpeciality(name, speciality);
    }

    // 15. filterDoctorByTimeAndSpecility Method:
    //    - Filters doctors by specialty and availability period.
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByTimeAndSpeciality(String speciality, String period) {
        List<Doctor> bySpec = doctorRepository.findBySpecialityIgnoreCase(speciality);
        return filterByTime(bySpec, period);
    }

    // 16. filterDoctorBySpecility Method:
    //    - Filters doctors by specialty.
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorBySpeciality(String speciality) {
        return doctorRepository.findBySpecialityIgnoreCase(speciality);
    }

    // 17. filterDoctorsByTime Method:
    //    - Filters all doctors by availability period.
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByTime(String period) {
        List<Doctor> all = doctorRepository.findAll();
        return filterByTime(all, period);
    }
}
