import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import './fichaTecnica.css';

const FichaTecnica = () => {
    const navigate = useNavigate();
    const { id } = useParams(); 
    const [showDropdown, setShowDropdown] = useState(false);
    const [user, setUser] = useState({ nombre: "Invitado", rol: "GUEST", id: null });
    
    // Estado para los datos del producto
    const [producto, setProducto] = useState(null);
    const [loading, setLoading] = useState(true);

    const isAuthenticated = !!localStorage.getItem('token');

    useEffect(() => {
        // 1. Manejo de Sesión
        if (isAuthenticated) {
            const idU = localStorage.getItem('idUsuario');
            const nombre = localStorage.getItem('nombreUsuario');
            const rol = localStorage.getItem('rolUsuario');
            setUser({ nombre: nombre || "Socio", rol: rol || "USER", id: idU });
        }

        // 2. Obtener datos del producto desde Spring Boot
        // Ajusta la URL según tu controlador en el Backend
const fetchProducto = async () => {
    try {
        // 1. Recuperamos el token del almacenamiento local
        const token = localStorage.getItem('token'); 
        console.log("El token enviado es:", token);
        const response = await fetch(`http://localhost:8080/api/productos/${id}`, {
            method: 'GET',
            // 2. Añadimos los headers con el token de autorización
            headers: { 
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            setProducto(data);
        } else {
            console.error("Error al obtener el producto. Status:", response.status);
        }
    } catch (error) {
        console.error("Error de conexión:", error);
    } finally {
        setLoading(false);
    }
};

        if (id) fetchProducto();
    }, [isAuthenticated, id]);

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

            {/* --- SECCIÓN DE FICHA TÉCNICA --- */}
            <main className="tech-sheet-container">
                {loading ? (
                    <div className="loading">Cargando detalles del producto...</div>
                ) : producto ? (
                    <div className="product-card">
                        <div className="product-header">
                            <h2>Ficha Técnica: {producto.nombre}</h2>
                            <span className="badge-category">{producto.categoria?.nombre || 'General'}</span>
                        </div>
                        
                        <div className="product-body">
                            <div className="info-section">
                                <h3>Información General</h3>
                                <table className="tech-table">
                                    <tbody>
                                        <tr>
                                            <td><strong>Referencia:</strong></td>
                                            <td>#{producto.id_producto}</td>
                                        </tr>
                                        <tr>
                                            <td><strong>Marca:</strong></td>
                                            <td>{producto.marca?.nombre || 'No especificada'}</td>
                                        </tr>
                                        <tr>
                                            <td><strong>Precio:</strong></td>
                                            <td className="price-tag">${parseFloat(producto.precio).toLocaleString()}</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>

                            <div className="description-section">
                                <h3>Descripción del Producto</h3>
                                <p>{producto.descripcion || "No hay una descripción disponible para este producto."}</p>
                            </div>
                        </div>

                        <div className="product-actions">
                            <button className="btn-primary" onClick={() => navigate(-1)}>Volver</button>
                            <button
                                className="btn-secondary"
                                onClick={() => {
                                    if (!producto) return;

                                    const productId = producto.id_producto ?? producto.id ?? Math.random().toString(36).slice(2, 9)
                                    const normalizedProduct = {
                                        id: productId,
                                        nombre: producto.nombre ?? producto.titulo ?? 'Producto',
                                        precio: Number(producto.precio ?? 0) || 0,
                                        cantidad: 1,
                                    }

                                    const stored = localStorage.getItem('selectedProducts')
                                    const current = stored ? JSON.parse(stored) : []
                                    const existing = current.find((item) => String(item.id) === String(productId))
                                    const updated = existing
                                        ? current.map((item) =>
                                            String(item.id) === String(productId)
                                                ? { ...item, cantidad: Number(item.cantidad ?? 0) + 1 }
                                                : item,
                                        )
                                        : [...current, normalizedProduct]

                                    localStorage.setItem('selectedProducts', JSON.stringify(updated))
                                    navigate('/hacer-cotizacion', { state: { producto: normalizedProduct } })
                                }}
                            >
                                Cotizar Repuesto
                            </button>
                        </div>
                    </div>
                ) : (
                    <div className="error-message">Producto no encontrado.</div>
                )}
            </main>

            <footer className="dashboard-footer">
                <div className="btn-tech-support">Soporte Técnico: 300-XXX-XXXX</div>
            </footer>
        </div>
    );
};

export default FichaTecnica;