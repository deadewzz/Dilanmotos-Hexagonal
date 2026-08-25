import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
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

                // AHORA el backend devuelve directamente el objeto JSON con recomendaciones
                const dataIA = await resIA.json();
                console.log('Respuesta de la IA:', dataIA);

                // Extraer las recomendaciones directamente
                let productosIA = [];
                
                if (dataIA && dataIA.recomendaciones && Array.isArray(dataIA.recomendaciones)) {
                    productosIA = dataIA.recomendaciones;
                } else if (Array.isArray(dataIA)) {
                    productosIA = dataIA;
                }

                if (!productosIA || productosIA.length === 0) {
                    throw new Error("No hay recomendaciones disponibles para esta moto en este momento.");
                }

                // Mapeo de imágenes estáticas de respaldo
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

    if (cargando) return (
        <div className="dashboard-wrapper" style={{display:'flex', justifyContent:'center', alignItems:'center', flexDirection:'column', gap:'20px'}}>
            <div className="spinner" style={{border: '4px solid #f3f3f3', borderTop: '4px solid #4e54c8', borderRadius: '50%', width: '40px', height: '40px', animation: 'spin 1s linear infinite'}}></div>
            <h2 className="text-primary">🤖 La IA está eligiendo los mejores componentes...</h2>
            <style>{`@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }`}</style>
        </div>
    );

    if (error) return (
        <div className="dashboard-wrapper" style={{display:'flex', flexDirection:'column', justifyContent:'center', alignItems:'center', padding:'40px', textAlign:'center'}}>
            <h2 style={{color: '#ff4d4d', marginBottom:'15px'}}>⚠️ Ups, algo salió mal</h2>
            <p className="text-muted" style={{marginBottom:'20px', maxWidth:'500px'}}>{error}</p>
            <button onClick={() => navigate('/dashboard')} className="category-btn" style={{padding:'10px 20px'}}>
                Volver al Inicio
            </button>
        </div>
    );

    return (
        <div className="dashboard-wrapper">
            <header className="dashboard-header">
                <div className="header-container">
                    <button onClick={() => navigate('/dashboard')} style={{background:'none', border:'none', color:'white', cursor:'pointer', fontWeight:'bold', display:'flex', alignItems:'center', gap:'8px'}}>
                        <i className="fa-solid fa-arrow-left"></i> Volver al Inicio
                    </button>
                    <span className="user-trigger">Recomendaciones IA</span>
                </div>
            </header>

            <main className="dashboard-content">
                <div className="hero-section" style={{marginBottom: '30px'}}>
                    <h1 style={{fontWeight: '800'}}>Selección Especial para tu {moto?.modelo || 'Moto'}</h1>
                    <p className="text-muted">Basado en tu cilindraje de {moto?.cilindraje || '---'}cc</p>
                </div>

                <div className="categories-grid">
                    {productos.map((prod, index) => (
                        <div key={index} className="category-item" style={{border: '2px solid #4e54c8', display: 'flex', flexDirection: 'column', justifyContent: 'space-between'}}>
                            <div>
                                <span style={{color: '#4e54c8', fontWeight: 'bold', fontSize: '0.8rem'}}>{prod.tipo}</span>
                                <div className="category-img" style={{marginTop: '20px', height: '120px', display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
                                    <img src={prod.img} alt={prod.nombre} style={{maxHeight: '100%', maxWidth: '100%', objectFit: 'contain'}} />
                                </div>
                                <h3 style={{margin: '15px 0', fontSize: '1.2rem'}}>{prod.nombre}</h3>
                                <p style={{fontSize: '0.85rem', color: '#666', marginBottom: '20px'}}>{prod.razon}</p>
                            </div>
                            
                            <button 
                                onClick={() => navigate('/catalogo')} 
                                className="category-btn" 
                                style={{marginTop: 'auto'}}
                            >
                                Ver Disponibilidad
                            </button>
                        </div>
                    ))}
                </div>

                <div style={{textAlign: 'center', marginTop: '50px'}}>
                    <button onClick={() => navigate('/asistente')} className="promo-banner" style={{background:'#4e54c8', cursor:'pointer', border:'none'}}>
                        ¿Tienes dudas? Habla con el Mecánico IA
                    </button>
                </div>
            </main>
        </div>
    );
};

export default Recomendaciones;