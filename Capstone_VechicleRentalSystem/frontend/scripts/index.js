// Index page scripts
// switchToSignup, switchToLogin, handleLogin, handleSignup

function switchToSignup() {
    document.getElementById('loginDiv').classList.add('hidden');
    document.getElementById('signupDiv').classList.remove('hidden');
}

function switchToLogin() {
    document.getElementById('loginDiv').classList.remove('hidden');
    document.getElementById('signupDiv').classList.add('hidden');
}

async function handleLogin(e) {
    e.preventDefault();
    const msg = document.getElementById('loginMessage');
    try {
        const data = await AuthService.login(
            document.getElementById('loginEmail').value,
            document.getElementById('loginPassword').value
        );

        AuthService.setCurrentUser(data.user);
        msg.textContent = 'Login successful! Redirecting...';
        msg.className = 'mt-4 text-center text-sm text-green-600';

        setTimeout(() => {
            window.location.href = '/app.html';
        }, 1500);
    } catch (err) {
        msg.textContent = 'Error: ' + err.message;
        msg.className = 'mt-4 text-center text-sm text-red-600';
    }
}

async function handleSignup(e) {
    e.preventDefault();
    const msg = document.getElementById('signupMessage');
    try {
        const data = await AuthService.signup(
            document.getElementById('signupName').value,
            document.getElementById('signupEmail').value,
            document.getElementById('signupPassword').value,
            'USER'
        );

        AuthService.setCurrentUser(data.user);
        msg.textContent = 'Account created! Switching to login...';
        msg.className = 'mt-4 text-center text-sm text-green-600';
        setTimeout(switchToLogin, 1500);
    } catch (err) {
        msg.textContent = 'Error: ' + err.message;
        msg.className = 'mt-4 text-center text-sm text-red-600';
    }
}
