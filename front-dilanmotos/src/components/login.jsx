import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../auth.css';

const Login = () => {
    const navigate = useNavigate();
    const [vista, setVista] = useState('login'); // Vistas posibles: 'login', 'solicitar', 'resetear'
    
    // Estados de los formularios
    const [credenciales, setCredenciales] = useState({ correo: '', contrasena: '' });
    const [correoRecuperacion, setCorreoRecuperacion] = useState('');
    const [resetDatos, setResetDatos] = useState({ token: '', nuevaContrasena: '' });

    const handleChangeLogin = (e) => {
        const { name, value } = e.target;
        setCredenciales(prev => ({ ...prev, [name]: value }));
    };

    const handleChangeReset = (e) => {
        const { name, value } = e.target;
        setResetDatos(prev => ({ ...prev, [name]: value }));
    };

    // ── 1. Login Normal ───────────────────────────────────
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
                console.log("Respuesta completa del servidor:", usuario);
                
                const idFinal = usuario.idUsuario || usuario.id || usuario.id_usuario;

                if (!idFinal) {
                    console.error("ERROR: El backend no envió un ID de usuario válido.");
                }

                localStorage.setItem('isAuthenticated', 'true'); 
                localStorage.setItem("idUsuario", idFinal); 
                localStorage.setItem("nombreUsuario", usuario.nombre);
                localStorage.setItem("correoUsuario", usuario.correo || usuario.email || credenciales.correo || '');
                localStorage.setItem('token', usuario.token); 
                localStorage.setItem("rolUsuario", usuario.rol || 'USER'); 
                
                window.location.href = "/dashboard"; 
            } else {
                alert("Correo o contraseña incorrectos.");
            }
        } catch (error) {
            alert("Error de conexión. Revisa que el servidor esté activo.");
        }
    };

    // ── 2. Enviar Correo de Recuperación ──────────────────
    const handleSolicitarRecuperacion = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch("http://localhost:8080/api/usuarios/recuperar-contrasena", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ correo: correoRecuperacion })
            });

            const data = await response.json();
            if (response.ok) {
                alert(data.mensaje || "Si el correo existe, recibirás las instrucciones.");
                setVista('resetear'); // Saltamos al formulario del Token
            } else {
                alert(data.error || "Ocurrió un error.");
            }
        } catch (error) {
            alert("Error de conexión al solicitar recuperación.");
        }
    };

    // ── 3. Resetear Contraseña con Token ─────────────────
    const handleResetearContrasena = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch("http://localhost:8080/api/usuarios/resetear-contrasena", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    token: resetDatos.token,
                    nuevaContrasena: resetDatos.nuevaContrasena
                })
            });

            const data = await response.json();
            if (response.ok) {
                alert(data.mensaje || "Contraseña restablecida correctamente.");
                setVista('login'); // Volvemos al Login para que pruebe su nueva clave
            } else {
                alert(data.error || "Token inválido o expirado.");
            }
        } catch (error) {
            alert("Error de conexión al restablecer contraseña.");
        }
    };

    return (
        <div className="auth-body">
            <div className="auth-card">
                <h2>Dilan Motos</h2>
                
                {/* ── VISITOR: LOGIN NORMAL ── */}
                {vista === 'login' && (
                    <>
                        <p style={{ textAlign: 'center', color: '#666' }}>Inicia sesión para entrar al taller</p>
                        <form onSubmit={handleLogin}>
                            <div className="form-group">
                                <label>Correo Electrónico</label>
                                <input className="auth-input" type="email" name="correo" value={credenciales.correo} onChange={handleChangeLogin} required />
                            </div>
                            <div className="form-group">
                                <label>Contraseña</label>
                                <input className="auth-input" type="password" name="contrasena" value={credenciales.contrasena} onChange={handleChangeLogin} required />
                            </div>
                            <button type="submit" className="auth-btn-primary">Entrar al Sistema</button>
                            
                            <div style={{ marginTop: '15px', textAlign: 'center', fontSize: '0.85rem' }}>
                                <span onClick={() => setVista('solicitar')} style={{ color: '#ec5e2a', cursor: 'pointer', fontWeight: 'bold' }}>
                                    ¿Olvidaste tu contraseña?
                                </span>
                            </div>
                            <div style={{ marginTop: '15px', textAlign: 'center', fontSize: '0.85rem' }}>
                                ¿No tienes cuenta? <span onClick={() => navigate("/register")} style={{ color: '#3b46d8', cursor: 'pointer', fontWeight: 'bold' }}>Regístrate aquí</span>
                            </div>
                        </form>
                    </>
                )}

                {/* ── VISITOR: SOLICITAR RECUPERACIÓN ── */}
                {vista === 'solicitar' && (
                    <>
                        <p style={{ textAlign: 'center', color: '#666' }}>Recuperar Contraseña</p>
                        <form onSubmit={handleSolicitarRecuperacion}>
                            <div className="form-group">
                                <label>Introduce tu Correo Registrado</label>
                                <input className="auth-input" type="email" value={correoRecuperacion} onChange={(e) => setCorreoRecuperacion(e.target.value)} required />
                            </div>
                            <button type="submit" className="auth-btn-primary">Enviar Código</button>
                            
                            <div style={{ marginTop: '20px', textAlign: 'center', fontSize: '0.85rem' }}>
                                <span onClick={() => setVista('login')} style={{ color: '#3b46d8', cursor: 'pointer' }}>
                                    Volver al Login
                                </span>
                            </div>
                        </form>
                    </>
                )}

                {/* ── VISITOR: RESETEAR CON TOKEN ── */}
                {vista === 'resetear' && (
                    <>
                        <p style={{ textAlign: 'center', color: '#666' }}>Restablecer Contraseña</p>
                        <form onSubmit={handleResetearContrasena}>
                            <div className="form-group">
                                <label>Código de Verificación (Token)</label>
                                <input className="auth-input" type="text" name="token" placeholder="Ej: ABC123" value={resetDatos.token} onChange={handleChangeReset} required />
                            </div>
                            <div className="form-group">
                                <label>Nueva Contraseña (Mínimo 6 caracteres)</label>
                                <input className="auth-input" type="password" name="nuevaContrasena" value={resetDatos.nuevaContrasena} onChange={handleChangeReset} required />
                            </div>
                            <button type="submit" className="auth-btn-primary">Actualizar Contraseña</button>
                            
                            <div style={{ marginTop: '20px', textAlign: 'center', fontSize: '0.85rem' }}>
                                <span onClick={() => setVista('login')} style={{ color: '#3b46d8', cursor: 'pointer' }}>
                                    Cancelar y Volver al Login
                                </span>
                            </div>
                        </form>
                    </>
                )}
            </div>
        </div>
    );
};

export default Login;