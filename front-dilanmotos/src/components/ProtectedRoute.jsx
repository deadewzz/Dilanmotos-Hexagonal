import React from 'react';
import Error403 from '../pages/Error403';

export default function ProtectedRoute({ children, roleRequerido }) {
    const token = localStorage.getItem('token');
    // Asumiendo que guardas el rol en localStorage o lo extraes del Token JWT
    const rolUsuario = localStorage.getItem('rolUsuario'); 

    // 1. Si no hay sesión iniciada, redirige al Login
    if (!token) {
        return <Error403 />; // O redireccionar a /login según la política del sistema
    }

    // 2. Si el rol no coincide con el requerido, renderiza la vista de Excepción/Error
    if (roleRequerido && rolUsuario !== roleRequerido) {
        return <Error403 />;
    }

    // 3. Si cumple con los permisos, renderiza el componente hijo
    return children;
}

const isTokenValid = (token) => {
    if (!token) return false;

    try {
        const parts = token.split('.');
        if (parts.length !== 3) return false;

        // Decodificar el payload en Base64Url
        const payloadBase64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
        const decodedPayload = JSON.parse(window.atob(payloadBase64));

        // Verificar si la fecha de expiración (exp) ya pasó
        if (decodedPayload.exp) {
            const currentTime = Math.floor(Date.now() / 1000);
            if (decodedPayload.exp < currentTime) {
                console.warn("⚠️ Sesión expirada en el cliente.");
                return false;
            }
        }

        return true;
    } catch (e) {
        console.error("❌ Token corrupto o malformado:", e);
        return false;
    }
};

export default function PrivateRoute() {
    const token = localStorage.getItem('token');

    if (!isTokenValid(token)) {
        // Limpiar almacenamiento si el token no es válido o expiró
        localStorage.removeItem('token');
        localStorage.removeItem('idUsuario');
        localStorage.removeItem('rol');
        
        return <Navigate to="/login" replace />;
    }

    return <Outlet />;
}