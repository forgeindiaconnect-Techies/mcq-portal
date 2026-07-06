(function () {
    const storedTheme = localStorage.getItem("fic-theme");
    if (storedTheme === "dark") {
        document.body.classList.add("dark-mode");
    }

    const toggle = document.getElementById("darkModeToggle");
    if (toggle) {
        toggle.addEventListener("click", function () {
            document.body.classList.toggle("dark-mode");
            localStorage.setItem("fic-theme", document.body.classList.contains("dark-mode") ? "dark" : "light");
        });
    }

    document.querySelectorAll(".needs-validation").forEach(function (form) {
        form.addEventListener("submit", function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add("was-validated");
        });
    });
})();
