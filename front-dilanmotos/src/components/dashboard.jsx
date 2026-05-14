import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Dashboard.css';

const Dashboard = () => {
    const navigate = useNavigate();
    const [showDropdown, setShowDropdown] = useState(false);
    const [user, setUser] = useState({ nombre: "Invitado", rol: "GUEST", id: null });

    // ✅ Determinamos si hay alguien logueado
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
                    <img 
                        src="/LogoDilanMotos.png" 
                        alt="Dilan Motos" 
                        className="main-logo" 
                        style={{cursor: 'pointer'}} 
                        onClick={() => navigate('/dashboard')} 
                    />
                    
                    <div className="header-nav">
                        <div className="user-trigger" onClick={() => setShowDropdown(!showDropdown)}>
                            <img src="/iconoPerfil.png" alt="Perfil" className="nav-icon avatar" />
                            <span>{user.nombre}</span>
                        </div>

                        {showDropdown && (
                            <ul className="dropdown-menu-custom shadow-lg">
                                {isAuthenticated ? (
                                    // 🔓 Opciones para usuarios LOGUEADOS
                                    <>
                                        <li><Link to="/perfil">Mi Cuenta</Link></li>
                                        <li><Link to="/asistente">Asistente IA</Link></li>
                                        <li><Link to="/historial">Mi Historial</Link></li>
                                        <li><Link to="/nueva-pqrs">Radicar PQRS</Link></li>
                                        {user.rol === 'ADMIN' && (
                                            <>
                                                <li className="divider"></li>
                                                <li><Link to="/usuarios" className="admin-link">Gestión de Sistema</Link></li>
                                            </>
                                        )}
                                        <li className="divider"></li>
                                        <li><button onClick={handleLogout} className="logout-btn-custom">Cerrar Sesión</button></li>
                                    </>
                                ) : (
                                    // 🔒 Opciones para VISITANTES
                                    <>
                                        <li><Link to="/login">Iniciar Sesión</Link></li>
                                        <li><Link to="/register">Registrarse</Link></li>
                                    </>
                                )}
                            </ul>
                        )}
                    </div>
                </div>
            </header>

            <main className="dashboard-content">
                <div className="hero-section text-center">
                    <h1 style={{marginBottom: '20px', fontWeight: '800'}}>Mantenimiento Inteligente</h1>
                    
                    {/* El banner de IA puede ser público, pero el asistente real pedirá login */}
                    <Link to="/asistente" className="promo-banner">
                        Ver Recomendaciones de la IA
                    </Link>
                </div>

                <h2 style={{margin: '40px 0 20px 0', fontWeight: '700'}}>Nuestros Productos</h2>
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
                        <h3>Aceites y Lubricantes</h3>
                        <Link to="/catalogoAceites" className="category-btn">Ver Catálogo</Link>
                    </div>
                </div>
            </main>

            <footer className="dashboard-footer">
                <div className="btn-tech-support">Soporte Técnico: 300-XXX-XXXX</div>
            </footer>
        </div>
    );
};

export default Dashboard;