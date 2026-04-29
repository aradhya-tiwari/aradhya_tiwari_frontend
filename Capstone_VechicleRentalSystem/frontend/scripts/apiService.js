
// ApiService - Handles API calls with JWT authentication
// Automatically includes JWT token in Authorization header


class ApiService {
    static BASE_URL = 'http://localhost:8080/api';

    //Get headers 
    static getHeaders(contentType = 'application/json') {
        const headers = {
            'Content-Type': contentType
        };

        const token = AuthService.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        return headers;
    }


    static async get(endpoint) {
        try {
            const response = await fetch(`${this.BASE_URL}${endpoint}`, {
                method: 'GET',
                headers: this.getHeaders(),
                credentials: 'include'
            });

            return this.handleResponse(response);
        } catch (error) {
            throw new Error(`GET ${endpoint} failed: ${error.message}`);
        }
    }

    // POST Request 
    static async post(endpoint, data) {
        try {
            const response = await fetch(`${this.BASE_URL}${endpoint}`, {
                method: 'POST',
                headers: this.getHeaders(),
                credentials: 'include',
                body: JSON.stringify(data)
            });

            return this.handleResponse(response);
        } catch (error) {
            throw new Error(`POST ${endpoint} failed: ${error.message}`);
        }
    }

    // PUT request
    static async put(endpoint, data) {
        try {
            const response = await fetch(`${this.BASE_URL}${endpoint}`, {
                method: 'PUT',
                headers: this.getHeaders(),
                credentials: 'include',
                body: JSON.stringify(data)
            });

            return this.handleResponse(response);
        } catch (error) {
            throw new Error(`PUT ${endpoint} failed: ${error.message}`);
        }
    }

    // DELETE Request ()
    static async delete(endpoint) {
        try {
            const response = await fetch(`${this.BASE_URL}${endpoint}`, {
                method: 'DELETE',
                headers: this.getHeaders(),
                credentials: 'include'
            });

            return this.handleResponse(response);
        } catch (error) {
            throw new Error(`DELETE ${endpoint} failed: ${error.message}`);
        }
    }

    // 
    static async handleResponse(response) {
        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            // If unauthorized, clear auth and redirect to login
            if (response.status === 401) {
                AuthService.logout();
                window.location.href = '/index.html';
            }
            throw new Error(data.message || `API error: ${response.status}`);
        }

        return data;
    }
}
