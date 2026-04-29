// App page scripts
window.addEventListener('DOMContentLoaded', () => {
    if (!AuthService.isAuthenticated()) {
        window.location.href = '/index.html';
        return;
    }

    const user = AuthService.getCurrentUser();
    const role = AuthService.getRole();

    if (user) {
        document.getElementById('userName').textContent = user.fullName;
    }

    if (role) {
        document.getElementById('userRole').textContent = role;
        if (role === "ADMIN")
            document.getElementById("admin-dashboard-panel").classList.remove("hidden")
    }
});

function handleLogout() {
    AuthService.logout();
    window.location.href = '/index.html';
}
