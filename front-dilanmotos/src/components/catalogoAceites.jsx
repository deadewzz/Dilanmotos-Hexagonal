import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Dashboard.css';

const CatalogoAceites = () => {
    const navigate = useNavigate();
    const dropdownRef = useRef(null);
    const [showDropdown, setShowDropdown] = useState(false);
    const [user, setUser] = useState({ nombre: "Invitado", rol: "GUEST", id: null });
    const [aceites, setAceites] = useState([]);
    const [loading, setLoading] = useState(true);

    const isAuthenticated = !!localStorage.getItem('token');

    // Cierre del menú desplegable al hacer clic afuera
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setShowDropdown(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    // Manejo de Sesión de Usuario
    useEffect(() => {
        if (isAuthenticated) {
            const idU = localStorage.getItem('idUsuario');
            const nombre = localStorage.getItem('nombreUsuario');
            const rol = localStorage.getItem('rolUsuario');
            setUser({ nombre: nombre || "Socio", rol: rol || "USER", id: idU });
        }
    }, [isAuthenticated]);

    // Obtener y filtrar los productos desde el Backend
    useEffect(() => {
        const fetchAceites = async () => {
            const token = localStorage.getItem('token');
            
            try {
                const response = await fetch('http://localhost:8080/api/productos', { 
                    method: 'GET', 
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    } 
                });

                if (response.ok) {
                    const todosLosProductos = await response.json();
        
                    const soloAceites = todosLosProductos.filter(producto => 
                        producto.nombre && producto.nombre.toLowerCase().includes('aceite')
                    );

                    setAceites(soloAceites);
                } else if (response.status === 401) {
                    localStorage.clear();
                    navigate('/login');
                }
            } catch (error) {
                console.error("Error de conexión:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchAceites();
    }, [navigate]);

    const handleLogout = () => {
        localStorage.clear();
        setUser({ nombre: "Invitado", rol: "GUEST", id: null });
        navigate('/login');
    };

    return (
        <div className="dashboard-wrapper">
            {/* Header / Barra de Navegación */}
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
                    <h1 className="main-title">Mantenimiento Inteligente</h1>
                    <p style={{ color: '#64748b', fontSize: '0.95rem', marginTop: '8px' }}>
                        Lubricación optimizada para el motor y transmisión de tu moto.
                    </p>
                    <div style={{ marginTop: '16px' }}>
                        <Link 
                            to="/asistente" 
                            state={{ consultaInicial: "Hola, necesito saber qué aceite es el recomendado para mi moto" }}
                            className="promo-banner" 
                            style={{ textDecoration: 'none', display: 'inline-block' }}
                        >
                            🤖 CONSULTAR ASISTENTE DE IA
                        </Link>
                    </div>
                </div>

                <h2 className="section-subtitle">Nuestros Aceites</h2>
                
                <div className="categories-grid">
                    {loading ? (
                        <div className="loading" style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '40px' }}>
                            <p style={{ color: '#64748b' }}>Cargando aceites desde la base de datos...</p>
                        </div>
                    ) : aceites.length > 0 ? (
                        aceites.map((aceite) => (
                            <div className="category-item" key={aceite.idProducto || aceite.id}>
                                <div className="category-img" style={{ height: '140px', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                                    <img 
                                        src={aceite.imagenUrl || "/AceiteMotul.png"} 
                                        alt={aceite.nombre} 
                                        style={{ maxHeight: '100%', maxWidth: '100%', objectFit: 'contain' }}
                                        onError={(e) => { e.target.onerror = null; e.target.src="/AceiteMotul.png"; }}
                                    />
                                </div>
                                <h3 style={{ fontSize: '1.1rem', color: '#1e293b', margin: '12px 0', fontWeight: '700' }}>{aceite.nombre}</h3>
                                <p style={{ fontSize: '0.85rem', color: '#64748b', marginBottom: '16px' }}>
                                    Precio: <strong style={{ color: '#2ecc71' }}>${aceite.precio ? aceite.precio.toLocaleString() : 'N/A'} COP</strong>
                                </p>
                                <Link to={`/fichaTecnica/${aceite.idProducto || aceite.id}`} className="category-btn" style={{ width: '100%', textDecoration: 'none', textAlign: 'center' }}>
                                    Ver ficha técnica
                                </Link>
                            </div>
                        ))
                    ) : (
                        <div className="error-message" style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '40px' }}>
                            <p style={{ color: '#64748b' }}>No se encontraron aceites disponibles en este momento.</p>
                        </div>
                    )}
                </div>
            </main>

            {/* Pie de Página */}
            <footer className="dashboard-footer" style={{ textAlign: 'center', padding: '20px', marginTop: '40px' }}>
                <div className="btn-tech-support" style={{ display: 'inline-block' }}>
                    Soporte Técnico: 301-535-6723
                </div>
            </footer>
        </div>
    );
};

export default CatalogoAceites;