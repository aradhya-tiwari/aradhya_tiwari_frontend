// Bookings page scripts
window.addEventListener('DOMContentLoaded', async () => {
    if (!AuthService.isAuthenticated()) {
        window.location.href = '/index.html';
        return;
    }
    await loadMyBookings();
});

async function loadMyBookings() {
    const statusEl = document.getElementById('myBookingStatus');
    const listEl = document.getElementById('myBookingList');

    statusEl.textContent = 'Loading bookings...';
    statusEl.className = 'mb-4 text-sm text-gray-600';
    listEl.innerHTML = '';

    try {
        const bookings = await ApiService.get('/bookings/my');

        if (!bookings || bookings.length === 0) {
            statusEl.textContent = 'No bookings yet.';
            statusEl.className = 'mb-4 text-sm text-yellow-700';
            return;
        }

        statusEl.textContent = `Showing ${bookings.length} booking(s).`;
        statusEl.className = 'mb-4 text-sm text-green-700';

        listEl.innerHTML = bookings.map(booking => `
            <article class="border border-gray-300 rounded-lg p-4 bg-white shadow-sm">
                <h4 class="text-lg font-semibold">${booking.vehicleName || 'Vehicle'} (${booking.registrationNumber || 'N/A'})</h4>
                <p class="text-sm text-gray-700 mt-1"><span class="font-semibold">Dates:</span> ${booking.startDate} to ${booking.endDate}</p>
                <p class="text-sm text-gray-700"><span class="font-semibold">Status:</span> ${booking.status}</p>
                <p class="text-sm text-gray-500"><span class="font-semibold">Booked On:</span> ${booking.bookingDate || 'N/A'}</p>
                ${booking.status !== 'CANCELLED' ? `
                    <button class="mt-3 bg-red-500 hover:bg-red-600 text-white px-3 py-2 rounded"
                        onclick="cancelBooking(${booking.bookingId})">
                        Cancel Booking
                    </button>
                ` : '<p class="mt-3 text-sm text-gray-500">Already cancelled</p>'}
            </article>
        `).join('');
    } catch (err) {
        statusEl.textContent = 'Failed to load bookings: ' + err.message;
        statusEl.className = 'mb-4 text-sm text-red-600';
    }
}

async function cancelBooking(bookingId) {
    try {
        await ApiService.put(`/bookings/${bookingId}/cancel`, {});
        await loadMyBookings();
    } catch (err) {
        const statusEl = document.getElementById('myBookingStatus');
        statusEl.textContent = 'Failed to cancel booking: ' + err.message;
        statusEl.className = 'mb-4 text-sm text-red-600';
    }
}

function handleLogout() {
    AuthService.logout();
    window.location.href = '/index.html';
}
