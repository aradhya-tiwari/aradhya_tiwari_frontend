
// AuthService - Handles JWT authentication
// Manages login, signup, and token storage with role-based redirection


const API_URL = 'http://localhost:8080/api/users';

class AuthService {
    // Decode base64URL of token and get the values
    static decodeToken(token) {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(
                atob(base64)
                    .split('')
                    .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                    .join('')
            );
            return JSON.parse(jsonPayload);
        } catch (err) {
            console.error('Failed to decode token:', err);
            return null;
        }
    }

    // Email password based login
    static async login(email, password) {
        try {
            const response = await fetch(`${API_URL}/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include', // Include cookies
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Login failed');
            }

            if (data.token) {
                this.setToken(data.token);
            }

            return data;
        } catch (error) {
            throw new Error('Login error: ' + error.message);
        }
    }


    // Signup user with full name, email, and password

    static async signup(fullName, email, password, role = 'USER') {
        try {
            const response = await fetch(`${API_URL}/signup`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include', // Include cookies
                body: JSON.stringify({ fullName, email, password, role })
            });

            const data = await response.json();

            if (response.status !== 201) {
                throw new Error(data.message || 'Signup failed');
            }

            if (data.token) {
                console.log(data.token)
                this.setToken(data.token);
            }

            return data;
        } catch (error) {
            throw new Error('Signup error: ' + error.message);
        }
    }

    // Authentication Check
    static isAuthenticated() {
        console.log(this.getToken())
        return this.getToken() !== null && this.getCurrentUser() !== null;
    }

    // Get JWT Token
    static getToken() {
        return localStorage.getItem('token');
    }

    //Save JWT token to local storage     
    static setToken(token) {
        localStorage.setItem('token', token);
    }

    //  Get User Role from localstorage
    static getRole() {
        const user = this.getCurrentUser();
        if (user && user.role) {
            return user.role;
        }

        const storedRole = localStorage.getItem('role');
        return storedRole ? storedRole : null;
    }

    // Check the role and redirect to either /app.html ot /admin.html
    static getRedirectUrl() {
        const role = this.getRole();
        return role === 'ADMIN' ? '/admin.html' : '/app.html';
    }

    // Clear authentication (logout)
    static logout() {
        // Clear any stored user data if needed
        localStorage.removeItem('user');
        localStorage.removeItem('role');
        localStorage.removeItem('token');
    }

    // Get current user from localstorage
    static getCurrentUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    }

    //Add current user to localstorage
    static setCurrentUser(user) {
        localStorage.setItem('user', JSON.stringify(user));
        if (user && user.role) {
            localStorage.setItem('role', user.role);
        }
    }
}
