// Bookings page scripts
let selectedVehicleForBooking = null;

window.addEventListener('DOMContentLoaded', async () => {
    // Check authentication of user
    if (!AuthService.isAuthenticated()) {
        window.location.href = '/index.html';
        return;
    }
    await loadMyBookings();
});

function renderStars(rating) {
    if (!rating || rating < 0 || rating > 5) {
        return '<span class="text-slate-400 text-sm">No rating</span>';
    }
    return `<span class="text-slate-700 font-semibold">Rating: ${rating}/5</span>`;
}
// For calculating total days for quotation
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

// Fetch and show user bookings
async function loadMyBookings() {
    const statusEl = document.getElementById('myBookingStatus');
    const listEl = document.getElementById('myBookingList');

    statusEl.textContent = 'Loading bookings...';
    statusEl.className = 'mb-6 p-3 rounded-lg text-sm text-slate-700 bg-slate-200';
    listEl.innerHTML = '';

    try {
        const bookings = await ApiService.get('/bookings/my');

        if (!bookings || bookings.length === 0) {
            statusEl.textContent = 'No bookings yet. Start by booking a vehicle.';
            statusEl.className = 'mb-6 p-3 rounded-lg text-sm text-yellow-700 bg-yellow-100';
            return;
        }

        statusEl.textContent = `Showing ${bookings.length} booking(s).`;
        statusEl.className = 'mb-6 p-3 rounded-lg text-sm text-green-700 bg-green-100';
        // Booking Card
        listEl.innerHTML = bookings.map(booking => `
            <article class="bg-white rounded-xl shadow-md hover:shadow-xl transition duration-300 overflow-hidden border border-slate-100">
                <div class="bg-slate-100 p-4 border-b border-slate-200">
                    <h4 class="text-lg font-bold text-slate-900">${booking.vehicleName || 'Vehicle'}</h4>
                    <p class="text-sm text-slate-600">No.  ${booking.registrationNumber || 'N/A'}</p>
                </div>
                
                <div class="p-4 space-y-3">
                    <div class="flex items-center justify-between">
                        <span class="text-sm font-semibold text-slate-700">Dates:</span>
                        <span class="text-sm text-slate-900">${booking.startDate} - ${booking.endDate}</span>
                    </div>
                    
                    <div class="flex items-center justify-between">
                        <span class="text-sm font-semibold text-slate-700">Status:</span>
                        <span class="px-3 py-1 rounded-full text-xs font-semibold ${booking.status === 'CONFIRMED' ? 'bg-green-100 text-green-800' :
                booking.status === 'CANCELLED' ? 'bg-red-100 text-red-800' :
                    booking.status === 'ACTIVE' ? 'bg-blue-100 text-blue-800' :
                        'bg-slate-100 text-slate-800'
            }">${booking.status}</span>
                    </div>

                    <div class="flex items-center justify-between">
                        <span class="text-sm font-semibold text-slate-700">Rating:</span>
                        <div>${renderStars(booking.rating)}</div>
                    </div>

                    <div class="text-xs text-slate-500">
                        Booked on: ${new Date(booking.bookingDate).toLocaleDateString()}
                    </div>
                </div>

                <div class="px-4 pb-4 flex gap-2">
                    ${booking.status !== 'CANCELLED' ? `
                        <button class="flex-1 bg-red-500 hover:bg-red-600 text-white px-3 py-2 rounded-lg transition duration-200 text-sm font-medium"
                            onclick="cancelBooking(${booking.bookingId})">
                            Cancel
                        </button>
                    ` : '<p class="flex-1 text-center text-xs text-slate-500 py-2">Booking cancelled</p>'}
                    
                    <button class="flex-1 bg-indigo-500 hover:bg-indigo-600 text-white px-3 py-2 rounded-lg transition duration-200 text-sm font-medium"
                        onclick="editRating(${booking.bookingId}, ${booking.rating || 0})">
                        Rate
                    </button>
                </div>
            </article>
        `).join('');
    } catch (err) {
        statusEl.textContent = 'Failed to load bookings: ' + err.message;
        statusEl.className = 'mb-6 p-3 rounded-lg text-sm text-red-700 bg-red-100';
    }
}
// Cancel booking with bookingId
async function cancelBooking(bookingId) {
    if (!confirm('Are you sure you want to cancel this booking?')) return;

    try {
        await ApiService.put(`/bookings/${bookingId}/cancel`, {});
        await loadMyBookings();
    } catch (err) {
        const statusEl = document.getElementById('myBookingStatus');
        statusEl.textContent = 'Failed to cancel booking: ' + err.message;
        statusEl.className = 'mb-6 p-3 rounded-lg text-sm text-red-700 bg-red-100';
    }
}
// Currently not implemented, TODO
async function editRating(bookingId, currentRating) {
    const rating = prompt(`Enter rating (0-5 stars). Current: ${currentRating || 'None'}:`, currentRating || '');
    if (rating === null) return;

    const ratingNum = parseFloat(rating);
    if (isNaN(ratingNum) || ratingNum < 0 || ratingNum > 5) {
        alert('Please enter a valid rating between 0 and 5');
        return;
    }

    try {
        alert(`Rating updated to ${ratingNum}. (Requires backend endpoint)`);
        // await ApiService.put(``);   TODO
        await loadMyBookings();
    } catch (err) {
        alert('Failed to update rating: ' + err.message);
    }
}

