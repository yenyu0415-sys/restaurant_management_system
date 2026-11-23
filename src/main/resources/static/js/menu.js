
async function loadMenus() {
    try {
        const response = await fetch('/menus');

        if (!response.ok) {
            throw new Error('Failed to load menu data');
        }

        const menus = await response.json();

        console.log('Menu data:', menus);

        const menuTableBody = document.getElementById('menuTableBody');

        if (!menuTableBody) {
            throw new Error('Menu table body not found');
        }

        menuTableBody.innerHTML = '';

        menus.forEach(menu => {
            const row = document.createElement('tr');

            row.innerHTML = `
                <td>${menu.menuName}</td>
                <td>${menu.category}</td>
                <td>${menu.price}</td>
                <td>
                    <span class="status-badge ${menu.active ? 'active' : 'inactive'}">
                        ${menu.active ? 'Active' : 'Inactive'}
                    </span>
                </td>
                <td>
                    <button
                        type="button"
                        class="edit-btn"
                        onclick="editMenu(${menu.id})">
                        Edit
                    </button>

                    ${
                menu.active
                    ? `<button
                               type="button"
                               class="deactivate-btn"
                               onclick="deactivateMenu(${menu.id})">
                               Deactivate
                           </button>`
                    : `<button
                               type="button"
                               class="activate-btn"
                               onclick="activateMenu(${menu.id})">
                               Activate
                           </button>`
            }
                </td>
            `;

            menuTableBody.appendChild(row);
        });

    } catch (error) {
        console.error('Menu loading error:', error);
    }
}


// =========================
// Add Menu
// =========================

const menuForm = document.getElementById('menuForm');

menuForm.addEventListener('submit', async function(event) {
    event.preventDefault();

    try {
        const newMenu = {
            menuName: document.getElementById('menuName').value.trim(),
            category: document.getElementById('category').value.trim(),
            price: Number(document.getElementById('price').value)
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
        alert('Failed to add menu.');
    }
});


// =========================
// Edit Menu
// =========================

async function editMenu(id) {
    try {
        const newMenuName = prompt('Enter new menu name:');

        if (newMenuName === null || newMenuName.trim() === '') {
            return;
        }

        const newCategory = prompt('Enter new category:');

        if (newCategory === null || newCategory.trim() === '') {
            return;
        }

        const newPrice = prompt('Enter new price:');

        if (newPrice === null || newPrice.trim() === '') {
            return;
        }

        const updatedMenu = {
            menuName: newMenuName.trim(),
            category: newCategory.trim(),
            price: Number(newPrice)
        };

        if (!Number.isFinite(updatedMenu.price) || updatedMenu.price <= 0) {
            alert('Please enter a valid price.');
            return;
        }

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
        alert('Failed to update menu.');
    }
}


// =========================
// Activate Menu
// =========================

async function activateMenu(id) {
    try {
        const confirmed = confirm('Do you want to activate this menu?');

        if (!confirmed) {
            return;
        }

        const response = await fetch(`/menus/${id}/activate`, {
            method: 'PATCH'
        });

        if (!response.ok) {
            throw new Error('Failed to activate menu');
        }

        console.log('Menu activated:', await response.json());

        alert('Menu activated successfully!');

        await loadMenus();

    } catch (error) {
        console.error('Menu activation error:', error);
        alert('Failed to activate menu.');
    }
}


// =========================
// Deactivate Menu
// =========================

async function deactivateMenu(id) {
    try {
        const confirmed = confirm('Do you want to deactivate this menu?');

        if (!confirmed) {
            return;
        }

        const response = await fetch(`/menus/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Failed to deactivate menu');
        }

        alert('Menu deactivated successfully!');

        await loadMenus();

    } catch (error) {
        console.error('Menu deactivation error:', error);
        alert('Failed to deactivate menu.');
    }
}


// =========================
// Initial Load
// =========================

loadMenus();
