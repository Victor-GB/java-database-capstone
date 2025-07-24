package com.project.back_end.repo;

import com.project.back_end.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Patient entities, providing CRUD operations and custom query methods.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // 1. Extend JpaRepository:
    //    - The repository extends JpaRepository<Patient, Long>, which provides basic CRUD functionality.
    //    - This allows the repository to perform operations like save, delete, update, and find without needing to implement these methods manually.
    //    - JpaRepository also includes features like pagination and sorting.

    /**
     * Retrieves a Patient entity by email address.
     *
     * @param email the email of the patient
     * @return an Optional containing the matching Patient, or empty if none found
     */
    Optional<Patient> findByEmail(String email);

    /**
     * Retrieves a Patient entity by either email address or phone number.
     *
     * @param email the email of the patient
     * @param phone the phone number of the patient
     * @return an Optional containing the matching Patient, or empty if none found
     */
    Optional<Patient> findByEmailOrPhone(String email, String phone);
}
