// Renders the header section based on user role and session status.
function renderHeader() {
    const headerDiv = document.getElementById("header"); // Target header container

    // If on root page, clear session and show basic header
    if (window.location.pathname.endsWith("/")) {
        localStorage.removeItem("userRole");
        headerDiv.innerHTML = `
          <header class="header">
            <div class="logo-section">
              <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
              <span class="logo-title">Hospital CMS</span>
            </div>
          </header>`;
        return;
    }

    const role = localStorage.getItem("userRole");      // User role: admin, doctor, patient
    const token = localStorage.getItem("token");       // Authentication token

    // Base header with logo
    let headerContent = `<header class="header">
      <div class="logo-section">
        <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
      </div>
      <nav>`;

    // If session expired or invalid, clear and prompt login
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    // Add navigation options based on role
    if (role === "admin") {
      headerContent += `
        <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
        <a href="#" onclick="logout()">Logout</a>`;
    } else if (role === "doctor") {
      headerContent += `
        <button class="adminBtn" onclick="selectRole('doctor')">Home</button>
        <a href="#" onclick="logout()">Logout</a>`;
    } else if (role === "patient") {
      headerContent += `
        <button id="patientLogin" class="adminBtn">Login</button>
        <button id="patientSignup" class="adminBtn">Sign Up</button>`;
    } else if (role === "loggedPatient") {
      headerContent += `
        <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
        <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
        <a href="#" onclick="logoutPatient()">Logout</a>`;
    }

    headerContent += `</nav></header>`; // Close header markup
    headerDiv.innerHTML = headerContent; // Inject into page
    attachHeaderButtonListeners();      // Wire up dynamic button events
}

// Adds event listeners to dynamically created header buttons
function attachHeaderButtonListeners() {
    const patientLoginBtn = document.getElementById("patientLogin");
    if (patientLoginBtn) patientLoginBtn.addEventListener("click", () => openModal("patientLogin"));

    const patientSignupBtn = document.getElementById("patientSignup");
    if (patientSignupBtn) patientSignupBtn.addEventListener("click", () => openModal("patientSignup"));

    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) addDocBtn.addEventListener("click", () => openModal("addDoctor"));
}

// Clears session and redirects to home
function logout() {
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

// Clears patient token and redirects to login
function logoutPatient() {
    localStorage.removeItem("token");
    window.location.href = "/pages/patientLogin.html";
}

renderHeader(); // Initialize header on page load
