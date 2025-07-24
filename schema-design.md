# Data Structure Design for Smart Clinic Management System

This document outlines the proposed database schema for the Smart Clinic Management System, using both MySQL (relational) and MongoDB (document-based) databases. It explains the rationale for structuring data in each store, and details the tables and collections with columns/fields, types, relationships, and constraints.

---

## MySQL Database Design

Structured, interrelated data (core operational entities) is stored in MySQL for strong consistency and relational integrity.

### Table: patients

* **id**: INT, PRIMARY KEY, AUTO\_INCREMENT
* **first\_name**: VARCHAR(50), NOT NULL
* **last\_name**: VARCHAR(50), NOT NULL
* **email**: VARCHAR(100), NOT NULL, UNIQUE
* **phone**: VARCHAR(20), NULL
* **date\_of\_birth**: DATE, NULL
* **created\_at**: DATETIME, NOT NULL, DEFAULT CURRENT\_TIMESTAMP

> **Notes:** Appointments reference this table via `patient_id`. Consider soft-delete (e.g., an `is_active` flag) to retain historical data.

### Table: doctors

* **id**: INT, PRIMARY KEY, AUTO\_INCREMENT
* **first\_name**: VARCHAR(50), NOT NULL
* **last\_name**: VARCHAR(50), NOT NULL
* **email**: VARCHAR(100), NOT NULL, UNIQUE
* **specialty**: VARCHAR(100), NOT NULL
* **phone**: VARCHAR(20), NULL
* **office\_location**: VARCHAR(150), NULL
* **created\_at**: DATETIME, NOT NULL, DEFAULT CURRENT\_TIMESTAMP
* **updated\_at**: DATETIME, NOT NULL, DEFAULT CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP

> **Notes:** Prevent overlapping appointments via business logic or a unique index on `(doctor_id, start_time)`.

### Table: appointments

* **id**: INT, PRIMARY KEY, AUTO\_INCREMENT
* **doctor\_id**: INT, NOT NULL, FOREIGN KEY → doctors(id)
* **patient\_id**: INT, NOT NULL, FOREIGN KEY → patients(id)
* **start\_time**: DATETIME, NOT NULL
* **end\_time**: DATETIME, NOT NULL
* **status**: ENUM('SCHEDULED','COMPLETED','CANCELLED'), NOT NULL, DEFAULT 'SCHEDULED'
* **created\_at**: DATETIME, NOT NULL, DEFAULT CURRENT\_TIMESTAMP

> **Notes:** `ON DELETE CASCADE` for `patient_id` or `doctor_id` can auto-clean orphaned appointments, but soft delete is often preferable.

### Table: admin

* **id**: INT, PRIMARY KEY, AUTO\_INCREMENT
* **username**: VARCHAR(50), NOT NULL, UNIQUE
* **password\_hash**: VARCHAR(255), NOT NULL
* **email**: VARCHAR(100), NOT NULL, UNIQUE
* **role**: ENUM('SUPER\_ADMIN','STAFF'), NOT NULL, DEFAULT 'STAFF'
* **created\_at**: DATETIME, NOT NULL, DEFAULT CURRENT\_TIMESTAMP

> **Notes:** Store hashed passwords and enforce strong password policies. Consider auditing login attempts.

### Table: clinic\_locations

* **id**: INT, PRIMARY KEY, AUTO\_INCREMENT
* **name**: VARCHAR(100), NOT NULL
* **address**: VARCHAR(200), NOT NULL
* **phone**: VARCHAR(20), NULL
* **created\_at**: DATETIME, NOT NULL, DEFAULT CURRENT\_TIMESTAMP

> **Notes:** Useful if the clinic has multiple branches.

### Table: payments

* **id**: INT, PRIMARY KEY, AUTO\_INCREMENT
* **appointment\_id**: INT, NOT NULL, FOREIGN KEY → appointments(id)
* **amount**: DECIMAL(10,2), NOT NULL
* **method**: ENUM('CREDIT\_CARD','CASH','INSURANCE'), NOT NULL
* **status**: ENUM('PENDING','PAID','FAILED'), NOT NULL, DEFAULT 'PENDING'
* **payment\_date**: DATETIME, NULL

> **Notes:** One-to-one with appointments; use `ON DELETE CASCADE` if appointment is removed.

### Table: doctor\_availability

* **id**: INT, PRIMARY KEY, AUTO\_INCREMENT
* **doctor\_id**: INT, NOT NULL, FOREIGN KEY → doctors(id)
* **day\_of\_week**: TINYINT, NOT NULL
* **start\_time**: TIME, NOT NULL
* **end\_time**: TIME, NOT NULL

> **Notes:** Defines recurring weekly availability. Prevent overlaps via unique constraint on `(doctor_id, day_of_week, start_time)`.

---

## MongoDB Collection Design

Flexible or semi-structured data (notes, feedback, prescriptions, logs) is stored in MongoDB to allow evolving schemas and nested structures.

### Collection: prescriptions

```json
{
  "_id": ObjectId("64abc1234567890abcdef1234"),
  "patient_id": 101,
  "appointment_id": 51,
  "medications": [
    { "name": "Paracetamol", "dosage": "500mg", "frequency": "Every 6 hours", "duration": "5 days" },
    { "name": "Ibuprofen",   "dosage": "200mg", "frequency": "Every 8 hours", "duration": "3 days" }
  ],
  "doctor_notes": "Advise rest and hydration.",
  "refill_count": 2,
  "prescribed_at": ISODate("2025-07-23T10:15:00Z"),
  "pharmacy": { "name": "Walgreens SF", "address": "123 Market St" }
}
```

> **Notes:** Embedding `medications` as an array supports varying numbers of entries. Referential fields (`patient_id`, `appointment_id`) link back to MySQL.

### Collection: feedback

```json
{
  "_id": ObjectId("64def4567890abcdef1234567"),
  "patient_id": 101,
  "doctor_id": 12,
  "appointment_id": 51,
  "rating": 4.5,
  "comments": "Doctor was very attentive and explained clearly.",
  "tags": ["friendly","informative"],
  "created_at": ISODate("2025-07-24T09:00:00Z")
}
```

> **Notes:** Free-form feedback and tags allow richer insights and filtering.

### Collection: logs

```json
{
  "_id": ObjectId("650000abcdef123456789012"),
  "user_id": 101,
  "user_role": "patient",
  "event_type": "CHECKIN",
  "timestamp": ISODate("2025-07-24T08:45:00Z"),
  "metadata": { "appointment_id": 51, "clinic_location": "Main Branch" }
}
```

> **Notes:** Audit trail of user actions; schema can evolve with new event types and metadata.

---

*Design rationale:*

* **MySQL** handles the core transactional data with strict schemas, referential integrity, and ACID guarantees.
* **MongoDB** stores flexible, nested documents that evolve quickly (prescriptions, feedback, logs) without costly migrations.
* Referential links (e.g., `patient_id`, `appointment_id`) bridge MySQL and MongoDB data when needed.

This schema provides a solid foundation for building models, services, and data access layers in the Smart Clinic application.
