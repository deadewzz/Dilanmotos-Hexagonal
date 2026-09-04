import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './perfilUsuario.css';

const PerfilUsuario = () => {
    const navigate = useNavigate();
    const [usuario, setUsuario] = useState(null);
    const [cargando, setCargando] = useState(true);
    const [enviandoClave, setEnviandoClave] = useState(false);
    const [seguridadAbierto, setSeguridadAbierto] = useState(false);

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

            if (!id || !token) {
                localStorage.clear();
                navigate('/login');
                return;
            }

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
                } else if (response.status === 401 || response.status === 403) {
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

    const handleCambiarContrasena = async (e) => {
        e.preventDefault();
        const idUsuario = localStorage.getItem('idUsuario');
        const token = localStorage.getItem('token');

        if (claves.contrasenaNueva !== claves.confirmarNueva) {
            alert("La nueva contraseña y su confirmación no coinciden.");
            return;
        }

        setEnviandoClave(true);

        try {
            const response = await fetch("http://localhost:8080/api/usuarios/cambiar-contrasena", {
                method: "POST",
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    idUsuario: idUsuario,
                    contrasenaActual: claves.contrasenaActual,
                    contrasenaNueva: claves.contrasenaNueva
                })
            });

            const data = await response.json().catch(() => ({}));

            if (response.ok) {
                alert(data.mensaje || "Contraseña cambiada exitosamente.");
                setClaves({ contrasenaActual: '', contrasenaNueva: '', confirmarNueva: '' });
                setSeguridadAbierto(false);
            } else {
                alert(data.error || data.mensaje || "Error al cambiar la contraseña.");
            }
        } catch (error) {
            alert("Error de conexión al intentar cambiar la contraseña.");
        } finally {
            setEnviandoClave(false);
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
                {/* Tarjeta Usuario */}
                <div className="perfil-card main-info">
                    <div className="avatar-section">
                        <img src="/iconoPerfil.png" alt="Usuario" className="avatar-img" />
                        <span className="badge-rol">{usuario?.rol || 'USER'}</span>
                    </div>
                    <div className="info-section">
                        <h1>{usuario?.nombre || 'Usuario'}</h1>
                        <p className="email-text">
                            <i className="fa-solid fa-envelope"></i> {usuario?.correo || 'correo@ejemplo.com'}
                        </p>
                    </div>
                </div>

                {/* Acordeón Seguridad */}
                <div className="seguridad-accordion">
                    <div 
                        className={`seguridad-header ${seguridadAbierto ? 'abierto' : ''}`}
                        onClick={() => setSeguridadAbierto(!seguridadAbierto)}
                    >
                        <div className="seguridad-title">
                            <i className="fa-solid fa-lock"></i>
                            <span>Seguridad de la Cuenta</span>
                        </div>
                        <i className={`fa-solid fa-chevron-down arrow-icon ${seguridadAbierto ? 'rotate' : ''}`}></i>
                    </div>
                    
                    {seguridadAbierto && (
                        <div className="seguridad-body">
                            <h3>Cambiar Contraseña</h3>
                            <form onSubmit={handleCambiarContrasena} className="form-cambiar-clave">
                                <div className="form-group">
                                    <label>Contraseña Actual</label>
                                    <input 
                                        type="password" 
                                        name="contrasenaActual" 
                                        value={claves.contrasenaActual} 
                                        onChange={handleClaveChange} 
                                        required 
                                    />
                                </div>
                                <div className="form-row">
                                    <div className="form-group">
                                        <label>Nueva Contraseña</label>
                                        <input 
                                            type="password" 
                                            name="contrasenaNueva" 
                                            value={claves.contrasenaNueva} 
                                            onChange={handleClaveChange} 
                                            required 
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label>Confirmar Nueva Contraseña</label>
                                        <input 
                                            type="password" 
                                            name="confirmarNueva" 
                                            value={claves.confirmarNueva} 
                                            onChange={handleClaveChange} 
                                            required 
                                        />
                                    </div>
                                </div>
                                <button type="submit" disabled={enviandoClave} className="btn-actualizar-clave">
                                    {enviandoClave ? 'Actualizando...' : 'Actualizar Contraseña'}
                                </button>
                            </form>
                        </div>
                    )}
                </div>

                {/* Sección Garaje */}
                <div className="motos-section">
                    <h2 className="section-title">
                        <i className="fa-solid fa-motorcycle"></i> Mi Garaje
                    </h2>
                    
                    <div className="motos-grid">
                        {usuario?.motos && usuario.motos.length > 0 ? (
                            usuario.motos.map((moto, index) => (
                                <div key={moto.id || index} className="moto-card-premium">
                                    <div className="moto-card-header">
                                        <span className="marca-tag">{moto.nombreMarca || 'Marca'}</span>
                                        <span className="cilindraje-tag">{moto.cilindraje} CC</span>
                                    </div>
                                    <h3 className="modelo-text">{moto.modelo}</h3>
                                    <div className="moto-card-footer">
                                        <span className="estado-ok">
                                            <i className="fa-solid fa-hashtag"></i> Tu placa es: {moto.placa || 'N/A'}
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