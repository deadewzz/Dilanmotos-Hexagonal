import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Dashboard.css';


const CatalogoKit = () => {
    const navigate = useNavigate();
    const [showDropdown, setShowDropdown] = useState(false);
    const [user, setUser] = useState({ nombre: "Admin", rol: "ADMIN", id: null });

    // Carga de datos de sesión (basado en tu referencia)
    useEffect(() => {
        const id = localStorage.getItem('idUsuario');
        const nombre = localStorage.getItem('nombreUsuario');
        const rol = localStorage.getItem('rolUsuario');
        
        
        if (nombre) {
            setUser({ nombre: nombre, rol: rol || "USER", id });
        }
    }, []);

    const handleLogout = () => {
        localStorage.clear();
        navigate('/login');
    };

    return (
        <div className="dashboard-wrapper">
            {/* Header consistente con la imagen */}
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
                                <li><Link to="/perfil">Mi Cuenta</Link></li>
                                <li><Link to="/asistente">Asistente IA</Link></li>
                                <li><Link to="/historial">Mi Historial</Link></li>
                                <li><Link to="/nueva-pqrs">Radicar PQRS</Link></li>
                                {user.rol === 'ADMIN' && (
                                    <>
                                        <li className="divider"></li>
                                        <li>
                                            <Link to="/usuarios" className="admin-link">
                                                Gestion de Sistema
                                            </Link>
                                        </li>
                                    </>
                                )}
                                
                                <li className="divider"></li>
                                <li>
                                    <button onClick={handleLogout} className="logout-btn-custom">
                                        Cerrar Sesion
                                    </button>
                                </li>
                            </ul>
                        )}
                    </div>
                </div>
            </header>

            {/* Contenido Principal Estilo Imagen */}
            <main className="dashboard-content">
                <div className="hero-section text-center">
                    <h1 className="main-title">Mantenimiento Inteligente</h1>
                    
                    <Link to="/recomendacion" className="promo-banner">
                        Ver Recomendaciones de la IA
                    </Link>
                </div>

                <h2 className="section-subtitle">Nuestros Productos</h2>
                
                <div className="categories-grid">
                    {/* Kit de Arrastre */}
                    <div className="category-item">
                        <div className="category-img">
                            <img src="/KitDeArrastre.png" alt="Kits" />
                        </div>
                        <h3>Nombre del producto</h3>
                        <Link to="/fichaTecnica/5" className="category-btn">Ver ficha técnica</Link>
                    </div>

                    {/* Llantas */}
                    <div className="category-item">
                        <div className="category-img">
                            <img src="/KitDeArrastre.png" alt="Kits" />
                        </div>
                        <h3>Nombre del producto</h3>
                        <Link to="/fichaTecnica/6" className="category-btn">Ver ficha técnica</Link>
                    </div>

                    {/* Aceites */}
                    <div className="category-item">
                        <div className="category-img">
                            <img src="/KitDeArrastre.png" alt="Kits" />
                        </div>
                        <h3>Nombre del producto</h3>
                        <Link to="/fichaTecnica/7" className="category-btn">Ver ficha técnica</Link>
                    </div>
                </div>
            </main>

            <footer className="dashboard-footer">
                <div className="btn-tech-support">
                    Soporte Tecnico: 300-XXX-XXXX
                </div>
            </footer>
        </div>
    );
};

export default CatalogoKit;