# Smart Clinic Application Architecture

## 1. Architecture Summary

This Spring Boot application follows a layered, “hexagonal” style architecture that cleanly separates concerns between presentation, business logic, and data access.

- **Presentation Layer**  
  - **Thymeleaf (MVC) Controllers** render server‑side HTML for the Admin and Doctor dashboards.  
  - **REST Controllers** expose JSON APIs consumed by modules like Appointments, PatientDashboard, and PatientRecord (e.g. mobile clients).

- **Service Layer**  
  All controllers delegate to a common Service Layer, which encapsulates business rules (doctor availability checks, validation, workflow coordination).

- **Data Access Layer**  
  - **MySQL Repositories** (Spring Data JPA) manage structured entities: Patient, Doctor, Appointment, Admin.  
  - **MongoDB Repository** (Spring Data MongoDB) manages document‑style data: Prescription.

Each repository interacts with its respective database engine. Data fetched are bound into Java model classes—JPA entities for MySQL and @Document‑annotated classes for MongoDB—and returned up the stack. Controllers then either inject those models into Thymeleaf templates (HTML) or serialize them (or DTOs) into JSON responses, completing the request–response cycle.

---

## 2. Numbered Flow of Data & Control

1. **User Interaction**  
   A user opens the Admin or Doctor dashboard in a browser (Thymeleaf) or invokes an API call from a client module (Appointments, PatientDashboard, PatientRecord).

2. **Routing to Controllers**  
   The request is routed—based on URL path and HTTP method—to either a Thymeleaf Controller (HTML views) or a REST Controller (JSON APIs).

3. **Service Layer Invocation**  
   The selected controller delegates to the Service Layer, passing along any parameters or payloads.

4. **Business Logic Execution**  
   The Service Layer applies business rules (e.g., input validation, doctor availability checks, multi-entity workflow coordination).

5. **Repository Calls**  
   Depending on the data required, the Service Layer calls either a MySQL Repository (JPA) or the MongoDB Repository to fetch or persist data.

6. **Database Access & Model Binding**  
   - **MySQL:** JPA entities ↔ relational tables  
   - **MongoDB:** @Document classes ↔ BSON/JSON collections  
   Retrieved data are mapped into Java model objects.

7. **Response Rendering**  
   - **MVC Flows:** Models are injected into Thymeleaf templates to render dynamic HTML.  
   - **REST Flows:** Models (or mapped DTOs) are serialized into JSON and returned in the HTTP response.
