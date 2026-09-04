import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Dashboard.css';
import { API_BASE_URL } from '../api';

const CatalogoKit = () => {
    const navigate = useNavigate();
    const dropdownRef = useRef(null);
    const [showDropdown, setShowDropdown] = useState(false);
    const [user, setUser] = useState({ nombre: "Invitado", rol: "GUEST", id: null });
    const [kits, setKits] = useState([]);
    const [loading, setLoading] = useState(true);

    const isAuthenticated = !!localStorage.getItem('token');

    // 1. Cierre del menú desplegable al hacer clic afuera
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setShowDropdown(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    // 2. Carga de Sesión
    useEffect(() => {
        if (isAuthenticated) {
            const idU = localStorage.getItem('idUsuario');
            const nombre = localStorage.getItem('nombreUsuario');
            const rol = localStorage.getItem('rolUsuario');
            setUser({ nombre: nombre || "Socio", rol: rol || "USER", id: idU });
        }
    }, [isAuthenticated]);

    // 3. Consulta de Productos (Kits de Arrastre)
    useEffect(() => {
        const fetchKits = async () => {
            const token = localStorage.getItem('token');
            
            try {
                const response = await fetch(`${API_BASE_URL}/api/productos`, { 
                    method: 'GET', 
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    } 
                });

                if (response.ok) {
                    const todosLosProductos = await response.json();
                    
                    const soloKits = todosLosProductos.filter(producto => 
                        producto.nombre && producto.nombre.toLowerCase().includes('kit')
                    );

                    setKits(soloKits); 
                } else if (response.status === 401) {
                    localStorage.clear();
                    navigate('/login');
                }
            } catch (error) {
                console.error("Error al cargar kits:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchKits();
    }, [navigate]);

    const handleLogout = () => {
        localStorage.clear();
        setUser({ nombre: "Invitado", rol: "GUEST", id: null });
        navigate('/login');
    };

    return (
        <div className="dashboard-wrapper">
            {/* Header / Barra Superior */}
            <header className="dashboard-header">
                <div className="header-container">
                    <div className="brand-logo-container" onClick={() => navigate('/dashboard')} style={{ cursor: 'pointer' }}>
                        <img 
                            src="/LogoDilanMotos.png" 
                            alt="Dilan Motos" 
                            className="main-logo" 
                        />
                        <span className="brand-name">DilanMotos</span>
                    </div>
                    
                    <div className="header-nav" ref={dropdownRef}>
                        <div className="user-trigger" onClick={() => setShowDropdown(!showDropdown)}>
                            <img src="/iconoPerfil.png" alt="Perfil" className="nav-icon avatar" />
                            <span>{user.nombre}</span>
                        </div>

                        {showDropdown && (
                            <ul className="dropdown-menu-custom shadow-lg">
                                {isAuthenticated ? (
                                    <>
                                        <li><Link to="/perfil" onClick={() => setShowDropdown(false)}>Mi Cuenta</Link></li>
                                        <li><Link to="/asistente" onClick={() => setShowDropdown(false)}>Asistente IA</Link></li>
                                        <li><Link to="/historial" onClick={() => setShowDropdown(false)}>Mi Historial</Link></li>
                                        <li><Link to="/nueva-pqrs" onClick={() => setShowDropdown(false)}>Radicar PQRS</Link></li>
                                        <li><Link to="/hacer-cotizacion" onClick={() => setShowDropdown(false)}>Hacer Cotización</Link></li>
                                        {user.rol === 'ADMIN' && (
                                            <>
                                                <li className="divider"></li>
                                                <li><Link to="/usuarios" className="admin-link" onClick={() => setShowDropdown(false)}>Gestión de Sistema</Link></li>
                                            </>
                                        )}
                                        <li className="divider"></li>
                                        <li><button onClick={handleLogout} className="logout-btn-custom">Cerrar Sesión</button></li>
                                    </>
                                ) : (
                                    <>
                                        <li><Link to="/login" onClick={() => setShowDropdown(false)}>Iniciar Sesión</Link></li>
                                        <li><Link to="/register" onClick={() => setShowDropdown(false)}>Registrarse</Link></li>
                                    </>
                                )}
                            </ul>
                        )}
                    </div>
                </div>
            </header>

            {/* Contenido Principal */}
            <main className="dashboard-content">
                <div className="hero-section text-center" style={{ marginBottom: '30px' }}>
                    <h1 className="main-title">Kits de Arrastre Recomendados</h1>
                    <p style={{ color: '#64748b', fontSize: '0.95rem', marginTop: '8px' }}>
                        Sistemas de transmisión optimizados para el rendimiento de tu motocicleta.
                    </p>
                    <div style={{ marginTop: '16px' }}>
                        {/* REDIRECCIÓN CORREGIDA A /asistente */}
                        <Link 
                            to="/asistente" 
                            state={{ consultaInicial: "Hola, necesito asesoría sobre kits de arrastre para mi moto" }}
                            className="promo-banner" 
                            style={{ textDecoration: 'none', display: 'inline-block' }}
                        >
                            🤖 CONSULTAR ASISTENTE DE IA
                        </Link>
                    </div>
                </div>

                <div className="categories-grid">
                    {loading ? (
                        <div className="loading" style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '40px' }}>
                            <div className="spinner" style={{ border: '3px solid #e2e8f0', borderTop: '3px solid #4e54c8', borderRadius: '50%', width: '36px', height: '36px', animation: 'spin 0.8s linear infinite', margin: '0 auto 12px' }}></div>
                            <p style={{ color: '#64748b' }}>Cargando kits disponibles...</p>
                            <style>{`@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }`}</style>
                        </div>
                    ) : kits.length > 0 ? (
                        kits.map((kit) => (
                            <div className="category-item" key={kit.idProducto || kit.id}>
                                <div className="category-img" style={{ height: '140px', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                                    <img 
                                        src={kit.imagenUrl || "/KTTakasago14-45.png"} 
                                        alt={kit.nombre} 
                                        style={{ maxHeight: '100%', maxWidth: '100%', objectFit: 'contain' }}
                                        onError={(e) => { e.target.onerror = null; e.target.src="/KTTakasago14-45.png"; }}
                                    />
                                </div>
                                <h3 style={{ fontSize: '1.1rem', color: '#1e293b', margin: '12px 0', fontWeight: '700' }}>{kit.nombre}</h3>
                                <p style={{ fontSize: '0.85rem', color: '#64748b', marginBottom: '16px' }}>
                                    Precio: <strong style={{ color: '#2ecc71' }}>${kit.precio ? kit.precio.toLocaleString() : 'N/A'} COP</strong>
                                </p>
                                <Link to={`/fichaTecnica/${kit.idProducto || kit.id}`} className="category-btn" style={{ width: '100%', textDecoration: 'none', textAlign: 'center' }}>
                                    Ver ficha técnica
                                </Link>
                            </div>
                        ))
                    ) : (
                        <div className="error-message" style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '40px', background: '#fff', borderRadius: '16px', boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}>
                            <p style={{ color: '#64748b', fontSize: '1rem' }}>No se encontraron kits de arrastre disponibles en este momento.</p>
                        </div>
                    )}
                </div>
            </main>

            {/* Pie de Página */}
            <footer className="dashboard-footer" style={{ textAlign: 'center', padding: '20px', marginTop: '40px' }}>
                <div className="btn-tech-support" style={{ display: 'inline-block' }}>
                    Soporte Técnico: 301-353-6723
                </div>
            </footer>
        </div>
    );
};

export default CatalogoKit;