function closeNewBookingModal() {
    const modal = document.getElementById('newBookingModal');
    modal.classList.remove('flex');
    modal.classList.add('hidden');
    selectedVehicleForBooking = null;
}

async function loadVehiclesForBooking() {
    const statusEl = document.getElementById('vehicleSelectStatus');
    const listEl = document.getElementById('vehicleSelectList');

    try {
        const vehicles = await ApiService.get('/vehicles');
        // This is currently client side will try to do this from backend
        const activeVehicles = (vehicles || []).filter((vehicle) => vehicle.isActive);

        if (activeVehicles.length === 0) {
            statusEl.textContent = 'No available vehicles.';
            statusEl.className = 'mb-3 text-sm text-yellow-700';
            return;
        }

        statusEl.textContent = `${activeVehicles.length} vehicle(s) available.`;
        statusEl.className = 'mb-3 text-sm text-green-700';

        listEl.innerHTML = activeVehicles.map((vehicle) => `
            <div class="border border-slate-200 rounded-lg p-3 cursor-pointer hover:bg-indigo-50 transition duration-200 ${selectedVehicleForBooking?.vehicleId === vehicle.vehicleId ? 'bg-indigo-100 border-indigo-400' : 'bg-white'}"
                onclick="selectVehicleForBooking(${JSON.stringify(vehicleToAttribute(vehicle)).replace(/"/g, '&quot;')})">
                <div class="font-semibold text-slate-900">${vehicle.vehicleName || 'Vehicle'}</div>
                <div class="text-xs text-slate-600 mt-1">
                    <span>${vehicle.vehicleType || 'N/A'}</span> | 
                    <span>${vehicle.registrationNumber || 'N/A'}</span>
                </div>
                <div class="text-sm font-semibold text-indigo-600 mt-2">₹${vehicle.pricePerDay || 0}/day</div>
            </div>
        `).join('');
    } catch (err) {
        statusEl.textContent = 'Failed to load vehicles: ' + err.message;
        statusEl.className = 'mb-3 text-sm text-red-600';
    }
}

async function selectVehicleForBooking(vehicleJson) {
    selectedVehicleForBooking = JSON.parse(vehicleJson);
    document.getElementById('newBookingRate').textContent = `₹${selectedVehicleForBooking.pricePerDay || 0}`;
    updateNewBookingSummary();
    await loadVehiclesForBooking(); // Refresh UI to show selection
}

function updateNewBookingSummary() {
    if (!selectedVehicleForBooking) {
        return;
    }

    const startDate = document.getElementById('newBookingStartDate').value;
    const endDate = document.getElementById('newBookingEndDate').value;
    const daysEl = document.getElementById('newBookingDays');
    const amountEl = document.getElementById('newBookingAmount');

    if (!startDate || !endDate || new Date(endDate) < new Date(startDate)) {
        daysEl.textContent = '0';
        amountEl.textContent = '₹0';
        return;
    }

    const totalDays = daysBetweenInclusive(startDate, endDate);
    daysEl.textContent = String(totalDays);
    amountEl.textContent = `₹${String(totalDays * (selectedVehicleForBooking.pricePerDay || 0))}`;
}

document.addEventListener('input', (event) => {
    if (event.target && (event.target.id === 'newBookingStartDate' || event.target.id === 'newBookingEndDate')) {
        updateNewBookingSummary();
    }
});


function handleLogout() {
    AuthService.logout();
    window.location.href = '/index.html';
}
