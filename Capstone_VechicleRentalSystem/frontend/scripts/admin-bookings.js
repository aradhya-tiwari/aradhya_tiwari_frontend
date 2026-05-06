// Admin bookings page scripts
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

    await loadAllBookings();
});

async function loadAllBookings() {
    const statusEl = document.getElementById('adminBookingStatus');
    const listEl = document.getElementById('adminBookingList');

    statusEl.textContent = 'Loading bookings...';
    statusEl.className = 'mb-4 text-sm text-gray-600';
    listEl.innerHTML = '';

    try {
        const bookings = await ApiService.get('/bookings/all');

        if (!bookings || bookings.length === 0) {
            statusEl.textContent = 'No bookings found.';
            statusEl.className = 'mb-4 text-sm text-yellow-700';
            return;
        }

        statusEl.textContent = `Showing ${bookings.length} booking(s).`;
        statusEl.className = 'mb-4 text-sm text-green-700';
        // Bookings components
        listEl.innerHTML = bookings.map(booking => `
            <article class="border border-gray-300 rounded-lg p-4 bg-white shadow-sm">
                <h4 class="text-lg font-semibold">${booking.vehicleName || 'Vehicle'} (${booking.registrationNumber || 'N/A'})</h4>
                <p class="text-sm text-gray-700 mt-1"><span class="font-semibold">User: </span> ${booking.userEmail || 'N/A'}</p>
                <p class="text-sm text-gray-700"><span class="font-semibold">Dates: </span> ${booking.startDate} to ${booking.endDate}</p>
                <p class="text-sm text-gray-700"><span class="font-semibold">Status: </span> 
                    <span class="px-2 py-1 rounded text-xs font-semibold ${booking.status === 'CONFIRMED' ? 'bg-green-100 text-green-800' :
                booking.status === 'CANCELLED' ? 'bg-red-100 text-red-800' :
                    booking.status === 'ACTIVE' ? 'bg-blue-100 text-blue-800' :
                        booking.status === 'COMPLETED' ? 'bg-purple-100 text-purple-800' :
                            'bg-gray-100 text-gray-800'
            }">${booking.status}</span>
                </p>
                <p class="text-sm text-gray-500"><span class="font-semibold">Booked On: </span> ${booking.bookingDate || 'N/A'}</p>
            </article>
        `).join('');
    } catch (err) {
        statusEl.textContent = 'Failed to load bookings: ' + err.message;
        statusEl.className = 'mb-4 text-sm text-red-600';
    }
}

function handleLogout() {
    AuthService.logout();
    window.location.href = '/index.html';
}
