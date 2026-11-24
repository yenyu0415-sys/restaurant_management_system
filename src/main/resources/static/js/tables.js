
const API_URL = "/dining-tables";
const SESSION_URL = "/dining-sessions";

const tableList = document.getElementById("tableList");
const message = document.getElementById("message");

const totalCount = document.getElementById("totalCount");
const availableCount = document.getElementById("availableCount");
const occupiedCount = document.getElementById("occupiedCount");

const refreshButton = document.getElementById("refreshButton");

// =========================
// Load Tables and Sessions
// =========================
async function loadTables(preserveMessage = false) {

    tableList.textContent = "Loading tables...";

    if (!preserveMessage) {
        message.textContent = "";
        message.className = "table-message";
    }

    refreshButton.disabled = true;

    try {

        const tableResponse = await fetch(API_URL);

        if (!tableResponse.ok) {
            throw new Error("Failed to load tables.");
        }

        const tables = await tableResponse.json();

        const sessionResponse = await fetch(SESSION_URL);

        if (!sessionResponse.ok) {
            throw new Error("Failed to load dining sessions.");
        }

        const sessions = await sessionResponse.json();

        if (!Array.isArray(tables) || !Array.isArray(sessions)) {
            throw new Error("Invalid API response.");
        }

        displayTables(tables, sessions);

    } catch (error) {

        console.error("Table loading error:", error);

        tableList.textContent = "Unable to load tables.";

        showMessage(error.message, "error");

    } finally {

        refreshButton.disabled = false;
    }
}

// =========================
// Display Tables
// =========================
function displayTables(tables, sessions) {

    tableList.innerHTML = "";

    let available = 0;
    let occupied = 0;

    totalCount.textContent = tables.length;

    tables.forEach(table => {

        if (table.status === "AVAILABLE") {
            available++;
        }

        if (table.status === "OCCUPIED") {
            occupied++;
        }

        const activeSession = sessions.find(session =>
            Number(session.tableId) === Number(table.tableId) &&
            session.status === "ACTIVE"
        );

        const card = document.createElement("div");
        card.className = "table-card";

        const title = document.createElement("h3");
        title.textContent = table.tableNumber;

        const capacity = document.createElement("p");
        capacity.textContent = `Capacity: ${table.capacity} people`;

        const status = document.createElement("p");
        status.textContent = table.status;
        status.className = "table-status";

        if (table.status === "AVAILABLE") {
            status.classList.add("available");
        } else if (table.status === "OCCUPIED") {
            status.classList.add("occupied");
        }

        const button = document.createElement("button");

        if (table.status === "AVAILABLE") {

            button.textContent = "Start Session";

            button.addEventListener("click", () => {
                startSession(table.tableId, button);
            });

        } else if (table.status === "OCCUPIED" && activeSession) {

            button.textContent = "End Session";

            button.addEventListener("click", () => {
                endSession(activeSession.sessionId, button);
            });

        } else {

            button.textContent = "No Active Session";
            button.disabled = true;
        }

        card.appendChild(title);
        card.appendChild(capacity);
        card.appendChild(status);
        card.appendChild(button);

        tableList.appendChild(card);
    });

    availableCount.textContent = available;
    occupiedCount.textContent = occupied;

    if (tables.length === 0) {
        tableList.textContent = "No tables found.";
    }
}

// =========================
// Start Dining Session
// =========================
async function startSession(tableId, button) {

    const confirmed = confirm(
        "Do you want to start a dining session for this table?"
    );

    if (!confirmed) {
        return;
    }

    button.disabled = true;
    button.textContent = "Starting...";

    try {

        const response = await fetch(
            `${SESSION_URL}/start/${tableId}`,
            {
                method: "POST"
            }
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(
                errorText || "Failed to start dining session."
            );
        }

        showMessage(
            "Dining session started successfully!",
            "success"
        );

        await loadTables(true);

    } catch (error) {

        console.error("Session start error:", error);

        showMessage(error.message, "error");

        await loadTables(true);
    }
}

// =========================
// End Dining Session
// =========================
async function endSession(sessionId, button) {

    const confirmed = confirm(
        "Do you want to end this dining session?"
    );

    if (!confirmed) {
        return;
    }

    button.disabled = true;
    button.textContent = "Ending...";

    try {

        const response = await fetch(
            `${SESSION_URL}/${sessionId}/end`,
            {
                method: "PATCH"
            }
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(
                errorText || "Failed to end dining session."
            );
        }

        showMessage(
            "Dining session ended successfully!",
            "success"
        );

        await loadTables(true);

    } catch (error) {

        console.error("Session end error:", error);

        showMessage(error.message, "error");

        await loadTables(true);
    }
}

// =========================
// Show Message
// =========================
function showMessage(text, type) {

    message.textContent = text;
    message.className = `table-message ${type}`;
}

// =========================
// Refresh Button
// =========================
refreshButton.addEventListener("click", () => {
    loadTables();
});

// =========================
// Initial Load
// =========================
loadTables();
