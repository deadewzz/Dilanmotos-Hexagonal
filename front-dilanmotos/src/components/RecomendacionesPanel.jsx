import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Dashboard.css';

const Recomendaciones = () => {
    const navigate = useNavigate();
    const [moto, setMoto] = useState(null);
    const [productos, setProductos] = useState([]);
    const [cargando, setCargando] = useState(true);
    const [error, setError] = useState(null);
    const token = localStorage.getItem('token');

    useEffect(() => {
        const cargarDatos = async () => {
            const idUsuarioLocal = localStorage.getItem('idUsuario');
            
            if (!token || !idUsuarioLocal) {
                setError("Sesión inválida. Por favor, inicia sesión nuevamente.");
                setCargando(false);
                return;
            }

            try {
                // 1. Obtener la moto del usuario
                const resMoto = await fetch(`http://localhost:8080/api/motos/usuario/${idUsuarioLocal}`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                if (!resMoto.ok) {
                    throw new Error(`Error al obtener datos de la moto (${resMoto.status})`);
                }

                const dataMoto = await resMoto.json();
                const motoData = Array.isArray(dataMoto) ? dataMoto[0] : dataMoto;

                if (!motoData) {
                    throw new Error("No se encontró ninguna moto asociada a este usuario.");
                }
                
                setMoto(motoData);

                // 2. Obtener las recomendaciones generadas por la IA
                const resIA = await fetch(`http://localhost:8080/api/ia/recomendaciones/${idUsuarioLocal}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (!resIA.ok) {
                    const textoErrorBackend = await resIA.text();
                    throw new Error(`Error del Servidor IA (${resIA.status}): ${textoErrorBackend || 'Petición rechazada'}`);
                }

                const dataIA = await resIA.json();
                console.log('Respuesta de la IA:', dataIA);

                let productosIA = [];
                if (dataIA && dataIA.recomendaciones && Array.isArray(dataIA.recomendaciones)) {
                    productosIA = dataIA.recomendaciones;
                } else if (Array.isArray(dataIA)) {
                    productosIA = dataIA;
                }

                if (!productosIA || productosIA.length === 0) {
                    throw new Error("No hay recomendaciones disponibles para esta moto en este momento.");
                }

                const imgs = ["/AceiteMotul.png", "/Llanta.png", "/KitDeArrastre.png"];
                setProductos(productosIA.map((p, i) => ({
                    tipo: p.tipo || 'Repuesto',
                    nombre: p.nombre || 'Producto recomendado',
                    razon: p.razon || 'Adecuado para el mantenimiento de tu motocicleta.',
                    img: imgs[i % imgs.length]
                })));

            } catch (err) {
                console.error("Error en el flujo de recomendaciones:", err);
                setError(err.message);
            } finally {
                setCargando(false);
            }
        };

        cargarDatos();
    }, [token]);

    // PANTALLA DE CARGA
    if (cargando) return (
        <div className="dashboard-wrapper" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', flexDirection: 'column', gap: '20px', minHeight: '100vh' }}>
            <div className="spinner" style={{ border: '4px solid #e2e8f0', borderTop: '4px solid #4e54c8', borderRadius: '50%', width: '48px', height: '48px', animation: 'spin 0.8s linear infinite' }}></div>
            <h2 style={{ color: '#4e54c8', fontWeight: '700', fontSize: '1.25rem' }}>🤖 La IA está analizando los mejores componentes...</h2>
            <style>{`@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }`}</style>
        </div>
    );

    // PANTALLA DE ERROR
    if (error) return (
        <div className="dashboard-wrapper" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', padding: '40px', textAlign: 'center', minHeight: '100vh' }}>
            <div style={{ background: '#fff', padding: '40px', borderRadius: '24px', boxShadow: '0 10px 30px rgba(0,0,0,0.08)', maxWidth: '480px', width: '100%' }}>
                <i className="fa-solid fa-triangle-exclamation" style={{ fontSize: '3rem', color: '#e74c3c', marginBottom: '15px' }}></i>
                <h2 style={{ color: '#2c3e50', marginBottom: '12px', fontWeight: '700' }}>Ups, algo salió mal</h2>
                <p style={{ color: '#6c757d', marginBottom: '24px', fontSize: '0.95rem', lineHeight: '1.5' }}>{error}</p>
                <button onClick={() => navigate('/dashboard')} className="category-btn" style={{ width: '100%', padding: '12px 20px', borderRadius: '10px' }}>
                    Volver al Inicio
                </button>
            </div>
        </div>
    );

    return (
        <div className="dashboard-wrapper">
            {/* HEADER PRINCIPAL UNIFICADO */}
            <header className="dashboard-header">
                <div className="header-container">
                    <div className="brand-logo-container" onClick={() => navigate('/dashboard')}>
                        <img src="/LogoDilanMotos.png" alt="Logo Dilan Motos" className="main-logo" />
                        <span className="brand-name">DilanMotos</span>
                    </div>

                    <div className="header-nav">
                        <button 
                            onClick={() => navigate('/dashboard')} 
                            className="btn-download-apk" 
                            style={{ background: 'rgba(255, 255, 255, 0.15)', boxShadow: 'none' }}
                        >
                            <i className="fa-solid fa-arrow-left"></i>
                            <span>Volver al Inicio</span>
                        </button>
                    </div>
                </div>
            </header>

            {/* CONTENIDO PRINCIPAL */}
            <main className="dashboard-content">
                <div className="hero-section" style={{ marginBottom: '40px' }}>
                    <span style={{ 
                        background: 'rgba(78, 84, 200, 0.1)', 
                        color: '#4e54c8', 
                        padding: '6px 16px', 
                        borderRadius: '20px', 
                        fontWeight: '700', 
                        fontSize: '0.85rem',
                        textTransform: 'uppercase',
                        letterSpacing: '0.5px'
                    }}>
                        Recomendaciones Personalizadas
                    </span>
                    <h1 style={{ fontWeight: '800', marginTop: '12px', color: '#1e293b' }}>
                        Selección Especial para tu {moto?.modelo || 'Moto'}
                    </h1>
                    <p style={{ color: '#64748b', fontSize: '1rem', marginTop: '4px' }}>
                        Configuración optimizada para cilindraje de <strong style={{ color: '#4e54c8' }}>{moto?.cilindraje || '---'} cc</strong>
                    </p>
                </div>

                {/* TARJETAS DE PRODUCTOS */}
                <div className="categories-grid">
                    {productos.map((prod, index) => (
                        <div 
                            key={index} 
                            className="category-item" 
                            style={{ 
                                display: 'flex', 
                                flexDirection: 'column', 
                                justifyContent: 'space-between',
                                border: '1px solid rgba(78, 84, 200, 0.2)',
                                position: 'relative',
                                overflow: 'hidden'
                            }}
                        >
                            <div style={{ textAlign: 'center' }}>
                                <span style={{ 
                                    display: 'inline-block',
                                    background: '#f1f5f9', 
                                    color: '#4e54c8', 
                                    fontWeight: '700', 
                                    fontSize: '0.78rem',
                                    padding: '4px 12px',
                                    borderRadius: '12px',
                                    textTransform: 'uppercase'
                                }}>
                                    {prod.tipo}
                                </span>

                                <div className="category-img" style={{ marginTop: '20px', height: '140px', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                                    <img src={prod.img} alt={prod.nombre} style={{ maxHeight: '100%', maxWidth: '100%', objectFit: 'contain' }} />
                                </div>

                                <h3 style={{ margin: '16px 0 8px 0', fontSize: '1.15rem', color: '#1e293b', fontWeight: '700' }}>
                                    {prod.nombre}
                                </h3>

                                <p style={{ fontSize: '0.88rem', color: '#64748b', marginBottom: '24px', lineHeight: '1.4' }}>
                                    {prod.razon}
                                </p>
                            </div>
                            
                            <button 
                                onClick={() => navigate('/catalogo')} 
                                className="category-btn" 
                                style={{ width: '100%', borderRadius: '10px' }}
                            >
                                Ver Disponibilidad
                            </button>
                        </div>
                    ))}
                </div>

                {/* BOTÓN ASISTENTE IA */}
                <div style={{ textAlign: 'center', marginTop: '50px' }}>
                    <button 
                        onClick={() => navigate('/asistente')} 
                        className="promo-banner" 
                        style={{ cursor: 'pointer', border: 'none', display: 'inline-flex', alignItems: 'center', gap: '10px' }}
                    >
                        <i className="fa-solid fa-comments"></i>
                        ¿Tienes dudas? Habla con el Mecánico IA
                    </button>
                </div>
            </main>
        </div>
    );
};

export default Recomendaciones;