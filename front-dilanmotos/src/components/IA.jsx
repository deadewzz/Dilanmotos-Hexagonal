import React, { useState, useRef, useEffect } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { useLocation } from 'react-router-dom'; 
import './AsistenteMotos.css';


const AsistenteMotos = () => {
    const location = useLocation(); 
    const [pregunta, setPregunta] = useState('');
    const [cargando, setCargando] = useState(false);
    const [modeloSeleccionado, setModeloSeleccionado] = useState('Buscando máquina...');
    const [mensajes, setMensajes] = useState([
        { rol: 'ia', texto: '¡Habla pues **parcero**! Bienvenido a **Dilan Motos**. ¿Qué máquina vamos a revisar hoy?' }
    ]);

    // Recuperamos credenciales del localStorage
    const idLogueado = localStorage.getItem("idUsuario");
    const token = localStorage.getItem("token"); //  Crucial para evitar el error 401
    const mensajesFinRef = useRef(null);

    // 🏍️ Efecto para cargar la moto al iniciar
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
                    // Ajustado para manejar si el backend devuelve una lista o un objeto único
                    const motoData = Array.isArray(data) ? data[0] : data;

                    if (motoData && motoData.modelo) {
                        const nombreMoto = motoData.modelo.toUpperCase();
                        setModeloSeleccionado(nombreMoto);
                        
                        
                        if (location.state?.autoPrompt) {
                            dispararRecomendacionInicial(nombreMoto);
                        }
                    }
                } else if (res.status === 401) {
                    console.error("Sesión expirada");
                }
            } catch (error) { 
                console.error("Error cargando moto:", error); 
            }
        };
        cargarMoto();
    }, [idLogueado, token, location.state]);

    // Función para la recomendación automática
    const dispararRecomendacionInicial = (moto) => {
        const promoMsg = `✨ ¡Dame recomendaciones para mi ${moto}!`;
        ejecutarConsulta(promoMsg, moto);
    };

    const ejecutarConsulta = async (texto, motoActual) => {
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
                    motor: motoActual || modeloSeleccionado,
                    falla: texto,
                    historial: mensajes 
                })
            });

            if (res.ok) {
                const data = await res.json();
                // Usamos 'recomendacion' o 'respuesta' según lo que envíe tu backend
                setMensajes(prev => [...prev, { rol: 'ia', texto: data.recomendacion || data.respuesta }]);
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
        <div className="chat-container">
            <div className="chat-header">
                <div className="avatar">DM</div>
                <div className="header-info">
                    <h2>Mecánico Virtual</h2>
                    <p><span className="status-dot"></span> Online - Dilan Motos</p>
                </div>
            </div>

            <div className="vehicle-selector">
                <span>🏍️ Moto detectada:</span>
                <strong style={{marginLeft: '10px', color: '#e74c3c'}}>{modeloSeleccionado}</strong>
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
    );
};

export default AsistenteMotos;