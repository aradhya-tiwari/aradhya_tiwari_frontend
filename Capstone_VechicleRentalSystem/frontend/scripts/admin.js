// Admin page scripts
let editingVehicleId = null;

window.addEventListener('DOMContentLoaded', async () => {
    if (!AuthService.isAuthenticated()) {
        window.location.href = '/index.html';
        return;
    }

    const role = AuthService.getRole();
    if (role !== 'ADMIN') {
        window.location.href = '/app.html';
        return;
    }

    const user = AuthService.getCurrentUser();
    if (user) {
        document.getElementById('adminName').textContent = user.fullName;
    }

    document.getElementById('adminRole').textContent = role;
    await loadVehicles();
});

async function handleVehicleSubmit(e) {
    e.preventDefault();

    const formMessage = document.getElementById('formMessage');
    const payload = {
        vehicleName: document.getElementById('vehicleName').value.trim(),
        vehicleType: document.getElementById('vehicleType').value,
        registrationNumber: document.getElementById('registrationNumber').value.trim(),
        pricePerDay: Number(document.getElementById('pricePerDay').value),
        imgUrl: document.getElementById('imgUrl').value.trim(),
        basicDetails: document.getElementById('basicDetails').value.trim(),
        availabilityStatus: document.getElementById('availabilityStatus').checked,
        isActive: document.getElementById('isActive').checked
    };

    try {
        if (editingVehicleId) {
            await ApiService.put(`/vehicles/${editingVehicleId}`, payload);
            formMessage.textContent = 'Vehicle updated successfully.';
        } else {
            await ApiService.post('/vehicles', payload);
            formMessage.textContent = 'Vehicle added successfully.';
        }

        formMessage.className = 'mt-3 text-sm text-green-600';
        resetForm();
        await loadVehicles();
    } catch (err) {
        formMessage.textContent = 'Failed: ' + err.message;
        formMessage.className = 'mt-3 text-sm text-red-600';
    }
}

async function loadVehicles() {
    const listMessage = document.getElementById('listMessage');
    const vehicleList = document.getElementById('vehicleList');

    listMessage.textContent = 'Loading vehicles...';
    listMessage.className = 'mb-4 text-sm text-gray-600';
    vehicleList.innerHTML = '';

    try {
        const vehicles = await ApiService.get('/vehicles');

        if (!vehicles || vehicles.length === 0) {
            listMessage.textContent = 'No vehicles found.';
            listMessage.className = 'mb-4 text-sm text-yellow-700';
            return;
        }

        listMessage.textContent = `Showing ${vehicles.length} vehicle(s).`;
        listMessage.className = 'mb-4 text-sm text-green-700';

        vehicleList.innerHTML = vehicles.map(vehicle => {
            const imageUrl = vehicle.imgUrl || vehicle.imgurl || '';
            return `
                <article class="bg-white border rounded-lg p-4 shadow-sm">
                    ${imageUrl ? `<img src="${imageUrl}" alt="${vehicle.vehicleName}" class="w-full h-40 object-cover rounded mb-3">` : ''}
                    <h4 class="text-lg font-bold">${vehicle.vehicleName}</h4>
                    <p class="text-sm text-gray-700 mt-1"><span class="font-semibold">Type:</span> ${vehicle.vehicleType}</p>
                    <p class="text-sm text-gray-700"><span class="font-semibold">Reg No:</span> ${vehicle.registrationNumber}</p>
                    <p class="text-sm text-gray-700"><span class="font-semibold">Price / Day:</span> INR ${vehicle.pricePerDay ?? 0}</p>
                    <p class="text-sm text-gray-700"><span class="font-semibold">Available:</span> ${vehicle.availabilityStatus ? 'Yes' : 'No'}</p>
                    <p class="text-sm text-gray-700"><span class="font-semibold">Active:</span> ${vehicle.isActive ? 'Yes' : 'No'}</p>
                    <p class="text-sm text-gray-600 mt-2">${vehicle.basicDetails || 'No details provided.'}</p>
                    <div class="mt-3 flex gap-2">
                        <button
                            data-action="edit"
                            data-vehicle='${JSON.stringify(vehicle).replace(/'/g, "&#39;")}'
                            class="bg-indigo-600 text-white px-3 py-2 rounded hover:bg-indigo-700">
                            Edit
                        </button>
                        <button
                            data-action="delete"
                            data-vehicle-id="${vehicle.vehicleId}"
                            class="bg-red-600 text-white px-3 py-2 rounded hover:bg-red-700">
                            Delete
                        </button>
                    </div>
                </article>
            `;
        }).join('');

        document.querySelectorAll('[data-action="edit"]').forEach((btn) => {
            btn.addEventListener('click', () => {
                const vehicle = JSON.parse(btn.getAttribute('data-vehicle'));
                startEdit(vehicle);
            });
        });

        document.querySelectorAll('[data-action="delete"]').forEach((btn) => {
            btn.addEventListener('click', async () => {
                const vehicleId = Number(btn.getAttribute('data-vehicle-id'));
                await deleteVehicle(vehicleId);
            });
        });
    } catch (err) {
        listMessage.textContent = 'Failed to load vehicles: ' + err.message;
        listMessage.className = 'mb-4 text-sm text-red-600';
    }
}

async function deleteVehicle(vehicleId) {
    const shouldDelete = window.confirm('Delete this vehicle? This action cannot be undone.');
    if (!shouldDelete) {
        return;
    }

    const listMessage = document.getElementById('listMessage');
    const formMessage = document.getElementById('formMessage');

    try {
        await ApiService.delete(`/vehicles/${vehicleId}`);

        if (editingVehicleId === vehicleId) {
            resetForm();
        }

        formMessage.textContent = 'Vehicle deleted successfully.';
        formMessage.className = 'mt-3 text-sm text-green-600';
        await loadVehicles();
    } catch (err) {
        listMessage.textContent = 'Failed to delete vehicle: ' + err.message;
        listMessage.className = 'mb-4 text-sm text-red-600';
    }
}

function startEdit(vehicle) {
    editingVehicleId = vehicle.vehicleId;
    document.getElementById('vehicleName').value = vehicle.vehicleName || '';
    document.getElementById('vehicleType').value = vehicle.vehicleType || '';
    document.getElementById('registrationNumber').value = vehicle.registrationNumber || '';
    document.getElementById('pricePerDay').value = vehicle.pricePerDay ?? '';
    document.getElementById('imgUrl').value = vehicle.imgUrl || vehicle.imgurl || '';
    document.getElementById('basicDetails').value = vehicle.basicDetails || '';
    document.getElementById('availabilityStatus').checked = !!vehicle.availabilityStatus;
    document.getElementById('isActive').checked = !!vehicle.isActive;

    document.getElementById('formTitle').textContent = 'Update Vehicle';
    document.getElementById('submitBtn').textContent = 'Update Vehicle';
    document.getElementById('cancelEditBtn').classList.remove('hidden');
    document.getElementById('formMessage').textContent = '';
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function cancelEdit() {
    resetForm();
}

function resetForm() {
    editingVehicleId = null;
    document.getElementById('vehicleForm').reset();
    document.getElementById('availabilityStatus').checked = true;
    document.getElementById('isActive').checked = true;
    document.getElementById('formTitle').textContent = 'Add Vehicle';
    document.getElementById('submitBtn').textContent = 'Add Vehicle';
    document.getElementById('cancelEditBtn').classList.add('hidden');
}

function handleLogout() {
    AuthService.logout();
    window.location.href = '/index.html';
}
