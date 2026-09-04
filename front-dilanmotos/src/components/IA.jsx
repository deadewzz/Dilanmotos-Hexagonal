import React, { useState, useRef, useEffect } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { useLocation, useNavigate } from 'react-router-dom'; 
import './AsistenteMotos.css';
import './Dashboard.css';

const AsistenteMotos = () => {
    const navigate = useNavigate();
    const location = useLocation(); 
    const [pregunta, setPregunta] = useState('');
    const [cargando, setCargando] = useState(false);
    const [modeloSeleccionado, setModeloSeleccionado] = useState('Buscando máquina...');
    const [mensajes, setMensajes] = useState([
        { rol: 'ia', texto: '¡Habla pues **parcero**! Bienvenido a **Dilan Motos**. ¿Qué máquina vamos a revisar hoy?' }
    ]);

    const idLogueado = localStorage.getItem("idUsuario");
    const token = localStorage.getItem("token");
    const mensajesFinRef = useRef(null);

    useEffect(() => {
        const cargarMoto = async () => {
            if (!idLogueado) return;
            try {
                const res = await fetch(`http://localhost:8080/api/motos/usuario/${idLogueado}`, {
                    headers: { 
                        'Authorization': `Bearer ${token}` 
                    }
                });
                
                if (res.ok) {
                    const data = await res.json();
                    const motoData = Array.isArray(data) ? data[0] : data;

                    if (motoData && motoData.modelo) {
                        const nombreMoto = motoData.modelo.toUpperCase();
                        setModeloSeleccionado(nombreMoto);
                        
                        if (location.state?.autoPrompt) {
                            dispararRecomendacionInicial(nombreMoto);
                        }
                    } else {
                        setModeloSeleccionado('Sin moto registrada');
                    }
                } else if (res.status === 401) {
                    console.error("Sesión expirada");
                }
            } catch (error) { 
                console.error("Error cargando moto:", error);
                setModeloSeleccionado('No disponible');
            }
        };
        cargarMoto();
    }, [idLogueado, token, location.state]);

    const dispararRecomendacionInicial = (moto) => {
        const promoMsg = `✨ ¡Dame recomendaciones para mi ${moto}!`;
        ejecutarConsulta(promoMsg);
    };

    const ejecutarConsulta = async (texto) => {
        if (cargando || !texto.trim()) return;

        const nuevoMensaje = { rol: 'usuario', texto };
        setMensajes(prev => [...prev, nuevoMensaje]);
        setCargando(true);

        try {
            const res = await fetch('http://localhost:8080/api/ia/consultar', {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}` 
                },
                body: JSON.stringify({ 
                    idUsuario: parseInt(idLogueado),
                    falla: texto
                })
            });

            if (res.ok) {
                const data = await res.json();
                setMensajes(prev => [...prev, { rol: 'ia', texto: data.content }]);
            } else if (res.status === 401) {
                setMensajes(prev => [...prev, { rol: 'ia', texto: "🚨 Tu sesión ha expirado. Por favor, ingresa de nuevo." }]);
            } else {
                throw new Error("Error en servidor");
            }
        } catch (e) {
            setMensajes(prev => [...prev, { rol: 'ia', texto: "🚨 Error, parcero. No pude conectar con el servidor." }]);
        } finally { 
            setCargando(false); 
        }
    };

    const consultarIA = (e) => {
        e.preventDefault();
        ejecutarConsulta(pregunta);
        setPregunta('');
    };

    useEffect(() => { 
        mensajesFinRef.current?.scrollIntoView({ behavior: "smooth" }); 
    }, [mensajes]);

    return (
        <div className="dashboard-wrapper">
            {/* HEADER UNIFICADO */}
            <header className="dashboard-header">
                <div className="header-container">
                    <div className="brand-logo-container" onClick={() => navigate('/dashboard')} style={{ cursor: 'pointer' }}>
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

            {/* CONTENEDOR DEL CHAT */}
            <main className="dashboard-content" style={{ maxWidth: '900px', margin: '0 auto', width: '100%' }}>
                <div className="chat-container">
                    <div className="chat-header">
                        <div className="avatar">CA</div>
                        <div className="header-info">
                            <h2>Chanda AI</h2>
                            <p><span className="status-dot"></span> Online - Dilan Motos</p>
                        </div>
                    </div>

                    <div className="vehicle-selector">
                        <span>🏍️ Moto detectada:</span>
                        <strong style={{ marginLeft: '10px', color: '#e74c3c' }}>{modeloSeleccionado}</strong>
                    </div>

                    <div className="chat-messages">
                        {mensajes.map((msg, index) => (
                            <div key={index} className={`message-row ${msg.rol === 'usuario' ? 'user' : 'ia'}`}>
                                <div className={`bubble ${msg.rol === 'usuario' ? 'user' : 'ia'}`}>
                                    <ReactMarkdown remarkPlugins={[remarkGfm]}>{msg.texto}</ReactMarkdown>
                                </div>
                            </div>
                        ))}
                        {cargando && (
                            <div className="message-row ia">
                                <div className="bubble ia pulse">Analizando los fierros...</div>
                            </div>
                        )}
                        <div ref={mensajesFinRef} />
                    </div>

                    <div className="chat-input-area">
                        <form onSubmit={consultarIA}>
                            <input 
                                type="text" 
                                value={pregunta} 
                                onChange={e => setPregunta(e.target.value)} 
                                placeholder="Ej: ¿Qué aceite me recomiendas?" 
                                disabled={cargando}
                            />
                            <button type="submit" disabled={cargando || !pregunta.trim()}>Enviar</button>
                        </form>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default AsistenteMotos;