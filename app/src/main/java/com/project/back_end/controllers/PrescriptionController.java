package com.project.back_end.controllers;

import com.project.back_end.entity.Prescription;
import com.project.back_end.service.PrescriptionService;
import com.project.back_end.service.SharedService;
import com.project.back_end.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("${api.path}prescription")` to set the base path for all prescription-related endpoints.
//    - This controller manages creating and retrieving prescriptions tied to appointments.
@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    // 2. Autowire Dependencies:
    //    - Inject `PrescriptionService` to handle logic related to saving and fetching prescriptions.
    //    - Inject the shared `Service` class for token validation and role-based access control.
    //    - Inject `AppointmentService` to update appointment status after a prescription is issued.
    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private SharedService sharedService;

    @Autowired
    private AppointmentService appointmentService;

    // 3. Define the `savePrescription` Method:
    //    - Handles HTTP POST requests to save a new prescription for a given appointment.
    //    - Accepts a validated `Prescription` object in the request body and a doctor’s token as a path variable.
    //    - Validates the token for the `"doctor"` role.
    //    - If the token is valid, updates the status of the corresponding appointment to reflect that a prescription has been added.
    //    - Delegates the saving logic to `PrescriptionService` and returns a response indicating success or failure.
    @PostMapping("/save/{token}")
    public ResponseEntity<?> savePrescription(
            @Valid @RequestBody Prescription prescription,
            @PathVariable String token
    ) {
        if (!sharedService.validateToken("doctor", token)) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        // Update appointment status to 'prescribed'
        appointmentService.changeStatus(
                prescription.getAppointment().getId(),
                Prescription.PRESCRIBED_STATUS
        );
        Prescription saved = prescriptionService.savePrescription(prescription);
        return ResponseEntity.ok(saved);
    }

    // 4. Define the `getPrescription` Method:
    //    - Handles HTTP GET requests to retrieve a prescription by its associated appointment ID.
    //    - Accepts the appointment ID and a doctor’s token as path variables.
    //    - Validates the token for the `"doctor"` role using the shared service.
    //    - If the token is valid, fetches the prescription using the `PrescriptionService`.
    //    - Returns the prescription details or an appropriate error message if validation fails.
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token
    ) {
        if (!sharedService.validateToken("doctor", token)) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        Prescription prescription = prescriptionService.getByAppointmentId(appointmentId);
        if (prescription == null) {
            return ResponseEntity.status(404).body("Prescription not found");
        }
        return ResponseEntity.ok(prescription);
    }
}
