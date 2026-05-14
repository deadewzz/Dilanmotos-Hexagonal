import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './historial.css';


const Historial = () => {
    const navigate = useNavigate();
    const [tab, setTab] = useState('pqrs');
    const [datos, setDatos] = useState([]);
    const [cargando, setCargando] = useState(true);
    const idLogueado = localStorage.getItem('idUsuario');

    const token = localStorage.getItem('token'); // Importante para autenticación
    
    useEffect(() => {
        const cargarDatos = async () => {
            if (!idLogueado || !token) return; //validamos token
            setCargando(true);
            const endpoint = tab === 'pqrs' ? 'pqrs' : 'servicios';
            try {
                const res = await fetch(`http://localhost:8080/api/${endpoint}/usuario/${idLogueado}`, {
                    headers: { 
                        'Authorization': `Bearer ${token}` 

                    }
                });
                if (res.ok) {
                    const data = await res.json();
                    setDatos(Array.isArray(data) ? data : []);
                }
            } catch (error) {
                console.error("Error al cargar historial:", error);
                setDatos([]);
            } finally {
                setCargando(false);
            }
        };
        cargarDatos();
    }, [tab, idLogueado]);

    const rowStyle = {
        display: 'flex',
        height: 'auto',
        minHeight: '65px',
        padding: '15px 0',
        alignItems: 'center',
        borderBottom: '1px solid #eee'
    };

    return (
        <div className="dashboard-wrapper">
            <header className="dashboard-header">
                <div className="header-container">
                    <button onClick={() => navigate('/dashboard')} className="btn-back-clean">
                        <i className="fa-solid fa-arrow-left"></i>
                    </button>
                    <h3 className="text-white m-0">Mi Historial de Actividad</h3>
                    <div style={{ width: '40px' }}></div>
                </div>
            </header>

            <main className="dashboard-content">
                <div className="tab-navigation shadow-sm">
                    <button 
                        className={`tab-item ${tab === 'pqrs' ? 'active' : ''}`}
                        onClick={() => setTab('pqrs')}
                    >
                        Mis PQRS
                    </button>
                    <button 
                        className={`tab-item ${tab === 'servicios' ? 'active' : ''}`}
                        onClick={() => setTab('servicios')}
                    >
                        Servicios Realizados
                    </button>
                </div>

                <div className="card-panel">
                    {cargando ? (
                        <div className="text-center p-5">Sincronizando con el taller...</div>
                    ) : (
                        <div className="custom-table-container">
                            
                            {/* --- SECCIÓN PQRS --- */}
                            {tab === 'pqrs' && (
                                <div key="tabla-pqrs"> {/* Key para evitar error de Fragment */}
                                    <div className="custom-table-header">
                                        <div style={{ flex: 1 }}>Fecha</div>
                                        <div style={{ flex: 1.5 }}>Asunto</div>
                                        <div style={{ flex: 1 }}>Estado</div>
                                        <div style={{ flex: 2 }}>Respuesta Taller</div>
                                    </div>
                                    {datos.length > 0 ? datos.map((p) => (
                                        <div className="custom-table-row" key={`pqrs-${p.idPqrs}`} style={rowStyle}>
                                            <div style={{ flex: 1, padding: '0 10px' }}>{p.fecha?.substring(0, 10)}</div>
                                            <div style={{ flex: 1.5, padding: '0 10px', fontWeight: 'bold' }}>{p.asunto}</div>
                                            <div style={{ flex: 1, padding: '0 10px' }}>
                                                <span className={`status-pill ${p.estado?.toLowerCase().replace(' ', '-')}`}>{p.estado}</span>
                                            </div>
                                            <div style={{ flex: 2, padding: '0 10px', whiteSpace: 'normal', wordWrap: 'break-word', fontStyle: 'italic' }}>
                                                {p.respuesta_admin || "Sin respuesta aun"}
                                            </div>
                                        </div>
                                    )) : <div className="p-5 text-center text-muted">No se encontraron registros de PQRS.</div>}
                                </div>
                            )}

                            {/* --- SECCIÓN SERVICIOS --- */}
                            {tab === 'servicios' && (
                                <div key="tabla-servicios"> {/* Key para evitar error de Fragment */}
                                    <div className="custom-table-header">
                                        <div style={{ flex: 1 }}>Fecha</div>
                                        <div style={{ flex: 1.5 }}>Servicio</div>
                                        <div style={{ flex: 1 }}>Mecanico</div>
                                        <div style={{ flex: 2 }}>Comentarios Tecnicos</div>
                                    </div>
                                    {datos.length > 0 ? datos.map((s) => (
                                        <div className="custom-table-row" key={`serv-${s.idServicio}`} style={rowStyle}>
                                            <div style={{ flex: 1, padding: '0 10px' }}>{s.fechaServicio}</div>
                                            <div style={{ flex: 1.5, padding: '0 10px', fontWeight: 'bold' }}>
                                                {s.tipoServicio?.nombreServicio || "Servicio General"}
                                            </div>
                                            <div style={{ flex: 1, padding: '0 10px' }}>
                                                {s.mecanico?.nombreMecanico || "Por asignar"}
                                            </div>
                                            <div style={{ flex: 2, padding: '0 10px', whiteSpace: 'normal', wordWrap: 'break-word' }}>
                                                {s.comentario || "Sin observaciones."}
                                                <div style={{ fontSize: '0.8rem', color: '#4e54c8', marginTop: '5px', fontWeight: 'bold' }}>
                                                    Puntuacion: {s.puntuacion ? '★'.repeat(s.puntuacion) : 'N/A'}
                                                </div>
                                            </div>
                                        </div>
                                    )) : <div className="p-5 text-center text-muted">No se encontraron servicios realizados.</div>}
                                </div>
                            )}
                        </div>
                    )}
                </div>
            </main>
        </div>
    );
};

export default Historial;