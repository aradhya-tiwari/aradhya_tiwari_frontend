// Vehicles page scripts
let selectedVehicle = null;

window.addEventListener('DOMContentLoaded', async () => {
    if (!AuthService.isAuthenticated()) {
        window.location.href = '/index.html';
        return;
    }

    await loadVehicles();
});

function daysBetweenInclusive(startDate, endDate) {
    const start = new Date(`${startDate}T00:00:00`);
    const end = new Date(`${endDate}T00:00:00`);
    const diffMs = end - start;
    return Math.floor(diffMs / (1000 * 60 * 60 * 24)) + 1;
}

function vehicleToAttribute(vehicle) {
    return JSON.stringify({
        vehicleId: vehicle.vehicleId,
        vehicleName: vehicle.vehicleName || 'Vehicle',
        vehicleType: vehicle.vehicleType || 'N/A',
        registrationNumber: vehicle.registrationNumber || 'N/A',
        pricePerDay: Number(vehicle.pricePerDay || 0)
    }).replace(/'/g, '&#39;');
}

async function loadVehicles() {
    const vehicleStatus = document.getElementById('vehicleStatus');
    const vehicleList = document.getElementById('vehicleList');

    vehicleStatus.textContent = 'Loading vehicles...';
    vehicleStatus.className = 'mb-4 text-sm text-gray-600';
    vehicleList.innerHTML = '';

    try {
        const vehicles = await ApiService.get('/vehicles');
        const activeVehicles = (vehicles || []).filter((vehicle) => vehicle.isActive);

        if (activeVehicles.length === 0) {
            vehicleStatus.textContent = 'No available vehicles found.';
            vehicleStatus.className = 'mb-4 text-sm text-yellow-700';
            return;
        }

        vehicleStatus.textContent = `Showing ${activeVehicles.length} vehicle(s).`;
        vehicleStatus.className = 'mb-4 text-sm text-green-700';

        vehicleList.innerHTML = activeVehicles.map((vehicle) => {
            const imageUrl = vehicle.imgUrl || vehicle.imgurl || '';
            return `
                <article class="border shadow-md border-gray-300 rounded-lg p-4 bg-white">
                    <h4 class="text-lg font-bold text-gray-900">${(vehicle.vehicleName || 'N/A').toUpperCase()}</h4>
                    ${imageUrl ? `<img src="${imageUrl}" alt="${vehicle.vehicleName || 'Vehicle'}" class="w-full h-60 rounded-lg shadow mt-3"/>` : ''}
                    <p class="text-sm text-gray-700 mt-3"><span class="font-semibold">Type:</span> ${vehicle.vehicleType || 'N/A'}</p>
                    <p class="text-sm text-gray-700"><span class="font-semibold">Reg No:</span> ${vehicle.registrationNumber || 'N/A'}</p>
                    <p class="text-sm text-gray-700"><span class="font-semibold">Price / Day:</span> INR ${vehicle.pricePerDay ?? 0}</p>
                    <p class="text-sm text-gray-600 mt-2">${vehicle.basicDetails || 'No details provided.'}</p>
                    <button
                        data-action="checkout-vehicle"
                        data-vehicle='${vehicleToAttribute(vehicle)}'
                        class="bg-blue-600 hover:bg-blue-700 mt-6 text-white px-4 py-2 rounded-sm">
                        Checkout
                    </button>
                </article>
            `;
        }).join('');

        document.querySelectorAll('[data-action="checkout-vehicle"]').forEach((button) => {
            button.addEventListener('click', async () => {
                const vehicle = JSON.parse(button.getAttribute('data-vehicle'));
                await openCheckoutModal(vehicle);
            });
        });
    } catch (err) {
        vehicleStatus.textContent = 'Failed to load vehicles: ' + err.message;
        vehicleStatus.className = 'mb-4 text-sm text-red-600';
    }
}

async function openCheckoutModal(vehicle) {
    selectedVehicle = vehicle;
    const modal = document.getElementById('checkoutModal');
    const today = new Date().toISOString().split('T')[0];

    document.getElementById('checkoutModalTitle').textContent = `Checkout - ${vehicle.vehicleName}`;
    document.getElementById('checkoutVehicleSummary').textContent = `${vehicle.vehicleType} | Reg No: ${vehicle.registrationNumber} | Price / Day: INR ${vehicle.pricePerDay}`;
    document.getElementById('checkoutStartDate').value = '';
    document.getElementById('checkoutEndDate').value = '';
    document.getElementById('checkoutStartDate').min = today;
    document.getElementById('checkoutEndDate').min = today;
    document.getElementById('checkoutRate').textContent = String(vehicle.pricePerDay || 0);
    document.getElementById('checkoutDays').textContent = '0';
    document.getElementById('checkoutAmount').textContent = '0';
    document.getElementById('checkoutMessage').textContent = '';
    document.getElementById('checkoutBookingCount').textContent = '0';
    document.getElementById('checkoutBookingStatus').textContent = 'Loading bookings...';
    document.getElementById('checkoutBookingStatus').className = 'mb-3 text-sm text-gray-600';
    document.getElementById('checkoutBookingList').innerHTML = '';

    modal.classList.remove('hidden');
    modal.classList.add('flex');

    try {
        const bookings = await ApiService.get(`/bookings/vehicle/${vehicle.vehicleId}`);
        document.getElementById('checkoutBookingCount').textContent = String(bookings.length);

        if (!bookings || bookings.length === 0) {
            document.getElementById('checkoutBookingStatus').textContent = 'No bookings found for this vehicle.';
            document.getElementById('checkoutBookingStatus').className = 'mb-3 text-sm text-yellow-700';
            return;
        }

        document.getElementById('checkoutBookingStatus').textContent = `Showing ${bookings.length} booking(s) for this vehicle.`;
        document.getElementById('checkoutBookingStatus').className = 'mb-3 text-sm text-green-700';
        document.getElementById('checkoutBookingList').innerHTML = bookings.map((booking) => `
            <article class="border border-gray-300 rounded-lg p-3 bg-white">
                <p class="text-sm text-gray-800"><span class="font-semibold">Dates:</span> ${booking.startDate} to ${booking.endDate}</p>
                <p class="text-sm text-gray-800"><span class="font-semibold">Status:</span> ${booking.status}</p>
            </article>
        `).join('');
    } catch (err) {
        document.getElementById('checkoutBookingStatus').textContent = 'Failed to load bookings: ' + err.message;
        document.getElementById('checkoutBookingStatus').className = 'mb-3 text-sm text-red-600';
    }
}

function closeCheckoutModal() {
    const modal = document.getElementById('checkoutModal');
    modal.classList.remove('flex');
    modal.classList.add('hidden');
    selectedVehicle = null;
}

function updateCheckoutSummary() {
    if (!selectedVehicle) {
        return;
    }

    const startDate = document.getElementById('checkoutStartDate').value;
    const endDate = document.getElementById('checkoutEndDate').value;
    const daysEl = document.getElementById('checkoutDays');
    const amountEl = document.getElementById('checkoutAmount');

    if (!startDate || !endDate || new Date(endDate) < new Date(startDate)) {
        daysEl.textContent = '0';
        amountEl.textContent = '0';
        return;
    }

    const totalDays = daysBetweenInclusive(startDate, endDate);
    daysEl.textContent = String(totalDays);
    amountEl.textContent = String(totalDays * (selectedVehicle.pricePerDay || 0));
}

document.addEventListener('input', (event) => {
    if (event.target && (event.target.id === 'checkoutStartDate' || event.target.id === 'checkoutEndDate')) {
        updateCheckoutSummary();
    }
});

async function createBooking() {
    const messageEl = document.getElementById('checkoutMessage');
    const startDate = document.getElementById('checkoutStartDate').value;
    const endDate = document.getElementById('checkoutEndDate').value;

    if (!selectedVehicle) {
        messageEl.textContent = 'Please select a vehicle first.';
        messageEl.className = 'mt-3 text-sm text-red-600';
        return;
    }

    if (!startDate || !endDate) {
        messageEl.textContent = 'Please select start and end dates.';
        messageEl.className = 'mt-3 text-sm text-red-600';
        return;
    }

    if (new Date(endDate) < new Date(startDate)) {
        messageEl.textContent = 'End date cannot be before start date.';
        messageEl.className = 'mt-3 text-sm text-red-600';
        return;
    }

    const totalDays = daysBetweenInclusive(startDate, endDate);
    const finalAmount = totalDays * (selectedVehicle.pricePerDay || 0);

    try {
        await ApiService.post('/bookings', {
            vehicleId: selectedVehicle.vehicleId,
            startDate,
            endDate
        });

        messageEl.textContent = `Booking created successfully. Final amount: INR ${finalAmount}`;
        messageEl.className = 'mt-3 text-sm text-green-600';
        await loadVehicles();
    } catch (err) {
        messageEl.textContent = 'Failed to create booking: ' + err.message;
        messageEl.className = 'mt-3 text-sm text-red-600';
    }
}

function bookAnotherVehicle() {
    closeCheckoutModal();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function handleLogout() {
    AuthService.logout();
    window.location.href = '/index.html';
}
