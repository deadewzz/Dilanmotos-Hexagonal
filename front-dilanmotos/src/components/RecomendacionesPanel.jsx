import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';

const Recomendaciones = () => {
    const navigate = useNavigate();
    const [moto, setMoto] = useState(null);
    const [productos, setProductos] = useState([]);
    const [cargando, setCargando] = useState(true);
    const token = localStorage.getItem('token');

    useEffect(() => {
        const cargarDatos = async () => {
            const id = localStorage.getItem('idUsuario');
            try {
                // 1. Traemos la moto
                const resMoto = await fetch(`http://localhost:8080/api/motos/usuario/${id}`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                const dataMoto = await resMoto.json();
                const motoData = Array.isArray(dataMoto) ? dataMoto[0] : dataMoto;
                setMoto(motoData);

                // 2. Llamamos a la IA real con los datos de la moto
                const resIA = await fetch('http://localhost:8080/api/ia/consultar', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        motor: motoData.modelo,
                        falla: `Dame exactamente 3 recomendaciones de productos para mi moto ${motoData.modelo} de ${motoData.cilindraje}cc. 
                                Responde SOLO en formato JSON con esta estructura, sin texto adicional:
                                [
                                  {"tipo": "Lubricante", "nombre": "nombre del producto", "razon": "por qué lo recomiendas"},
                                  {"tipo": "Llantas", "nombre": "nombre del producto", "razon": "por qué lo recomiendas"},
                                  {"tipo": "Kit de Arrastre", "nombre": "nombre del producto", "razon": "por qué lo recomiendas"}
                                ]`
                    })
                });

                const dataIA = await resIA.json();

                // 3. Parseamos el JSON que devuelve la IA
                const textoIA = dataIA.content;
                const jsonMatch = textoIA.match(/\[[\s\S]*\]/);
                if (jsonMatch) {
                    const productosIA = JSON.parse(jsonMatch[0]);
                    const imgs = ["/AceiteMotul.png", "/Llanta.png", "/KitDeArrastre.png"];
                    setProductos(productosIA.map((p, i) => ({ ...p, img: imgs[i] || "" })));
                }

            } catch (error) {
                console.error(error);
            } finally {
                setCargando(false);
            }
        };
        cargarDatos();
    }, []);

    if (cargando) return (
        <div className="dashboard-wrapper" style={{display:'flex', justifyContent:'center', alignItems:'center'}}>
            <h2 className="text-primary">🤖 La IA está eligiendo los mejores componentes...</h2>
        </div>
    );

    return (
        <div className="dashboard-wrapper">
            <header className="dashboard-header">
                <div className="header-container">
                    <button onClick={() => navigate('/dashboard')} style={{background:'none', border:'none', color:'white', cursor:'pointer', fontWeight:'bold'}}>
                        <i className="fa-solid fa-arrow-left"></i> Volver al Inicio
                    </button>
                    <span className="user-trigger">Recomendaciones IA</span>
                </div>
            </header>

            <main className="dashboard-content">
                <div className="hero-section" style={{marginBottom: '30px'}}>
                    <h1 style={{fontWeight: '800'}}>Selección Especial para tu {moto?.modelo}</h1>
                    <p className="text-muted">Basado en tu cilindraje de {moto?.cilindraje}cc</p>
                </div>

                <div className="categories-grid">
                    {productos.map((prod, index) => (
                        <div key={index} className="category-item" style={{border: '2px solid #4e54c8'}}>
                            <span style={{color: '#4e54c8', fontWeight: 'bold', fontSize: '0.8rem'}}>{prod.tipo}</span>
                            <div className="category-img" style={{marginTop: '20px'}}>
                                <img src={prod.img} alt={prod.nombre} />
                            </div>
                            <h3 style={{margin: '15px 0', fontSize: '1.2rem'}}>{prod.nombre}</h3>
                            <p style={{fontSize: '0.85rem', color: '#666', marginBottom: '20px'}}>{prod.razon}</p>
                            <button className="category-btn">Ver Disponibilidad</button>
                        </div>
                    ))}
                </div>

                <div style={{textAlign: 'center', marginTop: '50px'}}>
                    <button onClick={() => navigate('/asistente')} className="promo-banner" style={{background:'none', cursor:'pointer'}}>
                        ¿Tienes dudas? Habla con el Mecánico IA
                    </button>
                </div>
            </main>
        </div>
    );
};

export default Recomendaciones;