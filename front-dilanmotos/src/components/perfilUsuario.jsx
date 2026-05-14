import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './perfilUsuario.css';

const PerfilUsuario = () => {
    const navigate = useNavigate();
    const [usuario, setUsuario] = useState(null);
    const [cargando, setCargando] = useState(true);

    useEffect(() => {
        const cargarDatos = async () => {
            const id = localStorage.getItem('idUsuario');
            const token = localStorage.getItem('token'); // Recuperamos el carnet del taller

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
                    // Si el carnet venció, lo mandamos al login
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

    if (cargando) return (
        <div className="perfil-loader">
            <div className="spinner"></div>
            <p>Cargando tu garaje...</p>
        </div>
    );

    return (
        <div className="perfil-wrapper">
            {/* Fondo decorativo superior */}
            <div className="perfil-header-bg">
                <button className="btn-volver" onClick={() => navigate('/dashboard')}>
                    <i className="fa-solid fa-chevron-left"></i> Volver
                </button>
            </div>

            <div className="perfil-content">
                {/* TARJETA DE INFORMACIÓN PERSONAL */}
                <div className="perfil-card main-info">
                    <div className="avatar-section">
                        <img src="/iconoPerfil.png" alt="Usuario" className="avatar-img" />
                        <span className="badge-rol">{usuario?.rol || 'SOCIO'}</span>
                    </div>
                    <div className="info-section">
                        {/* Usamos ?. para que no explote si usuario es null al inicio */}
                        <h1>{usuario?.nombre || 'Usuario'}</h1>
                        <p className="email-text">
                            <i className="fa-solid fa-envelope"></i> {usuario?.correo || 'correo@ejemplo.com'}
                        </p>
                    </div>
                </div>

                {/* SECCIÓN DE MOTOS REGISTRADAS */}
                <div className="motos-section">
                    <h2 className="section-title">
                        <i className="fa-solid fa-motorcycle"></i> Mi Garaje
                    </h2>
                    
                    <div className="motos-grid">
                        {usuario?.motos && usuario.motos.length > 0 ? (
                            usuario.motos.map((moto, index) => (
                                <div key={index} className="moto-card-premium">
                                    <div className="moto-card-header">
                                        <span className="marca-tag">{moto.marca?.nombre || 'Marca'}</span>
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

                {/* BOTÓN DE CIERRE */}
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