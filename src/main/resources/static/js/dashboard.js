
async function loadDashboard() {

    // 1. Find HTML elements
    const totalTables =
        document.getElementById('totalTables');

    const activeOrders =
        document.getElementById('activeOrders');

    const todayPayments =
        document.getElementById('todayPayments');

    const dashboardError =
        document.getElementById('dashboardError');

    try {

        // 2. Request dashboard data from Spring Boot
        const response = await fetch('/dashboard/summary');

        // 3. Check HTTP response
        if (!response.ok) {
            throw new Error('Failed to load dashboard data.');
        }

        // 4. Convert JSON response into JavaScript object
        const data = await response.json();

        // 5. Validate and display dashboard data
        const tables = Number(data.totalTables);
        const orders = Number(data.activeOrders);
        const payments = Number(data.todayPayments);

        if (
            !Number.isFinite(tables) ||
            !Number.isFinite(orders) ||
            !Number.isFinite(payments)
        ) {
            throw new Error('Invalid dashboard data received.');
        }

        totalTables.textContent = tables;
        activeOrders.textContent = orders;
        todayPayments.textContent = payments.toFixed(2);

        // 6. Hide error message after successful loading
        dashboardError.hidden = true;
        dashboardError.textContent = '';

    } catch (error) {

        // 7. Display error in browser console
        console.error('Dashboard loading error:', error);

        // 8. Display error message on the page
        totalTables.textContent = '-';
        activeOrders.textContent = '-';
        todayPayments.textContent = '-';

        dashboardError.textContent =
            'Unable to load dashboard data. Please try again.';

        dashboardError.hidden = false;
    }
}

// 9. Load dashboard when the page opens
loadDashboard();
