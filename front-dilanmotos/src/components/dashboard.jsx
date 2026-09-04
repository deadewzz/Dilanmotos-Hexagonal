import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Dashboard.css';

const Dashboard = () => {
    const navigate = useNavigate();
    const [showDropdown, setShowDropdown] = useState(false);
    const [user, setUser] = useState({ nombre: "Invitado", rol: "GUEST", id: null });

    // Determinamos si hay un usuario logueado
    const isAuthenticated = !!localStorage.getItem('token');

    useEffect(() => {
        if (isAuthenticated) {
            const id = localStorage.getItem('idUsuario');
            const nombre = localStorage.getItem('nombreUsuario');
            const rol = localStorage.getItem('rolUsuario');
            setUser({ nombre: nombre || "Socio", rol: rol || "USER", id });
        }
    }, [isAuthenticated]);

    const handleLogout = () => {
        localStorage.clear();
        setUser({ nombre: "Invitado", rol: "GUEST", id: null });
        navigate('/login');
    };

    return (
        <div className="dashboard-wrapper">
            <header className="dashboard-header">
                <div className="header-container">
                    
                    {/* 🏍️ LOGO Y NOMBRE DE LA MARCA */}
                    <div className="brand-logo-container" onClick={() => navigate('/dashboard')}>
                        <img 
                            src="/LogoDilanMotos.png" 
                            alt="Logo Dilan Motos" 
                            className="main-logo" 
                        />
                        <span className="brand-name">DilanMotos</span>
                    </div>
                    
                    <div className="header-nav">
                        {/* 📱 BOTÓN DE DESCARGA APP MÓVIL */}
                        <a 
                            href={`${import.meta.env.BASE_URL}DilanMotos.apk`}
                            download="DilanMotos.apk" 
                            target="_blank"
                            rel="noopener noreferrer"
                            className="btn-download-apk"
                            title="Descargar Aplicación Móvil para Android"
                        >
                            <i className="fa-solid fa-mobile-screen-button"></i>
                            <span>Descargar App</span>
                        </a>

                        {/* 👤 SECCIÓN DE USUARIO CON FLECHA ROTATIVA */}
                        <div 
                            className={`user-trigger ${showDropdown ? 'active' : ''}`}
                            onClick={() => setShowDropdown(!showDropdown)}
                        >
                            <img src="/iconoPerfil.png" alt="Perfil" className="nav-icon avatar" />
                            <span className="user-name">{user.nombre}</span>
                            <i className="fa-solid fa-chevron-down dropdown-arrow"></i>
                        </div>

                        {/* 📋 MENÚ DESPLEGABLE CON ICONOS */}
                        {showDropdown && (
                            <ul className="dropdown-menu-custom">
                                {isAuthenticated ? (
                                    <>
                                        <li>
                                            <Link to="/perfil">
                                                <i className="fa-regular fa-user"></i> Mi Cuenta
                                            </Link>
                                        </li>
                                        <li>
                                            <Link to="/asistente">
                                                <i className="fa-solid fa-robot"></i> Asistente IA
                                            </Link>
                                        </li>
                                        <li>
                                            <Link to="/historial">
                                                <i className="fa-solid fa-clock-rotate-left"></i> Mi Historial
                                            </Link>
                                        </li>
                                        <li>
                                            <Link to="/nueva-pqrs">
                                                <i className="fa-regular fa-paper-plane"></i> Radicar PQRS
                                            </Link>
                                        </li>
                                        <li>
                                            <Link to="/hacer-cotizacion">
                                                <i className="fa-solid fa-calculator"></i> Hacer Cotización
                                            </Link>
                                        </li>

                                        {user.rol === 'ADMIN' && (
                                            <>
                                                <li className="divider"></li>
                                                <li>
                                                    <Link to="/usuarios" className="admin-link">
                                                        <i className="fa-solid fa-shield-halved"></i> Panel de administración
                                                    </Link>
                                                </li>
                                            </>
                                        )}

                                        <li className="divider"></li>
                                        <li>
                                            <button onClick={handleLogout} className="logout-btn-custom">
                                                <i className="fa-solid fa-right-from-bracket"></i> Cerrar Sesión
                                            </button>
                                        </li>
                                    </>
                                ) : (
                                    <>
                                        <li>
                                            <Link to="/login">
                                                <i className="fa-solid fa-arrow-right-to-bracket"></i> Iniciar Sesión
                                            </Link>
                                        </li>
                                        <li>
                                            <Link to="/register">
                                                <i className="fa-solid fa-user-plus"></i> Registrarse
                                            </Link>
                                        </li>
                                    </>
                                )}
                            </ul>
                        )}
                    </div>
                </div>
            </header>

            <main className="dashboard-content">
                <div className="hero-section text-center">
                    <h1 style={{ marginBottom: '20px', fontWeight: '800' }}>Mantenimiento Inteligente</h1>
                    <Link to="/recomendaciones" className="promo-banner">
                        Ver Recomendaciones de la IA
                    </Link>
                </div>

                <h2 className="section-subtitle" style={{ margin: '40px 0 20px 0', fontWeight: '700' }}>Nuestros Productos</h2>
                <div className="categories-grid">
                    <div className="category-item">
                        <div className="category-img"><img src="/KitDeArrastre.png" alt="Kits" /></div>
                        <h3>Kits de Arrastre</h3>
                        <Link to="/catalogoKit" className="category-btn">Ver Catálogo</Link>
                    </div>

                    <div className="category-item">
                        <div className="category-img"><img src="/Llanta.png" alt="Llantas" /></div>
                        <h3>Llantas</h3>
                        <Link to="/catalogoLlantas" className="category-btn">Ver Catálogo</Link>
                    </div>

                    <div className="category-item">
                        <div className="category-img"><img src="/AceiteMotul.png" alt="Aceites" /></div>
                        <h3>Aceites</h3>
                        <Link to="/catalogoAceites" className="category-btn">Ver Catálogo</Link>
                    </div>
                </div>
            </main>

            <footer className="dashboard-footer">
                <div className="btn-tech-support">Soporte Técnico: 301-353-6723</div>
            </footer>
        </div>
    );
};

export default Dashboard;