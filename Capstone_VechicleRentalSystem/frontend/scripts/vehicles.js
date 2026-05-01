// Vehicles page scripts
let selectedVehicle = null;
let allActiveVehicles = [];

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

// Fetch all the vehicles from /api/vehicles
async function loadVehicles() {
    const vehicleStatus = document.getElementById('vehicleStatus');
    const vehicleList = document.getElementById('vehicleList');

    vehicleStatus.textContent = 'Loading vehicles...';
    vehicleStatus.className = 'mb-6 p-3 rounded-lg text-sm text-slate-700 bg-slate-200';
    vehicleList.innerHTML = '';

    try {
        const vehicles = await ApiService.get('/vehicles');
        allActiveVehicles = (vehicles || []).filter((vehicle) => vehicle.isActive);

        if (allActiveVehicles.length === 0) {
            vehicleStatus.textContent = 'No available vehicles found.';
            vehicleStatus.className = 'mb-6 p-3 rounded-lg text-sm text-yellow-700 bg-yellow-100';
            return;
        }

        populateVehicleTypeFilter(allActiveVehicles);
        wireVehicleFilters();
        renderFilteredVehicles();

    } catch (err) {
        vehicleStatus.textContent = 'Failed to load vehicles: ' + err.message;
        vehicleStatus.className = 'mb-6 p-3 rounded-lg text-sm text-red-700 bg-red-100';
    }
}

function wireVehicleFilters() {
    document.getElementById('vehicleSearch')?.addEventListener('input', renderFilteredVehicles);
    document.getElementById('vehicleTypeFilter')?.addEventListener('change', renderFilteredVehicles);
    document.getElementById('vehicleMaxPrice')?.addEventListener('input', renderFilteredVehicles);
}

function populateVehicleTypeFilter(vehicles) {
    const typeFilter = document.getElementById('vehicleTypeFilter');
    const types = [...new Set(vehicles.map(v => v.vehicleType).filter(Boolean))];
    const optionsHtml = types.map(type => `<option value="${type}">${type}</option>`).join('');
    typeFilter.innerHTML = '<option value="all">All Types</option>' + optionsHtml;
}

function getFilteredVehicles() {
    const searchValue = document.getElementById('vehicleSearch')?.value.toLowerCase() || '';
    const typeValue = document.getElementById('vehicleTypeFilter')?.value || 'all';
    const maxPriceValue = Number(document.getElementById('vehicleMaxPrice')?.value || 0);

    return allActiveVehicles.filter(v => {
        const matchesSearch = !searchValue || v.vehicleName.toLowerCase().includes(searchValue) || v.registrationNumber.toLowerCase().includes(searchValue);
        const matchesType = typeValue === 'all' || v.vehicleType === typeValue;
        const matchesPrice = maxPriceValue === 0 || v.pricePerDay >= maxPriceValue;
        return matchesSearch && matchesType && matchesPrice;
    });
}

function renderFilteredVehicles() {
    const vehicleStatus = document.getElementById('vehicleStatus');
    const vehicleList = document.getElementById('vehicleList');
    const filtered = getFilteredVehicles();

    if (filtered.length === 0) {
        vehicleStatus.textContent = 'No vehicles match your filters.';
        vehicleStatus.className = 'mb-6 p-3 rounded-lg text-sm text-yellow-700 bg-yellow-100';
        vehicleList.innerHTML = '';
        return;
    }

    vehicleStatus.textContent = `Showing ${filtered.length} vehicle(s).`;
    vehicleStatus.className = 'mb-6 p-3 rounded-lg text-sm text-green-700 bg-green-100';

    // Vehicle Component
    vehicleList.innerHTML = filtered.map((vehicle) => {
        // Fallback image when image is not provided
        const imageUrl = vehicle.imgUrl || vehicle.imgurl || 'https://png.pngtree.com/png-vector/20211231/ourlarge/pngtree-simple-cartoon-car-template-png-image_4074154.png';
        return `
                <article class="bg-white rounded-xl shadow-md hover:shadow-xl transition duration-300 overflow-hidden border border-slate-100">
                    <img src="${imageUrl}" alt="${vehicle.vehicleName || 'Vehicle'}" class="w-full h-56 object-cover"/>
                    
                    <div class="p-5">
                        <h4 class="text-xl font-bold text-slate-900">${(vehicle.vehicleName || 'Vehicle').toUpperCase()}</h4>
                        
                        <div class="mt-3 space-y-2 text-sm text-slate-700">
                            <p><span class="font-semibold">Type:</span> ${vehicle.vehicleType || 'N/A'}</p>
                            <p><span class="font-semibold">Reg No:</span> ${vehicle.registrationNumber || 'N/A'}</p>
                            <p><span class="text-lg font-bold text-indigo-600">Rs. ${vehicle.pricePerDay ?? 0}/day</span></p>
                        </div>

                        <p class="text-sm text-slate-600 mt-3 line-clamp-2">${vehicle.basicDetails || 'No details provided.'}</p>
                        
                        <button
                            data-action="checkout-vehicle"
                            data-vehicle='${vehicleToAttribute(vehicle)}'
                            class="w-full mt-5 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-3 rounded-lg transition duration-200 font-semibold shadow-md hover:shadow-lg">
                            Book Now
                        </button>
                    </div>
                </article>
            `;
    }).join('');

    document.querySelectorAll('[data-action="checkout-vehicle"]').forEach((button) => {
        button.addEventListener('click', async () => {
            const vehicle = JSON.parse(button.getAttribute('data-vehicle'));
            await openCheckoutModal(vehicle);
        });
    });
}

