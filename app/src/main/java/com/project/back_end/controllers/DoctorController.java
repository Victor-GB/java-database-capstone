package com.project.back_end.controllers;

import com.project.back_end.entity.Doctor;
import com.project.back_end.dto.Login;
import com.project.back_end.service.DoctorService;
import com.project.back_end.service.SharedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
//    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
//    - This class manages doctor-related functionalities such as registration, login, updates, and availability.
@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    // 2. Autowire Dependencies:
    //    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
    //    - Inject the shared `Service` class for general-purpose features like token validation and filtering.
    @Autowired
    private DoctorService doctorService;

    @Autowired
    private SharedService sharedService;

    // 3. Define the `getDoctorAvailability` Method:
    //    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
    //    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
    //    - First validates the token against the user type.
    //    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.
    @GetMapping("/availability/{userType}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String userType,
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String token
    ) {
        if (!sharedService.validateToken(userType, token)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid token or unauthorized");
            return ResponseEntity.status(401).body(error);
        }
        List<String> availability = doctorService.getAvailability(doctorId, date);
        return ResponseEntity.ok(availability);
    }

    // 4. Define the `getDoctor` Method:
    //    - Handles HTTP GET requests to retrieve a list of all doctors.
    //    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.
    @GetMapping
    public ResponseEntity<Map<String, List<Doctor>>> getDoctor() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        Map<String, List<Doctor>> response = new HashMap<>();
        response.put("doctors", doctors);
        return ResponseEntity.ok(response);
    }

    // 5. Define the `saveDoctor` Method:
    //    - Handles HTTP POST requests to register a new doctor.
    //    - Accepts a validated `Doctor` object in the request body and a token for authorization.
    //    - Validates the token for the `"admin"` role before proceeding.
    //    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.
    @PostMapping
    public ResponseEntity<?> saveDoctor(
            @Valid @RequestBody Doctor doctor,
            @RequestParam String token
    ) {
        if (!sharedService.validateToken("admin", token)) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        if (doctorService.existsByUsername(doctor.getUsername())) {
            return ResponseEntity.status(409).body("Doctor already exists");
        }
        doctorService.registerDoctor(doctor);
        return ResponseEntity.ok("Doctor registered successfully");
    }

    // 6. Define the `doctorLogin` Method:
    //    - Handles HTTP POST requests for doctor login.
    //    - Accepts a validated `Login` DTO containing credentials.
    //    - Delegates authentication to the `DoctorService` and returns login status and token information.
    @PostMapping("/login")
    public ResponseEntity<?> doctorLogin(
            @Valid @RequestBody Login login
    ) {
        Map<String, Object> authResult = doctorService.authenticate(login.getEmail(), login.getPassword());
        return ResponseEntity.ok(authResult);
    }

    // 7. Define the `updateDoctor` Method:
    //    - Handles HTTP PUT requests to update an existing doctor's information.
    //    - Accepts a validated `Doctor` object and a token for authorization.
    //    - Token must belong to an `"admin"`.
    //    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.
    @PutMapping
    public ResponseEntity<?> updateDoctor(
            @Valid @RequestBody Doctor doctor,
            @RequestParam String token
    ) {
        if (!sharedService.validateToken("admin", token)) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        boolean updated = doctorService.updateDoctor(doctor);
        if (!updated) {
            return ResponseEntity.status(404).body("Doctor not found");
        }
        return ResponseEntity.ok("Doctor updated successfully");
    }

    // 8. Define the `deleteDoctor` Method:
    //    - Handles HTTP DELETE requests to remove a doctor by ID.
    //    - Requires both doctor ID and an admin token as path variables.
    //    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.
    @DeleteMapping("/{doctorId}/{token}")
    public ResponseEntity<?> deleteDoctor(
            @PathVariable Long doctorId,
            @PathVariable String token
    ) {
        if (!sharedService.validateToken("admin", token)) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        boolean deleted = doctorService.deleteDoctor(doctorId);
        if (!deleted) {
            return ResponseEntity.status(404).body("Doctor not found");
        }
        return ResponseEntity.ok("Doctor deleted successfully");
    }

    // 9. Define the `filter` Method:
    //    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
    //    - Accepts `name`, `time`, and `speciality` as path variables.
    //    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, List<Doctor>>> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality
    ) {
        List<Doctor> filtered = sharedService.filterDoctors(name, time, speciality);
        Map<String, List<Doctor>> response = new HashMap<>();
        response.put("doctors", filtered);
        return ResponseEntity.ok(response);
    }
}
