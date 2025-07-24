// doctorCard.js
// Imports for booking overlay and API/service functions
import { showBookingOverlay } from './loggedPatient.js'; // Overlay UI for booking
import { deleteDoctor } from './doctorServices.js';      // API call to delete doctor (admin)
import { getPatientDetails } from './patientServices.js'; // Fetch patient data for bookings

/**
 * Creates and returns a DOM element representing a single doctor card.
 * @param {Object} doctor - Doctor data including id, name, specialization, email, availableTimes.
 * @returns {HTMLElement} card - Configured doctor card element.
 */
export function createDoctorCard(doctor) {
  // Main card container
  const card = document.createElement('div');
  card.classList.add('doctor-card'); // Add doctor-card class for styling

  // Determine current user role from localStorage
  const role = localStorage.getItem('userRole'); // 'admin', 'doctor', 'patient', or 'loggedPatient'

  // --- Doctor Info Section ---
  const infoDiv = document.createElement('div');
  infoDiv.classList.add('doctor-info'); // Add doctor-info class for styling details // Wrapper for doctor details

  // Doctor's name element
  const nameEl = document.createElement('h3');
  nameEl.textContent = doctor.name;

  // Doctor's specialization element
  const specEl = document.createElement('p');
  specEl.textContent = `Specialization: ${doctor.specialization}`;

  // Doctor's email element
  const emailEl = document.createElement('p');
  emailEl.textContent = `Email: ${doctor.email}`;

  // List of available appointment times
  const timesList = document.createElement('ul');
  doctor.availableTimes.forEach(time => {
    const li = document.createElement('li');
    li.textContent = time;
    timesList.appendChild(li);
  });

  // Append doctor details to info section
  infoDiv.append(nameEl, specEl, emailEl, timesList);

  // --- Action Buttons Section ---
  const actionsDiv = document.createElement('div');
  actionsDiv.classList.add('card-actions'); // Add card-actions class for actions container // Wrapper for action buttons

  // Admin actions: delete doctor
  if (role === 'admin') {
    const deleteBtn = document.createElement('button');
    deleteBtn.classList.add('delete-btn'); // Style delete button
    deleteBtn.textContent = 'Delete';
    deleteBtn.addEventListener('click', async () => {
      const token = localStorage.getItem('token'); // Admin auth token
      try {
        await deleteDoctor(doctor.id, token); // API call
        card.remove(); // Remove card on success
        alert('Doctor successfully deleted.');
      } catch (error) {
        console.error(error);
        alert('Failed to delete doctor.');
      }
    });
    actionsDiv.appendChild(deleteBtn);

  // Patient not logged-in: prompt login
  } else if (!role || role === 'patient') {
    const bookBtn = document.createElement('button');
    bookBtn.classList.add('book-btn'); // Style book button
    bookBtn.textContent = 'Book Now';
    bookBtn.addEventListener('click', () => {
      alert('Please log in as a patient to book an appointment.');
    });
    actionsDiv.appendChild(bookBtn);

  // Logged-in patient: show booking overlay
  } else if (role === 'loggedPatient') {
    const bookBtn = document.createElement('button');
    bookBtn.className = 'book-btn';
    bookBtn.textContent = 'Book Now';
    bookBtn.addEventListener('click', async () => {
      const token = localStorage.getItem('token'); // Patient auth token
      if (!token) {
        alert('Session expired. Please log in again.');
        return;
      }
      try {
        // Fetch patient details for booking
        const patient = await getPatientDetails(token);
        // Show booking overlay with doctor and patient info
        showBookingOverlay(doctor, patient);
      } catch (error) {
        console.error(error);
        alert('Unable to fetch patient details.');
      }
    });
    actionsDiv.appendChild(bookBtn);
  }

  // Assemble card: info + actions
  card.append(infoDiv, actionsDiv);
  return card;
}
