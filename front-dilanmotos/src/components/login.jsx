import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../auth.css';

const Login = () => {
    const navigate = useNavigate();
    const [credenciales, setCredenciales] = useState({
        correo: '',
        contrasena: ''
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setCredenciales(prev => ({ ...prev, [name]: value }));
    };

    const handleLogin = async (e) => {
        e.preventDefault(); 
        try {
            const response = await fetch("http://localhost:8080/api/usuarios/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(credenciales)
            });

            if (response.ok) {
                const usuario = await response.json();
                
                // --- DEPURACIÓN CRÍTICA ---
                console.log("Respuesta completa del servidor:", usuario);
                
                // Intentamos capturar el ID sin importar cómo se llame en el JSON
                const idFinal = usuario.idUsuario || usuario.id || usuario.id_usuario;

                if (!idFinal) {
                    console.error("ERROR: El backend no envió un ID de usuario válido.");
                }

                // Guardamos los datos de sesión
                localStorage.setItem('isAuthenticated', 'true'); 
                localStorage.setItem("idUsuario", idFinal); // Aquí se guarda el número real
                localStorage.setItem("nombreUsuario", usuario.nombre);
                localStorage.setItem('token', usuario.token); 
                localStorage.setItem("rolUsuario", usuario.rol || 'USER'); 
                
                // Redirección
                window.location.href = "/dashboard"; 

            } else {
                alert("Correo o contraseña incorrectos.");
            }
        } catch (error) {
            alert("Error de conexión. Revisa que el servidor esté activo.");
        }
    };

    return (
        <div className="auth-body">
            <div className="auth-card">
                <h2>Dilan Motos</h2>
                <p style={{ textAlign: 'center', color: '#666' }}>Inicia sesión para entrar al taller</p>
                <form onSubmit={handleLogin}>
                    <div className="form-group">
                        <label>Correo Electrónico</label>
                        <input className="auth-input" type="email" name="correo" value={credenciales.correo} onChange={handleChange} required />
                    </div>
                    <div className="form-group">
                        <label>Contraseña</label>
                        <input className="auth-input" type="password" name="contrasena" value={credenciales.contrasena} onChange={handleChange} required />
                    </div>
                    <button type="submit" className="auth-btn-primary">Entrar al Sistema</button>
                    <div style={{ marginTop: '20px', textAlign: 'center', fontSize: '0.85rem' }}>
                        ¿No tienes cuenta? <span onClick={() => navigate("/register")} style={{ color: '#3b46d8', cursor: 'pointer', fontWeight: 'bold' }}>Regístrate aquí</span>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Login;