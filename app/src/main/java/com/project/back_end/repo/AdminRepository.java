package com.project.back_end.repo;

import com.project.back_end.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Admin entities, providing CRUD operations and custom query methods.
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * Finds an Admin entity by username.
     *
     * @param username the username of the Admin
     * @return the matching Admin, or null if none found
     */
    Admin findByUsername(String username);
}
