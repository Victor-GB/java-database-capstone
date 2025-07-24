// adminDashboard.js
// Handles admin dashboard: loading, filtering, and adding doctors

// Import required modules
import { openModal } from '../components/modals.js'; // Modal for add doctor form
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js'; // API service functions
import { createDoctorCard } from '../components/doctorCard.js'; // UI component for doctor cards

/**
 * Fetch and display all doctor cards on the dashboard
 */
async function loadDoctorCards() {
  try {
    const contentDiv = document.getElementById('content'); // Main content area
    contentDiv.innerHTML = ''; // Clear existing cards

    const doctors = await getDoctors(); // Fetch all doctors
    doctors.forEach(doctor => {
      const card = createDoctorCard(doctor); // Create card element
      contentDiv.appendChild(card); // Add to page
    });
  } catch (error) {
    console.error('Error loading doctor cards:', error);
  }
}

/**
 * Render a list of doctor cards from provided array
 * @param {Array} doctors - Array of doctor objects
 */
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById('content');
  contentDiv.innerHTML = ''; // Clear current display
  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

/**
 * Handler for search and filter inputs; fetches and displays filtered results
 */
async function filterDoctorsOnChange() {
  try {
    const name = document.getElementById('searchBar').value || null;
    const time = document.getElementById('filterTime').value || null;
    const specialty = document.getElementById('filterSpecialty').value || null;

    const result = await filterDoctors(name, time, specialty); // Fetch filtered
    if (result.doctors.length > 0) {
      renderDoctorCards(result.doctors);
    } else {
      document.getElementById('content').innerHTML = '<p>No doctors found with the given filters.</p>';
    }
  } catch (error) {
    console.error('Error filtering doctors:', error);
    alert('An error occurred while filtering doctors.');
  }
}

/**
 * Collect form data and add a new doctor via the service
 */
async function adminAddDoctor() {
  // Get form inputs
  const name = document.getElementById('doctorName').value;
  const email = document.getElementById('doctorEmail').value;
  const phone = document.getElementById('doctorPhone').value;
  const password = document.getElementById('doctorPassword').value;
  const specialty = document.getElementById('doctorSpecialty').value;
  const times = Array.from(document.querySelectorAll('input[name="availability"]:checked')).map(cb => cb.value);

  const token = localStorage.getItem('token'); // Admin auth token
  if (!token) {
    alert('You must be logged in as admin to add doctors.');
    return;
  }

  const doctor = { name, email, phone, password, specialization: specialty, availableTimes: times };
  const response = await saveDoctor(doctor, token); // Save via API

  if (response.success) {
    alert('Doctor added successfully.');
    openModal('addDoctor'); // Close modal assumed
    loadDoctorCards(); // Refresh list
  } else {
    alert(`Failed to add doctor: ${response.message}`);
  }
}

// Attach event listeners after DOM load
window.addEventListener('DOMContentLoaded', () => {
  // Load initial cards
  loadDoctorCards();

  // Add Doctor button opens modal
  const addBtn = document.getElementById('addDocBtn');
  if (addBtn) addBtn.addEventListener('click', () => openModal('addDoctor'));

  // Search and filter inputs
  const searchBar = document.getElementById('searchBar');
  const filterTime = document.getElementById('filterTime');
  const filterSpecialty = document.getElementById('filterSpecialty');
  if (searchBar) searchBar.addEventListener('input', filterDoctorsOnChange);
  if (filterTime) filterTime.addEventListener('change', filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener('change', filterDoctorsOnChange);

  // Modal form submission
  const addDoctorForm = document.getElementById('addDoctorForm');
  if (addDoctorForm) addDoctorForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    await adminAddDoctor();
  });
});

// Export functions if needed elsewhere
export { loadDoctorCards, filterDoctorsOnChange, adminAddDoctor };
