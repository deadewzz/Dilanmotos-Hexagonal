import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Dashboard.css';

const CatalogoLlantas = () => {
    const navigate = useNavigate();
    const [showDropdown, setShowDropdown] = useState(false);
    const [user, setUser] = useState({ nombre: "Invitado", rol: "GUEST", id: null });
    const [llantas, setLlantas] = useState([]); // Estado renombrado para coherencia
    const [loading, setLoading] = useState(true);

    const isAuthenticated = !!localStorage.getItem('token');

    // 1. Manejo de Sesión de Usuario
    useEffect(() => {
        if (isAuthenticated) {
            const idU = localStorage.getItem('idUsuario');
            const nombre = localStorage.getItem('nombreUsuario');
            const rol = localStorage.getItem('rolUsuario');
            setUser({ nombre: nombre || "Socio", rol: rol || "USER", id: idU });
        }
    }, [isAuthenticated]);

    // 2. Obtener y filtrar los productos desde el Backend
    useEffect(() => {
        const fetchLlantas = async () => {
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
        
                    // Filtramos asegurándonos de que solo entren las Llantas
                    const soloLlantas = todosLosProductos.filter(producto => 
                        producto.nombre && producto.nombre.toLowerCase().includes('llanta')
                    );

                    setLlantas(soloLlantas);
                }
            } catch (error) {
                console.error("Error de conexión:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchLlantas();
    }, []);

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
                    <img 
                        src="/LogoDilanMotos.png" 
                        alt="Dilan Motos" 
                        className="main-logo" 
                        style={{ cursor: 'pointer' }} 
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
                                    <>
                                        <li><Link to="/perfil">Mi Cuenta</Link></li>
                                        <li><Link to="/asistente">Asistente IA</Link></li>
                                        <li><Link to="/historial">Mi Historial</Link></li>
                                        <li><Link to="/nueva-pqrs">Radicar PQRS</Link></li>
                                        <li><Link to="/hacer-cotizacion">Hacer Cotización</Link></li>
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

            {/* Contenido Principal */}
            <main className="dashboard-content">
                <div className="hero-section text-center">
                    <h1 className="main-title">Mantenimiento Inteligente</h1>
                    <Link to="/recomendacion" className="promo-banner">
                        Ver Recomendaciones de la IA
                    </Link>
                </div>

                <h2 className="section-subtitle">Nuestras Llantas</h2>
                
                <div className="categories-grid">
                    {loading ? (
                        <div className="loading" style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '20px' }}>
                            Cargando llantas desde la base de datos...
                        </div>
                    ) : llantas.length > 0 ? (
                        llantas.map((llanta) => (
                            <div className="category-item" key={llanta.idProducto}>
                                <div className="category-img">
                                    <img src={llanta.imagenUrl || "/LlantasMichelin.png"} alt={llanta.nombre} />
                                </div>
                                <h3>{llanta.nombre}</h3>
                                <Link to={`/fichaTecnica/${llanta.idProducto}`} className="category-btn">
                                    Ver ficha técnica
                                </Link>
                            </div>
                        ))
                    ) : (
                        <div className="error-message" style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '20px' }}>
                            No se encontraron llantas disponibles en este momento.
                        </div>
                    )}
                </div>
            </main>

            {/* Pie de Página */}
            <footer className="dashboard-footer">
                <div className="btn-tech-support">
                    Soporte Técnico: 300-XXX-XXXX
                </div>
            </footer>
        </div>
    );
};

export default CatalogoLlantas;