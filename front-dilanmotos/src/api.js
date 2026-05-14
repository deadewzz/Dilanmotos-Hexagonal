

const API_URL = "http://localhost:8080/api";

export const authFetch = async (url, options = {}) => {
    const token = localStorage.getItem("jwtToken"); // Asegúrate de guardar el token con esta llave al hacer login

    const headers = {
        "Content-Type": "application/json",
        ...options.headers,
        ...(token ? { "Authorization": `Bearer ${token}` } : {})
    };

    const res = await fetch(`${API_URL}${url}`, { ...options, headers });
    
    if (res.status === 401) {
        // El token expiró o es inválido
        localStorage.clear();
        window.location.href = "/login";
    }
    return res;
};