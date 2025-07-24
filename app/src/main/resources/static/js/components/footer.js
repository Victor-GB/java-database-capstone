// Dynamically generates and inserts the footer into the page
function renderFooter() {
    const footer = document.getElementById("footer"); // Select footer container
    if (!footer) return; // Exit if footer element is not present

    // Build footer HTML structure
    let footerContent = `
    <footer class="footer">
      <div class="footer-container">

        <!-- Logo and copyright -->
        <div class="footer-logo">
          <img src="../assets/images/logo/logo.png" alt="Hospital CMS Logo">
          <p>© 2025 Hospital CMS. All Rights Reserved.</p>
        </div>

        <!-- Footer links grouped by category -->
        <div class="footer-links">

          <!-- Company section -->
          <div class="footer-column">
            <h4>Company</h4>
            <a href="#">About</a>
            <a href="#">Careers</a>
            <a href="#">Press</a>
          </div>

          <!-- Support section -->
          <div class="footer-column">
            <h4>Support</h4>
            <a href="#">Account</a>
            <a href="#">Help Center</a>
            <a href="#">Contact Us</a>
          </div>

          <!-- Legal section -->
          <div class="footer-column">
            <h4>Legals</h4>
            <a href="#">Terms & Conditions</a>
            <a href="#">Privacy Policy</a>
            <a href="#">Licensing</a>
          </div>

        </div> <!-- .footer-links -->

      </div> <!-- .footer-container -->
    </footer>`;

    footer.innerHTML = footerContent; // Insert built HTML into DOM
}

// Initialize footer on page load
renderFooter();
