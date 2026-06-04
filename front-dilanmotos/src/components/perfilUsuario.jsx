import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './perfilUsuario.css';

const PerfilUsuario = () => {
    const navigate = useNavigate();
    const [usuario, setUsuario] = useState(null);
    const [cargando, setCargando] = useState(true);

    // Estado exclusivo para cambiar contraseña
    const [claves, setClaves] = useState({
        contrasenaActual: '',
        contrasenaNueva: '',
        confirmarNueva: ''
    });

    const handleClaveChange = (e) => {
        const { name, value } = e.target;
        setClaves(prev => ({ ...prev, [name]: value }));
    };

    useEffect(() => {
        const cargarDatos = async () => {
            const id = localStorage.getItem('idUsuario');
            const token = localStorage.getItem('token'); 

            try {
                const response = await fetch(`http://localhost:8080/api/usuarios/${id}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });

                if (response.ok) {
                    const data = await response.json();
                    setUsuario(data);
                } else if (response.status === 401) {
                    localStorage.clear();
                    navigate('/login');
                }
            } catch (error) {
                console.error("Error al obtener perfil:", error);
            } finally {
                setCargando(false);
            }
        };
        cargarDatos();
    }, [navigate]);

    // Gestión del envío de cambio de contraseña
    const handleCambiarContrasena = async (e) => {
        e.preventDefault();
        const idUsuario = localStorage.getItem('idUsuario');
        const token = localStorage.getItem('token');

        if (claves.contrasenaNueva !== claves.confirmarNueva) {
            alert("La nueva contraseña y su confirmación no coinciden.");
            return;
        }

        try {
            const response = await fetch("http://localhost:8080/api/usuarios/cambiar-contrasena", {
                method: "POST",
                headers: {
                    'Authorization': `Bearer ${token}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    idUsuario: idUsuario,
                    contrasenaActual: claves.contrasenaActual,
                    contrasenaNueva: claves.contrasenaNueva
                })
            });

            const data = await response.json();

            if (response.ok) {
                alert(data.mensaje || "Contraseña cambiada exitosamente.");
                // Limpiamos los inputs
                setClaves({ contrasenaActual: '', contrasenaNueva: '', confirmarNueva: '' });
            } else {
                alert(data.error || "Ocurrió un error al cambiar la contraseña.");
            }
        } catch (error) {
            alert("Error de conexión al intentar cambiar la contraseña.");
        }
    };

    if (cargando) return (
        <div className="perfil-loader">
            <div className="spinner"></div>
            <p>Cargando tu garaje...</p>
        </div>
    );

    return (
        <div className="perfil-wrapper">
            <div className="perfil-header-bg">
                <button className="btn-volver" onClick={() => navigate('/dashboard')}>
                    <i className="fa-solid fa-chevron-left"></i> Volver
                </button>
            </div>

            <div className="perfil-content">
                <div className="perfil-card main-info">
                    <div className="avatar-section">
                        <img src="/iconoPerfil.png" alt="Usuario" className="avatar-img" />
                        <span className="badge-rol">{usuario?.rol || 'SOCIO'}</span>
                    </div>
                    <div className="info-section">
                        <h1>{usuario?.nombre || 'Usuario'}</h1>
                        <p className="email-text">
                            <i className="fa-solid fa-envelope"></i> {usuario?.correo || 'correo@ejemplo.com'}
                        </p>
                    </div>
                </div>

                {/* ── NUEVA SECCIÓN: SEGURIDAD Y CONFIGURACIÓN ── */}
                <div className="motos-section" style={{ marginTop: '30px' }}>
                    <h2 className="section-title">
                        <i className="fa-solid fa-lock"></i> Seguridad de la Cuenta
                    </h2>
                    <div className="perfil-card" style={{ padding: '20px', background: '#fff', borderRadius: '12px', boxShadow: '0 4px 10px rgba(0,0,0,0.05)' }}>
                        <h3 style={{ marginBottom: '15px', fontSize: '1.1rem', color: '#333' }}>Cambiar Contraseña</h3>
                        
                        <form onSubmit={handleCambiarContrasena} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                                <label style={{ fontSize: '0.85rem', fontWeight: '600', color: '#555' }}>Contraseña Actual</label>
                                <input type="password" name="contrasenaActual" value={claves.contrasenaActual} onChange={handleClaveChange} required style={{ padding: '10px', borderRadius: '6px', border: '1px solid #ccc' }} />
                            </div>
                            
                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
                                <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                                    <label style={{ fontSize: '0.85rem', fontWeight: '600', color: '#555' }}>Nueva Contraseña</label>
                                    <input type="password" name="contrasenaNueva" value={claves.contrasenaNueva} onChange={handleClaveChange} required style={{ padding: '10px', borderRadius: '6px', border: '1px solid #ccc' }} />
                                </div>
                                <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                                    <label style={{ fontSize: '0.85rem', fontWeight: '600', color: '#555' }}>Confirmar Nueva Contraseña</label>
                                    <input type="password" name="confirmarNueva" value={claves.confirmarNueva} onChange={handleClaveChange} required style={{ padding: '10px', borderRadius: '6px', border: '1px solid #ccc' }} />
                                </div>
                            </div>

                            <button type="submit" style={{ alignSelf: 'flex-start', background: '#ec5e2a', color: '#fff', border: 'none', padding: '10px 20px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', marginTop: '5px' }}>
                                Actualizar Contraseña
                            </button>
                        </form>
                    </div>
                </div>

                <div className="motos-section">
                    <h2 className="section-title">
                        <i className="fa-solid fa-motorcycle"></i> Mi Garaje
                    </h2>
                    
                    <div className="motos-grid">
                        {usuario?.motos && usuario.motos.length > 0 ? (
                            usuario.motos.map((moto, index) => (
                                <div key={index} className="moto-card-premium">
                                    <div className="moto-card-header">
                                        <span className="marca-tag">{moto.nombreMarca || 'Marca'}</span>
                                        <span className="cilindraje-tag">{moto.cilindraje} CC</span>
                                    </div>
                                    <h3 className="modelo-text">{moto.modelo}</h3>
                                    <div className="moto-card-footer">
                                        <span className="estado-ok">
                                            <i className="fa-solid fa-check-double"></i> Documentos al día
                                        </span>
                                    </div>
                                </div>
                            ))
                        ) : (
                            <div className="no-motos">
                                <i className="fa-solid fa-tools"></i>
                                <p>Aún no tienes motos registradas.</p>
                                <button className="btn-registrar" onClick={() => navigate('/motos')}>
                                    Registrar Ahora
                                </button>
                            </div>
                        )}
                    </div>
                </div>

                <div className="perfil-footer">
                    <button className="btn-logout" onClick={() => { localStorage.clear(); navigate('/login'); }}>
                        <i className="fa-solid fa-power-off"></i> Cerrar Sesión
                    </button>
                </div>
            </div>
        </div>
    );
};

export default PerfilUsuario;