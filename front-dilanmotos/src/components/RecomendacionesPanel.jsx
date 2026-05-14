import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';

const Recomendaciones = () => {
    const navigate = useNavigate();
    const [moto, setMoto] = useState(null);
    const [productos, setProductos] = useState([]);
    const [cargando, setCargando] = useState(true);

    useEffect(() => {
        const cargarDatos = async () => {
            const id = localStorage.getItem('idUsuario');
            try {
                // 1. Traemos la moto
                const resMoto = await fetch(`http://localhost:8080/api/motos/usuario/${id}`);
                const dataMoto = await resMoto.json();
                setMoto(dataMoto);

                // 2. Simulamos que la IA elige los nombres exactos para ESA moto
                // En un escenario real, esto vendría de tu endpoint de IA
                setTimeout(() => {
                    const esAlta = dataMoto.cilindraje >= 400;
                    setProductos([
                        { 
                            tipo: "Lubricante Premium", 
                            nombre: esAlta ? "Motul 7100 10W40 Sintético" : "Motul 5100 15W50 Semi", 
                            img: "/AceiteMotul.png",
                            razon: "Ideal para la compresión de tu motor."
                        },
                        { 
                            tipo: "Llantas Recomendadas", 
                            nombre: esAlta ? "Michelin Road 6" : "Michelin Pilot Street", 
                            img: "/Llanta.png",
                            razon: "Máximo agarre en las calles de Bogotá."
                        },
                        { 
                            tipo: "Kit de Arrastre", 
                            nombre: "Kit DID con Cadena Reforzada", 
                            img: "/KitDeArrastre.png",
                            razon: "Diseñado para soportar el torque de tu máquina."
                        }
                    ]);
                    setCargando(false);
                }, 1000);
            } catch (error) {
                console.error(error);
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