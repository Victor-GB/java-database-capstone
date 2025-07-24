// doctorServices.js
// Provides functions to interact with doctor-related API endpoints

// Import base API URL from configuration
import { API_BASE_URL } from '../config/config.js';

// Base endpoint for doctor operations
const DOCTOR_API = `${API_BASE_URL}/doctor`;

/**
 * Fetches the list of all doctors from the API.
 * @returns {Promise<Array>} Array of doctor objects or empty array on error.
 */
export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API); // GET all doctors
    const data = await response.json();
    return data.doctors || [];
  } catch (error) {
    console.error('Error fetching doctors:', error);
    return [];
  }
}

/**
 * Deletes a specific doctor by ID, using an authentication token.
 * @param {string} id - Doctor's unique identifier.
 * @param {string} token - Authentication token.
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function deleteDoctor(id, token) {
  try {
    const url = `${DOCTOR_API}/${encodeURIComponent(id)}/${encodeURIComponent(token)}`;
    const response = await fetch(url, { method: 'DELETE' });
    const result = await response.json();
    return {
      success: response.ok,
      message: result.message || 'Doctor deletion status unknown.'
    };
  } catch (error) {
    console.error('Error deleting doctor:', error);
    return { success: false, message: 'Network or server error during deletion.' };
  }
}

/**
 * Saves (creates) a new doctor record via POST request.
 * @param {Object} doctor - Doctor data to save.
 * @param {string} token - Authentication token.
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function saveDoctor(doctor, token) {
  try {
    const url = `${DOCTOR_API}/${encodeURIComponent(token)}`;
    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(doctor)
    });
    const result = await response.json();
    return {
      success: response.ok,
      message: result.message || 'Doctor save status unknown.'
    };
  } catch (error) {
    console.error('Error saving doctor:', error);
    return { success: false, message: 'Network or server error during save.' };
  }
}

/**
 * Fetches doctors filtered by name, time, and specialty.
 * @param {string} name - Name filter (partial or full).
 * @param {string} time - Available time filter.
 * @param {string} specialty - Medical specialty filter.
 * @returns {Promise<{doctors: Array}>} Filtered doctors or empty array on error.
 */
export async function filterDoctors(name, time, specialty) {
  try {
    const url = `${DOCTOR_API}/filter/${encodeURIComponent(name)}/${encodeURIComponent(time)}/${encodeURIComponent(specialty)}`;
    const response = await fetch(url);
    if (response.ok) {
      const data = await response.json();
      return { doctors: data.doctors || [] };
    } else {
      console.error('Filtering doctors failed:', response.statusText);
      return { doctors: [] };
    }
  } catch (error) {
    console.error('Error filtering doctors:', error);
    alert('Unable to fetch filtered doctors. Please try again later.');
    return { doctors: [] };
  }
}
