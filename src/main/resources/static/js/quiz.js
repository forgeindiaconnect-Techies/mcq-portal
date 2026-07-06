(function () {
    const form = document.getElementById("quizForm");
    if (!form) return;

    const cards = Array.from(document.querySelectorAll(".question-card"));
    const prevBtn = document.getElementById("prevBtn");
    const nextBtn = document.getElementById("nextBtn");
    const submitBtn = document.getElementById("submitBtn");
    const timerEl = document.getElementById("timer");
    const progressEl = document.getElementById("quizProgress");
    const warningEl = document.getElementById("malpracticeWarning");
    const warningTextEl = document.getElementById("malpracticeWarningText");
    const warningCountEl = document.getElementById("malpracticeWarningCount");
    const malpracticeDetectedInput = document.getElementById("malpracticeDetected");
    const malpracticeWarningsInput = document.getElementById("malpracticeWarnings");
    const malpracticeReasonInput = document.getElementById("malpracticeReason");
    const storageKey = "fic-quiz-autosave";
    let index = 0;
    let submitted = false;
    let remaining = Number(form.dataset.duration || 30) * 60;
    let malpracticeCount = 0;
    let warningTimer = null;
    let lastFocusWarning = 0;

    function restoreAnswers() {
        const saved = JSON.parse(localStorage.getItem(storageKey) || "{}");
        Object.entries(saved).forEach(([name, value]) => {
            const input = form.querySelector(`input[name="${name}"][value="${value}"]`);
            if (input) input.checked = true;
        });
    }

    function saveAnswers() {
        const data = {};
        form.querySelectorAll("input[type='radio']:checked").forEach(input => data[input.name] = input.value);
        localStorage.setItem(storageKey, JSON.stringify(data));
        updateProgress();
    }

    function updateProgress() {
        const answered = form.querySelectorAll("input[type='radio']:checked").length;
        const pct = cards.length === 0 ? 0 : Math.round((answered / cards.length) * 100);
        progressEl.style.width = pct + "%";
        progressEl.textContent = pct + "%";
    }

    function showCard(nextIndex) {
        index = Math.max(0, Math.min(nextIndex, cards.length - 1));
        cards.forEach((card, cardIndex) => card.classList.toggle("active", cardIndex === index));
        prevBtn.disabled = index === 0;
        nextBtn.classList.toggle("d-none", index === cards.length - 1);
        submitBtn.classList.toggle("d-none", index !== cards.length - 1);
        updateProgress();
    }

    function drawTimer() {
        const minutes = Math.floor(remaining / 60).toString().padStart(2, "0");
        const seconds = (remaining % 60).toString().padStart(2, "0");
        timerEl.textContent = `${minutes}:${seconds}`;
        timerEl.classList.toggle("warning", remaining <= 300);
    }

    function submitForMalpractice(reason) {
        if (submitted) return;
        submitted = true;
        malpracticeDetectedInput.value = "true";
        malpracticeWarningsInput.value = "4";
        malpracticeReasonInput.value = reason || "Repeated restricted quiz activity.";
        localStorage.removeItem(storageKey);
        form.submit();
    }

    function showMalpracticeWarning(message) {
        if (submitted) return;
        malpracticeCount += 1;
        malpracticeWarningsInput.value = malpracticeCount.toString();
        malpracticeReasonInput.value = message;
        if (malpracticeCount >= 4) {
            submitForMalpractice(message);
            return;
        }
        warningTextEl.textContent = message;
        warningCountEl.textContent = `Warning ${malpracticeCount} of 3`;
        warningEl.classList.remove("d-none");
        clearTimeout(warningTimer);
        warningTimer = setTimeout(() => warningEl.classList.add("d-none"), 5000);
    }

    function warnForLeavingQuiz(message) {
        const now = Date.now();
        if (now - lastFocusWarning < 2500) return;
        lastFocusWarning = now;
        showMalpracticeWarning(message);
    }

    form.querySelectorAll("input[type='radio']").forEach(input => input.addEventListener("change", saveAnswers));
    prevBtn.addEventListener("click", () => showCard(index - 1));
    nextBtn.addEventListener("click", () => showCard(index + 1));
    form.addEventListener("submit", function (event) {
        if (submitted) {
            event.preventDefault();
            return;
        }
        submitted = true;
        localStorage.removeItem(storageKey);
        submitBtn.disabled = true;
    });

    document.addEventListener("copy", function (event) {
        event.preventDefault();
        showMalpracticeWarning("Copying quiz questions is not allowed.");
    });

    document.addEventListener("cut", function (event) {
        event.preventDefault();
        showMalpracticeWarning("Copying or cutting quiz content is not allowed.");
    });

    document.addEventListener("contextmenu", function (event) {
        event.preventDefault();
        showMalpracticeWarning("Right-click actions are disabled during the quiz.");
    });

    document.addEventListener("keydown", function (event) {
        const key = event.key.toLowerCase();
        if ((event.ctrlKey || event.metaKey) && ["c", "x", "u", "s", "p"].includes(key)) {
            event.preventDefault();
            showMalpracticeWarning("Keyboard shortcut activity is not allowed during the quiz.");
        }
    });

    document.addEventListener("visibilitychange", function () {
        if (document.hidden) {
            warnForLeavingQuiz("Leaving the quiz tab is not allowed.");
        }
    });

    window.addEventListener("blur", function () {
        warnForLeavingQuiz("Opening another tab or window during the quiz is not allowed.");
    });

    restoreAnswers();
    showCard(0);
    drawTimer();
    setInterval(function () {
        if (submitted) return;
        remaining -= 1;
        drawTimer();
        if (remaining <= 0) {
            submitted = true;
            form.submit();
        }
    }, 1000);
})();
