// services/index.js
// Handles login functionality for Admin and Doctor roles using modals and API calls

// Import modal function to show login forms
import { openModal } from '../components/modals.js';
// Import base URL for API endpoints
import { API_BASE_URL } from '../config/config.js';

// Define API endpoints for admin and doctor login
const ADMIN_API = `${API_BASE_URL}/admin`;            // Admin authentication endpoint
const DOCTOR_API = `${API_BASE_URL}/doctor/login`;   // Doctor authentication endpoint

// Wait until the DOM is fully loaded to attach event listeners
window.onload = function () {
  // Select login buttons by their IDs
  const adminBtn = document.getElementById('adminLogin');
  const doctorBtn = document.getElementById('doctorLogin');

  // If Admin Login button exists, open admin login modal on click
  if (adminBtn) {
    adminBtn.addEventListener('click', () => openModal('adminLogin'));
  }
  // If Doctor Login button exists, open doctor login modal on click
  if (doctorBtn) {
    doctorBtn.addEventListener('click', () => openModal('doctorLogin'));
  }
};

/**
 * Handles Admin login form submission.
 * Reads credentials, sends POST to ADMIN_API, stores token, and sets role.
 */
window.adminLoginHandler = async function () {
  try {
    // Retrieve input values for username and password
    const username = document.getElementById('adminUsername').value;
    const password = document.getElementById('adminPassword').value;
    const admin = { username, password };

    // Send credentials to server
    const response = await fetch(ADMIN_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(admin)
    });

    if (response.ok) {
      // On success, parse JSON and store token
      const { token } = await response.json();
      localStorage.setItem('token', token);
      selectRole('admin'); // Proceed with admin-specific UI
    } else {
      // Invalid credentials
      alert('Invalid Admin credentials!');
    }
  } catch (error) {
    // Network or server error
    console.error('Admin login error:', error);
    alert('An error occurred during Admin login. Please try again.');
  }
};

/**
 * Handles Doctor login form submission.
 * Reads credentials, sends POST to DOCTOR_API, stores token, and sets role.
 */
window.doctorLoginHandler = async function () {
  try {
    // Retrieve input values for email and password
    const email = document.getElementById('doctorEmail').value;
    const password = document.getElementById('doctorPassword').value;
    const doctor = { email, password };

    // Send credentials to server
    const response = await fetch(DOCTOR_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(doctor)
    });

    if (response.ok) {
      // On success, parse JSON and store token
      const { token } = await response.json();
      localStorage.setItem('token', token);
      selectRole('doctor'); // Proceed with doctor-specific UI
    } else {
      // Invalid credentials
      alert('Invalid Doctor credentials!');
    }
  } catch (error) {
    // Network or server error
    console.error('Doctor login error:', error);
    alert('An error occurred during Doctor login. Please try again.');
  }
};