function clearVehicleFilters() {
    document.getElementById('vehicleSearch').value = '';
    document.getElementById('vehicleTypeFilter').value = 'all';
    document.getElementById('vehicleMaxPrice').value = '';
    renderFilteredVehicles();
}
//  Remove hidden from checkoutModal, fill in vehicle attributes with bookings fetched from /api/bookings/vehicle/:id
async function openCheckoutModal(vehicle) {
    selectedVehicle = vehicle;
    const modal = document.getElementById('checkoutModal');
    const today = new Date().toISOString().split('T')[0];

    document.getElementById('checkoutModalTitle').textContent = `Checkout - ${vehicle.vehicleName}`;
    document.getElementById('checkoutVehicleSummary').textContent = `${vehicle.vehicleType} | Reg: ${vehicle.registrationNumber} | Rs. ${vehicle.pricePerDay}/day`;
    document.getElementById('checkoutStartDate').value = '';
    document.getElementById('checkoutEndDate').value = '';
    document.getElementById('checkoutStartDate').min = today;
    document.getElementById('checkoutEndDate').min = today;
    document.getElementById('checkoutRate').textContent = `Rs. ${String(vehicle.pricePerDay || 0)}`;
    document.getElementById('checkoutDays').textContent = '0';
    document.getElementById('checkoutAmount').textContent = 'Rs. 0';
    document.getElementById('checkoutMessage').textContent = '';
    document.getElementById('checkoutBookingCount').textContent = '0';
    document.getElementById('checkoutBookingStatus').textContent = 'Loading bookings...';
    document.getElementById('checkoutBookingStatus').className = 'mb-3 text-sm text-slate-600';
    document.getElementById('checkoutBookingList').innerHTML = '';

    modal.classList.remove('hidden');
    modal.classList.add('flex');

    try {
        const bookings = await ApiService.get(`/bookings/vehicle/${vehicle.vehicleId}`);
        document.getElementById('checkoutBookingCount').textContent = String(bookings.length);

        if (!bookings || bookings.length === 0) {
            document.getElementById('checkoutBookingStatus').textContent = 'No bookings found for this vehicle.';
            document.getElementById('checkoutBookingStatus').className = 'mb-3 text-sm text-green-700';
            return;
        }

        document.getElementById('checkoutBookingStatus').textContent = `${bookings.length} existing booking(s):`;
        document.getElementById('checkoutBookingStatus').className = 'mb-3 text-sm text-slate-700 font-semibold';
        document.getElementById('checkoutBookingList').innerHTML = bookings.map((booking) => `
            <article class="border border-slate-200 rounded-lg p-3 bg-slate-50">
                <p class="text-sm text-slate-800"><span class="font-semibold">Dates:</span> ${booking.startDate} - ${booking.endDate}</p>
                <p class="text-sm text-slate-800"><span class="font-semibold">Status:</span> 
                    <span class="px-2 py-0.5 rounded text-xs font-semibold ${booking.status === 'CONFIRMED' ? 'bg-green-100 text-green-800' :
                booking.status === 'ACTIVE' ? 'bg-blue-100 text-blue-800' :
                    'bg-slate-100 text-slate-800'
            }">${booking.status}</span>
                </p>
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

// Showing total amount. 
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
        amountEl.textContent = 'Rs. 0';
        return;
    }

    const totalDays = daysBetweenInclusive(startDate, endDate);
    daysEl.textContent = String(totalDays);
    amountEl.textContent = `Rs. ${String(totalDays * (selectedVehicle.pricePerDay || 0))}`;
}

document.addEventListener('input', (event) => {
    if (event.target && (event.target.id === 'checkoutStartDate' || event.target.id === 'checkoutEndDate')) {
        updateCheckoutSummary();
    }
});

// Check final booking amount, create bookings in the backend
async function createBooking() {
    const messageEl = document.getElementById('checkoutMessage');
    const startDate = document.getElementById('checkoutStartDate').value;
    const endDate = document.getElementById('checkoutEndDate').value;

    if (!selectedVehicle) {
        messageEl.textContent = 'Please select a vehicle first.';
        messageEl.className = 'mt-4 text-sm text-red-600 font-medium';
        return;
    }

    if (!startDate || !endDate) {
        messageEl.textContent = 'Please select start and end dates.';
        messageEl.className = 'mt-4 text-sm text-red-600 font-medium';
        return;
    }

    if (new Date(endDate) < new Date(startDate)) {
        messageEl.textContent = 'End date cannot be before start date.';
        messageEl.className = 'mt-4 text-sm text-red-600 font-medium';
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

        messageEl.textContent = `Booking successful! Total: Rs. ${finalAmount}`;
        messageEl.className = 'mt-4 text-sm text-green-600 font-semibold';

        setTimeout(() => {
            closeCheckoutModal();
            loadVehicles();
        }, 2000);
    } catch (err) {
        messageEl.textContent = 'Booking failed: ' + err.message;
        messageEl.className = 'mt-4 text-sm text-red-600 font-medium';
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
