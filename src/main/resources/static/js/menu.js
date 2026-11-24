
// ========================================
// Menu Management
// ========================================


// Load All Menus
async function loadMenus() {
    const menuTableBody = document.getElementById('menuTableBody');

    if (!menuTableBody) {
        console.error('Menu table body not found');
        return;
    }

    try {
        const response = await fetch('/menus');

        if (!response.ok) {
            throw new Error('Failed to load menu data');
        }

        const menus = await response.json();

        console.log('Menu data:', menus);

        menuTableBody.innerHTML = '';

        menus.forEach(menu => {
            const row = document.createElement('tr');

            // Menu Name
            const nameCell = document.createElement('td');
            nameCell.textContent = menu.menuName;

            // Category
            const categoryCell = document.createElement('td');
            categoryCell.textContent = menu.category;

            // Price
            const priceCell = document.createElement('td');
            priceCell.textContent = menu.price;

            // Status
            const statusCell = document.createElement('td');
            const statusBadge = document.createElement('span');

            statusBadge.className =
                `status-badge ${menu.active ? 'active' : 'inactive'}`;

            statusBadge.textContent =
                menu.active ? 'Active' : 'Inactive';

            statusCell.appendChild(statusBadge);

            // Actions
            const actionCell = document.createElement('td');

            // Edit Button
            const editButton = document.createElement('button');
            editButton.type = 'button';
            editButton.className = 'edit-btn';
            editButton.textContent = 'Edit';
            editButton.addEventListener('click', () => editMenu(menu.id));

            actionCell.appendChild(editButton);

            // Activate / Deactivate Button
            const statusButton = document.createElement('button');
            statusButton.type = 'button';

            if (menu.active) {
                statusButton.className = 'deactivate-btn';
                statusButton.textContent = 'Deactivate';

                statusButton.addEventListener(
                    'click',
                    () => deactivateMenu(menu.id)
                );
            } else {
                statusButton.className = 'activate-btn';
                statusButton.textContent = 'Activate';

                statusButton.addEventListener(
                    'click',
                    () => activateMenu(menu.id)
                );
            }

            actionCell.appendChild(statusButton);

            // Add Cells to Row
            row.appendChild(nameCell);
            row.appendChild(categoryCell);
            row.appendChild(priceCell);
            row.appendChild(statusCell);
            row.appendChild(actionCell);

            menuTableBody.appendChild(row);
        });

    } catch (error) {
        console.error('Menu loading error:', error);

        menuTableBody.innerHTML = `
            <tr>
                <td colspan="5">Failed to load menu data.</td>
            </tr>
        `;
    }
}


// ========================================
// Add Menu
// ========================================

const menuForm = document.getElementById('menuForm');

if (menuForm) {
    menuForm.addEventListener('submit', async function(event) {
        event.preventDefault();

        try {
            const menuName =
                document.getElementById('menuName').value.trim();

            const category =
                document.getElementById('category').value.trim();

            const priceValue =
                document.getElementById('price').value.trim();

            const price = Number(priceValue);

            // Validate Input
            if (!menuName || !category) {
                alert('Please enter menu name and category.');
                return;
            }

            if (!Number.isInteger(price) || price <= 0) {
                alert('Please enter a valid whole number price.');
                return;
            }

            const newMenu = {
                menuName: menuName,
                category: category,
                price: price
            };

            const response = await fetch('/menus', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newMenu)
            });

            if (!response.ok) {
                throw new Error('Failed to add menu');
            }

            const savedMenu = await response.json();

            console.log('New menu added:', savedMenu);

            alert('Menu added successfully!');

            menuForm.reset();

            await loadMenus();

        } catch (error) {
            console.error('Menu adding error:', error);
            alert('Failed to add menu. Please try again.');
        }
    });
}


// ========================================
// Edit Menu
// ========================================

async function editMenu(id) {
    try {
        const newMenuName = prompt('Enter new menu name:');

        if (newMenuName === null) {
            return;
        }

        if (!newMenuName.trim()) {
            alert('Menu name cannot be empty.');
            return;
        }

        const newCategory = prompt('Enter new category:');

        if (newCategory === null) {
            return;
        }

        if (!newCategory.trim()) {
            alert('Category cannot be empty.');
            return;
        }

        const newPrice = prompt('Enter new price:');

        if (newPrice === null) {
            return;
        }

        const price = Number(newPrice.trim());

        if (!Number.isInteger(price) || price <= 0) {
            alert('Please enter a valid whole number price.');
            return;
        }

        const updatedMenu = {
            menuName: newMenuName.trim(),
            category: newCategory.trim(),
            price: price
        };

        const response = await fetch(`/menus/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedMenu)
        });

        if (!response.ok) {
            throw new Error('Failed to update menu');
        }

        const savedMenu = await response.json();

        console.log('Menu updated:', savedMenu);

        alert('Menu updated successfully!');

        await loadMenus();

    } catch (error) {
        console.error('Menu update error:', error);
        alert('Failed to update menu. Please try again.');
    }
}


// ========================================
// Activate Menu
// ========================================

async function activateMenu(id) {
    try {
        const confirmed = confirm(
            'Do you want to activate this menu?'
        );

        if (!confirmed) {
            return;
        }

        const response = await fetch(`/menus/${id}/activate`, {
            method: 'PATCH'
        });

        if (!response.ok) {
            throw new Error('Failed to activate menu');
        }

        const updatedMenu = await response.json();

        console.log('Menu activated:', updatedMenu);

        alert('Menu activated successfully!');

        await loadMenus();

    } catch (error) {
        console.error('Menu activation error:', error);
        alert('Failed to activate menu. Please try again.');
    }
}


// ========================================
// Deactivate Menu
// ========================================

async function deactivateMenu(id) {
    try {
        const confirmed = confirm(
            'Do you want to deactivate this menu?'
        );

        if (!confirmed) {
            return;
        }

        const response = await fetch(`/menus/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Failed to deactivate menu');
        }

        console.log('Menu deactivated:', id);

        alert('Menu deactivated successfully!');

        await loadMenus();

    } catch (error) {
        console.error('Menu deactivation error:', error);
        alert('Failed to deactivate menu. Please try again.');
    }
}


// ========================================
// Initial Load
// ========================================

loadMenus();
