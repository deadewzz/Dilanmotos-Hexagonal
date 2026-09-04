import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../auth.css';

const Login = () => {
    const navigate = useNavigate();
    const [errorMensaje, setErrorMensaje] = useState('');
    const [vista, setVista] = useState('login'); 
    
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

    const handleLogin = async (e) => {
        e.preventDefault(); 
        setErrorMensaje('');
        try {
            const response = await fetch("http://localhost:8080/api/usuarios/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(credenciales)
            });

            if (response.ok) {
                const usuario = await response.json();
                const idFinal = usuario.idUsuario || usuario.id || usuario.id_usuario;

                localStorage.setItem('isAuthenticated', 'true'); 
                localStorage.setItem("idUsuario", idFinal); 
                localStorage.setItem("nombreUsuario", usuario.nombre);
                localStorage.setItem("correoUsuario", usuario.correo);
                localStorage.setItem('token', usuario.token); 
                localStorage.setItem("rolUsuario", usuario.rol || 'USER'); 
                
                window.location.href = "/dashboard"; 

            } else if (response.status === 404) {
                const data = await response.json();
                setErrorMensaje(data.mensaje); 
            } else if (response.status === 401) {
                const data = await response.json();
                setErrorMensaje(data.mensaje); 
            } else {
                setErrorMensaje("Ocurrió un error al iniciar sesión.");
            }
        } catch (error) {
            setErrorMensaje("Error de conexión. Revisa que el servidor esté activo.");
        }
    };

    const handleSolicitarRecuperacion = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch("http://localhost:8080/api/usuarios/recuperar-contrasena", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ correo: correoRecuperacion })
            });

            const data = await response.json();
            
            // Si el correo EXISTE y se envió el código (Código 200)
            if (response.ok) {
                alert(data.mensaje || "Código de recuperación enviado. Revisa tu correo.");
                setVista('resetear'); 
                
            // Si el correo NO EXISTE en la BD (Código 404)
            } else if (response.status === 404) {
                alert(data.error || "Ese correo no está registrado en la base de datos.");
                
            } else {
                alert(data.error || data.mensaje || "Ocurrió un error.");
            }
        } catch (error) {
            alert("Error de conexión al solicitar recuperación.");
        }
    };

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
                setVista('login');
            } else {
                alert(data.error || data.mensaje || "Token inválido o expirado.");
            }
        } catch (error) {
            alert("Error de conexión al restablecer contraseña.");
        }
    };

    return (
        <div className="auth-body">
            <div className="auth-card">
                <h2>Dilan Motos</h2>
                
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

                            {errorMensaje && (
                                <p style={{ color: '#c0392b', textAlign: 'center', fontSize: '0.9rem', marginBottom: '10px' }}>
                                    {errorMensaje.toLowerCase().includes('no está registrado') ? (
                                        <>
                                            Ese correo no está registrado en la base de datos,{' '}
                                            <span
                                                onClick={() => navigate('/register')}
                                                style={{ color: '#3b46d8', cursor: 'pointer', fontWeight: 'bold', textDecoration: 'underline' }}
                                            >
                                                regístrate aquí
                                            </span>
                                        </>
                                    ) : (
                                        errorMensaje
                                    )}
                                </p>
                            )}

                            <button type="submit" name="IniciarSesión" className="auth-btn-primary">Entrar al Sistema</button>
                            
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