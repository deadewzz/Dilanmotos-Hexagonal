import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './historial.css';

const Historial = () => {
    const navigate = useNavigate();
    const [tab, setTab] = useState('pqrs');
    const [datos, setDatos] = useState([]);
    const [cargando, setCargando] = useState(true);
    const idLogueado = localStorage.getItem('idUsuario');
    const token = localStorage.getItem('token');

    useEffect(() => {
        const cargarDatos = async () => {
            if (!idLogueado || !token) return;
            setCargando(true);
            const endpoint = tab === 'pqrs' ? 'pqrs'
                           : tab === 'servicios' ? 'servicios'
                           : 'cotizaciones';
            try {
                const res = await fetch(`http://localhost:8080/api/${endpoint}/usuario/${idLogueado}`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                if (res.ok) {
                    const data = await res.json();
                    setDatos(Array.isArray(data) ? data : []);
                } else {
                    setDatos([]);
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
                    <h3 className="text-white m-0" style={{ color: '#ffffff' }}>Mi Historial de Actividad</h3>
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
                    <button
                        className={`tab-item ${tab === 'cotizaciones' ? 'active' : ''}`}
                        onClick={() => setTab('cotizaciones')}
                    >
                        Cotizaciones Solicitadas
                    </button>
                </div>

                <div className="card-panel">
                    {cargando ? (
                        <div className="text-center p-5">Sincronizando con el taller...</div>
                    ) : (
                        <div className="custom-table-container">

                            {/* --- SECCIÓN PQRS --- */}
                            {tab === 'pqrs' && (
                                <div key="tabla-pqrs">
                                    <div className="custom-table-header" style={{ display: 'flex' }}>
                                        <div style={{ flex: 1 }}>Fecha</div>
                                        <div style={{ flex: 1.5 }}>Asunto</div>
                                        <div style={{ flex: 1 }}>Estado</div>
                                        <div style={{ flex: 2 }}>Respuesta Taller</div>
                                    </div>
                                    {datos.length > 0 ? datos.map((p) => (
                                        <div className="custom-table-row" key={`pqrs-${p.id_pqrs}`} style={{ ...rowStyle, display: 'flex' }}>
                                            <div style={{ flex: 1, padding: '0 10px' }}>
                                                {p.fecha?.substring(0, 10)}
                                            </div>
                                            <div style={{ flex: 1.5, padding: '0 10px', fontWeight: 'bold' }}>
                                                {p.asunto}
                                            </div>
                                            <div style={{ flex: 1, padding: '0 10px' }}>
                                                <span className={`status-pill ${p.estado?.toLowerCase().replace(' ', '-')}`}>
                                                    {p.estado}
                                                </span>
                                            </div>
                                            <div style={{ flex: 2, padding: '0 10px', whiteSpace: 'normal', wordWrap: 'break-word', fontStyle: 'italic' }}>
                                                {p.respuesta_admin || "Sin respuesta aún"}
                                            </div>
                                        </div>
                                    )) : <div className="p-5 text-center text-muted">No se encontraron registros de PQRS.</div>}
                                </div>
                            )}

                            {/* --- SECCIÓN SERVICIOS --- */}
                            {tab === 'servicios' && (
                                <div key="tabla-servicios">
                                    <div style={{
                                        display: 'flex',
                                        background: '#2c3e50',
                                        color: 'white',
                                        padding: '18px 15px',
                                        fontSize: '0.85rem',
                                        textTransform: 'uppercase',
                                        letterSpacing: '1px',
                                        fontWeight: 'bold',
                                        minWidth: '700px'
                                    }}>
                                        <div style={{ flex: 1.5 }}>Servicio</div>
                                        <div style={{ flex: 1 }}>Estado</div>
                                        <div style={{ flex: 1 }}>Mecánico</div>
                                        <div style={{ flex: 1.5 }}>Comentarios</div>
                                        <div style={{ flex: 1 }}>Fecha</div>
                                        <div style={{ flex: 1 }}>Puntuación</div>
                                    </div>
                                    {datos.length > 0 ? datos.map((s) => (
                                        <div key={`serv-${s.idServicio}`} style={{
                                            display: 'flex',
                                            alignItems: 'center',
                                            minHeight: '65px',
                                            padding: '12px 15px',
                                            borderBottom: '1px solid #f1f1f1',
                                            minWidth: '700px'
                                        }}>
                                            <div style={{ flex: 1.5, padding: '0 8px', fontWeight: 'bold' }}>
                                                {s.nombreServicio || "Servicio General"}
                                            </div>
                                            <div style={{ flex: 1, padding: '0 8px' }}>
                                                <span className={`status-pill ${s.estadoServicio?.toLowerCase().replace(' ', '-')}`}>
                                                    {s.estadoServicio || "Sin estado"}
                                                </span>
                                            </div>
                                            <div style={{ flex: 1, padding: '0 8px' }}>
                                                {s.nombreMecanico || "Por asignar"}
                                            </div>
                                            <div style={{ flex: 1.5, padding: '0 8px', whiteSpace: 'normal', wordWrap: 'break-word' }}>
                                                {s.comentario || "Sin observaciones."}
                                            </div>
                                            <div style={{ flex: 1, padding: '0 8px' }}>
                                                {s.fechaServicio?.substring(0, 10)}
                                            </div>
                                            <div style={{ flex: 1, padding: '0 8px', color: '#4e54c8', fontWeight: 'bold' }}>
                                                {s.puntuacion ? '★'.repeat(s.puntuacion) : 'N/A'}
                                            </div>
                                        </div>
                                    )) : <div className="p-5 text-center text-muted">No se encontraron servicios realizados.</div>}
                                </div>
                            )}

                            {/* --- SECCIÓN COTIZACIONES --- */}
                            {tab === 'cotizaciones' && (
                                <div key="tabla-cotizaciones">
                                    <div className="custom-table-header" style={{ display: 'flex' }}>
                                        <div style={{ flex: 1 }}>Fecha</div>
                                        <div style={{ flex: 2 }}>Producto</div>
                                        <div style={{ flex: 0.8 }}>Cantidad</div>
                                        <div style={{ flex: 1 }}>Precio Unit.</div>
                                        <div style={{ flex: 1 }}>Subtotal</div>
                                        <div style={{ flex: 1 }}>Estado</div> {/* Nueva Columna de Estado */}
                                    </div>
                                    {datos.length > 0 ? datos.map((c) => (
                                        <div className="custom-table-row" key={`cotizacion-${c.idCotizacion}`} style={{ ...rowStyle, display: 'flex' }}>
                                            <div style={{ flex: 1, padding: '0 10px' }}>
                                                {c.fecha?.substring(0, 10)}
                                            </div>
                                            <div style={{ flex: 2, padding: '0 10px', fontWeight: 'bold' }}>
                                                {c.producto}
                                            </div>
                                            <div style={{ flex: 0.8, padding: '0 10px', textAlign: 'center' }}>
                                                {c.cantidad}
                                            </div>
                                            <div style={{ flex: 1, padding: '0 10px' }}>
                                                ${c.precioUnitario?.toFixed(2)}
                                            </div>
                                            <div style={{ flex: 1, padding: '0 10px', color: '#198754', fontWeight: 'bold' }}>
                                                ${(c.precioUnitario * c.cantidad)?.toFixed(2)}
                                            </div>
                                            {/* Renderizado Dinámico del Estado de la Cotización */}
                                            <div style={{ flex: 1, padding: '0 10px' }}>
                                                <span className={`status-pill ${(c.estado || 'PENDIENTE').toLowerCase().replace(' ', '-')}`}>
                                                    {c.estado || 'PENDIENTE'}
                                                </span>
                                            </div>
                                        </div>
                                    )) : <div className="p-5 text-center text-muted">No se encontraron cotizaciones.</div>}
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