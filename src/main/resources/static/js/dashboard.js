
async function loadDashboard() {
    try {
        const response = await fetch('/dashboard/summary');

        if (!response.ok) {
            throw new Error('Failed to load dashboard data');
        }

        const data = await response.json();

        document.getElementById('totalTables').textContent =
            data.totalTables;

        document.getElementById('activeOrders').textContent =
            data.activeOrders;

        document.getElementById('todayPayments').textContent =
            Number(data.todayPayments).toFixed(2);

    } catch (error) {
        console.error('Dashboard loading error:', error);
    }
}

// Load dashboard data when the page opens
loadDashboard();
