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
        availability_status: true,
        isActive: document.getElementById('isActive').checked
    };

    try {
        if (editingVehicleId) {
            await ApiService.put(`/vehicles/${editingVehicleId}`, payload);
            formMessage.textContent = 'Vehicle updated successfully!';
        } else {
            await ApiService.post('/vehicles', payload);
            formMessage.textContent = 'Vehicle added successfully!';
        }

        formMessage.className = 'mt-4 text-sm text-green-600 font-semibold';
        resetForm();
        await loadVehicles();
    } catch (err) {
        formMessage.textContent = 'Failed: ' + err.message;
        formMessage.className = 'mt-4 text-sm text-red-600 font-medium';
    }
}
//  fetch all vehicles
async function loadVehicles() {
    const listMessage = document.getElementById('listMessage');
    const vehicleList = document.getElementById('vehicleList');

    listMessage.textContent = 'Loading vehicles...';
    listMessage.className = 'mb-6 p-3 rounded-lg text-sm text-slate-700 bg-slate-200';
    vehicleList.innerHTML = '';

    try {
        const vehicles = await ApiService.get('/vehicles');

        if (!vehicles || vehicles.length === 0) {
            listMessage.textContent = 'No vehicles found.';
            listMessage.className = 'mb-6 p-3 rounded-lg text-sm text-yellow-700 bg-yellow-100';
            return;
        }

        listMessage.textContent = `Showing ${vehicles.length} vehicle(s).`;
        listMessage.className = 'mb-6 p-3 rounded-lg text-sm text-green-700 bg-green-100';

        vehicleList.innerHTML = vehicles.map(vehicle => {
            const imageUrl = vehicle.imgUrl || vehicle.imgurl || '';
            return `
                <article class="bg-white rounded-xl shadow-md hover:shadow-xl transition duration-300 overflow-hidden border border-slate-100">
                    ${imageUrl ? `<img src="${imageUrl}" alt="${vehicle.vehicleName}" class="w-full h-48 object-cover"/>` : '<div class="w-full h-48 bg-slate-300 flex items-center justify-center text-slate-700 text-3xl">Vehicle</div>'}
                    
                    <div class="p-5">
                        <h4 class="text-lg font-bold text-slate-900">${vehicle.vehicleName}</h4>
                        
                        <div class="mt-3 space-y-1 text-sm text-slate-700">
                            <p><span class="font-semibold">Type:</span> ${vehicle.vehicleType}</p>
                            <p><span class="font-semibold">Reg No:</span> ${vehicle.registrationNumber}</p>
                            <p><span class="font-semibold text-purple-600">₹${vehicle.pricePerDay ?? 0}/day</span></p>
                            <p><span class="font-semibold">Active:</span> <span class="px-2 py-0.5 rounded text-xs font-semibold ${vehicle.isActive ? 'bg-blue-100 text-blue-800' : 'bg-slate-100 text-slate-800'}">${vehicle.isActive ? 'Yes' : 'No'}</span></p>
                        </div>
                        
                        <p class="text-sm text-slate-600 mt-3 line-clamp-2">${vehicle.basicDetails || 'No details provided.'}</p>
                        
                        <div class="mt-4 flex gap-2">
                            <button
                                data-action="edit"
                                data-vehicle='${JSON.stringify(vehicle).replace(/'/g, "&#39;")}'
                                class="flex-1 bg-indigo-600 hover:bg-indigo-700 text-white px-3 py-2 rounded-lg transition duration-200 text-sm font-medium">
                                Edit
                            </button>
                            <button
                                data-action="delete"
                                data-vehicle-id="${vehicle.vehicleId}"
                                class="flex-1 bg-red-600 hover:bg-red-700 text-white px-3 py-2 rounded-lg transition duration-200 text-sm font-medium">
                                Delete
                            </button>
                        </div>
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
        listMessage.className = 'mb-6 p-3 rounded-lg text-sm text-red-700 bg-red-100';
    }
}
// DELETE /api/vehilces/:id 
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

        formMessage.textContent = 'Vehicle deleted successfully!';
        formMessage.className = 'mt-4 text-sm text-green-600 font-semibold';
        await loadVehicles();
    } catch (err) {
        listMessage.textContent = 'Failed to delete vehicle: ' + err.message;
        listMessage.className = 'mb-6 p-3 rounded-lg text-sm text-red-700 bg-red-100';
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
    document.getElementById('isActive').checked = true;
    document.getElementById('formTitle').textContent = 'Add Vehicle';
    document.getElementById('submitBtn').textContent = 'Add Vehicle';
    document.getElementById('cancelEditBtn').classList.add('hidden');
}

function handleLogout() {
    AuthService.logout();
    window.location.href = '/index.html';
}
