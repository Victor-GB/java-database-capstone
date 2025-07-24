// appointmentDashboard.js
// Handles fetching and rendering patient appointment records for doctors

// Import required modules
import { getAllAppointments } from './services/appointmentRecordService.js'; // API service for appointments
import { createPatientRow } from '../components/patientRows.js';            // Component for rendering table rows

// Initialize global variables
const tableBody = document.getElementById('patientTableBody'); // Table body for appointment rows
let selectedDate = new Date().toISOString().split('T')[0];     // Today's date in YYYY-MM-DD
const token = localStorage.getItem('token');                    // Auth token for API calls
let patientName = null;                                          // Search filter by patient name

// Set up search bar functionality
const searchBar = document.getElementById('searchBar');
if (searchBar) {
  searchBar.addEventListener('input', () => {
    const val = searchBar.value.trim();
    patientName = val !== '' ? val : null; // Use null when empty
    loadAppointments();
  });
}

// Bind "Today's Appointments" button
const todayButton = document.getElementById('todayButton');
if (todayButton) {
  todayButton.addEventListener('click', () => {
    selectedDate = new Date().toISOString().split('T')[0]; // Reset to today
    const datePicker = document.getElementById('datePicker');
    if (datePicker) datePicker.value = selectedDate;       // Update picker UI
    loadAppointments();
  });
}

// Bind date picker change
const datePicker = document.getElementById('datePicker');
if (datePicker) {
  datePicker.value = selectedDate;
  datePicker.addEventListener('change', () => {
    selectedDate = datePicker.value; // Update selected date
    loadAppointments();
  });
}

/**
 * Fetches and displays appointments based on selectedDate and patientName filter
 */
async function loadAppointments() {
  try {
    // Fetch appointments from backend
    const appointments = await getAllAppointments(selectedDate, patientName, token);

    // Clear existing rows
    tableBody.innerHTML = '';

    if (!appointments || appointments.length === 0) {
      // Show no appointments message
      const row = document.createElement('tr');
      row.innerHTML = `<td colspan="5">No Appointments found for ${selectedDate}.</td>`;
      tableBody.appendChild(row);
      return;
    }

    // Render each appointment row
    appointments.forEach(appt => {
      const patient = {
        id: appt.patientId,
        name: appt.patientName,
        phone: appt.patientPhone,
        email: appt.patientEmail
      };
      const row = createPatientRow(appt.id, patient, appt.time);
      tableBody.appendChild(row);
    });
  } catch (error) {
    console.error('Error loading appointments:', error);
    // Show error message row
    const row = document.createElement('tr');
    row.innerHTML = `<td colspan="5">Error loading appointments. Try again later.</td>`;
    tableBody.appendChild(row);
  }
}

// Initial render on page load
window.addEventListener('DOMContentLoaded', () => {
  if (typeof renderContent === 'function') renderContent(); // Setup layout if defined
  loadAppointments();                                          // Load today's appointments
});
