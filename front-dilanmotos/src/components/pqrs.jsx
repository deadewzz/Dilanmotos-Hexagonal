import { useEffect, useState } from "react";
import '../global.css';
import { API_BASE_URL } from '../api';

export default function Pqrs() {
    const idLogueado = localStorage.getItem("idUsuario");
    const token = localStorage.getItem("token");
    
    // Estados de datos y control de red
    const [solicitudes, setSolicitudes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);

    // Estados del formulario
    const [nuevo, setNuevo] = useState({ 
        tipo: 'Peticion', 
        asunto: '', 
        descripcion: '', 
        estado: 'PENDIENTE', 
        respuesta_admin: '' 
    });
    
    const [editMode, setEditMode] = useState(false);
    const [selectedId, setSelectedId] = useState(null);
    const [estadoOriginal, setEstadoOriginal] = useState(null);

    const API_URL = `${API_BASE_URL}/api/pqrs`;

    const cargarDatos = async () => {
        setLoading(true);
        setError(false);
        try {
            const res = await fetch(API_URL, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (res.ok) {
                const data = await res.json();
                setSolicitudes(Array.isArray(data) ? data : []);
            } else {
                setError(true);
            }
        } catch (err) { 
            console.error("Error al cargar historial de PQRS:", err); 
            setError(true);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { 
        cargarDatos(); 
    }, []);

    // Solo se considera respondida y bloqueada si YA venía en estado RESPONDIDA desde la BD
    const esRespondidaBD = editMode && estadoOriginal === 'RESPONDIDA';

    const guardar = async (e) => {
        e.preventDefault();

        if (esRespondidaBD) {
            alert("⚠️ No es posible modificar una solicitud que ya fue respondida previamente.");
            return;
        }

        const url = editMode ? `${API_URL}/${selectedId}` : API_URL;
        const method = editMode ? 'PUT' : 'POST';

        const payload = editMode ? {
            idUsuario: parseInt(idLogueado),
            estado: nuevo.estado,
            respuesta_admin: nuevo.respuesta_admin
        } : {
            idUsuario: parseInt(idLogueado),
            tipo: nuevo.tipo,
            asunto: nuevo.asunto,
            descripcion: nuevo.descripcion,
            estado: nuevo.estado,
            respuesta_admin: nuevo.respuesta_admin
        };

        try {
            const res = await fetch(url, {
                method: method,
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                alert(editMode ? "✅ Solicitud actualizada con éxito" : "✅ Solicitud PQRS radicada con éxito");
                resetForm();
                await cargarDatos();
            } else {
                alert("Ocurrió un error al procesar el formulario.");
            }
        } catch (err) {
            alert("Error de conexión con el servidor.");
        }
    };

    const iniciarEdicion = (p) => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
        setEditMode(true);
        setSelectedId(p.id_pqrs);
        setEstadoOriginal(p.estado); // Preservar estado inicial antes de editar

        setNuevo({
            tipo: p.tipo,
            asunto: p.asunto,
            descripcion: p.descripcion,
            estado: p.estado || 'PENDIENTE',
            respuesta_admin: p.respuesta_admin || p.respuestaAdmin || ''
        });
    };

    const resetForm = () => {
        setNuevo({ tipo: 'Peticion', asunto: '', descripcion: '', estado: 'PENDIENTE', respuesta_admin: '' });
        setEditMode(false);
        setSelectedId(null);
        setEstadoOriginal(null);
    };

    const eliminar = async (id, asunto) => {
        if (!window.confirm(`¿Está seguro de eliminar la solicitud "${asunto}"?`)) return;
        try {
            const res = await fetch(`${API_URL}/${id}`, { 
                method: 'DELETE', 
                headers: { 'Authorization': `Bearer ${token}` } 
            });
            if (res.ok) {
                alert("✅ Registro eliminado correctamente.");
                cargarDatos();
            }
        } catch (error) {
            console.error("Error al eliminar:", error);
        }
    };

    return (
        <div className="main-content-inner">
            {/* PANEL DE FORMULARIO */}
            <div className="card-panel">
                <h3 className="text-primary mb-4">
                    {editMode ? '📝 Gestionar y Responder PQRS' : '📩 Radicar Nueva PQRS'}
                </h3>
                <form onSubmit={guardar}>
                    <div className="row">
                        <div className="col-md-6 mb-3">
                            <label className="form-label fw-bold">Tipo de Solicitud</label>
                            <select 
                                className="input-bs" 
                                value={nuevo.tipo} 
                                onChange={e => setNuevo({...nuevo, tipo: e.target.value})} 
                                disabled={editMode}
                            >
                                <option value="Peticion">Petición</option>
                                <option value="Queja">Queja</option>
                                <option value="Reclamo">Reclamo</option>
                            </select>
                        </div>
                        {editMode && (
                            <div className="col-md-6 mb-3">
                                <label className="form-label fw-bold text-danger">Estado de la Solicitud</label>
                                <select 
                                    className="input-bs" 
                                    value={nuevo.estado} 
                                    onChange={e => setNuevo({...nuevo, estado: e.target.value})}
                                    disabled={esRespondidaBD}
                                >
                                    <option value="PENDIENTE">PENDIENTE</option>
                                    <option value="EN PROCESO">EN PROCESO</option>
                                    <option value="RESPONDIDA">RESPONDIDA</option>
                                </select>
                            </div>
                        )}
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Asunto</label>
                        <input 
                            type="text"
                            className="input-bs" 
                            placeholder="Ej: Inconformidad con servicio técnico"
                            value={nuevo.asunto} 
                            onChange={e => setNuevo({...nuevo, asunto: e.target.value})} 
                            required 
                            disabled={editMode} 
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Descripción / Hechos</label>
                        <textarea 
                            className="input-bs" 
                            rows="4" 
                            placeholder="Detalla aquí los motivos de tu solicitud..."
                            value={nuevo.descripcion} 
                            onChange={e => setNuevo({...nuevo, descripcion: e.target.value})} 
                            required 
                            disabled={editMode} 
                        />
                    </div>

                    {editMode && (
                        <div className="mt-3 p-3 border rounded style-reply-box" style={{ background: '#f8f9fa' }}>
                            <label className="form-label fw-bold text-success">Respuesta Administrativa Oficial</label>
                            <textarea 
                                className="input-bs" 
                                rows="4" 
                                placeholder={esRespondidaBD ? "Sin respuesta registrada." : "Escribe la respuesta formal para el usuario..."}
                                value={nuevo.respuesta_admin} 
                                onChange={e => setNuevo({...nuevo, respuesta_admin: e.target.value})} 
                                required={!esRespondidaBD}
                                disabled={esRespondidaBD}
                            />  
                        </div>
                    )}

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginTop: '20px' }}>
                        <button 
                            type="submit" 
                            className="btn-bs btn-success w-100"
                            style={{ 
                                padding: '12px', 
                                fontSize: '1rem', 
                                opacity: esRespondidaBD ? 0.6 : 1, 
                                cursor: esRespondidaBD ? 'not-allowed' : 'pointer' 
                            }}
                            disabled={esRespondidaBD}
                        >
                            {esRespondidaBD ? '🔒 Solicitud Respondida (Lectura)' : editMode ? 'Guardar Cambios' : 'Enviar Formulario'}
                        </button>
                        {editMode && (
                            <button 
                                type="button" 
                                className="btn-bs btn-danger w-100" 
                                onClick={resetForm}
                                style={{ padding: '12px', fontSize: '1rem' }}
                            >
                                {esRespondidaBD ? 'Volver / Cancelar' : 'Cancelar Edición'}
                            </button>
                        )}
                    </div>
                </form>
            </div>

            {/* PANEL DE HISTORIAL */}
            <div className="card-panel mt-4">
                <div className="row align-items-center mb-3">
                    <div className="col-md-6">
                        <h4 className="text-muted m-0">📚 Historial y Listado General</h4>
                    </div>
                </div>
                
                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    <div style={{ 
                        display: 'grid', 
                        gridTemplateColumns: '1.2fr 2fr 1.2fr 2.5fr 1fr', 
                        gap: '15px', 
                        alignItems: 'center', 
                        padding: '15px',
                        background: 'var(--header-table)',
                        color: 'var(--white)',
                        fontWeight: 'bold',
                        minWidth: '850px'
                    }}>
                        <div>Tipo</div>
                        <div>Asunto</div>
                        <div>Estado</div>
                        <div>Respuesta Admin</div>
                        <div style={{ display: 'flex', justifyContent: 'center' }}>Acciones</div>
                    </div>

                    {/* MANEJO CONDICIONAL DE CARGA, ERROR Y REGISTROS */}
                    {loading ? (
                        <div className="p-4 text-center text-muted">
                            <i className="fa-solid fa-spinner fa-spin me-2"></i> Cargando historial de PQRS...
                        </div>
                    ) : error ? (
                        <div className="p-4 text-center text-danger">
                            <p className="mb-2 fw-bold">⚠️ Error al recuperar el historial de PQRS. Intente de nuevo.</p>
                            <button 
                                type="button" 
                                className="btn-bs btn-primary btn-sm mt-2" 
                                onClick={cargarDatos}
                            >
                                <i className="fa-solid fa-rotate-right me-1"></i> Reintentar
                            </button>
                        </div>
                    ) : solicitudes.length > 0 ? (
                        solicitudes.map(p => {
                            const respondida = p.estado === 'RESPONDIDA';
                            return (
                                <div key={p.id_pqrs} className="table-row-hover-effect" style={{ 
                                    display: 'grid', 
                                    gridTemplateColumns: '1.2fr 2fr 1.2fr 2.5fr 1fr', 
                                    gap: '15px', 
                                    alignItems: 'center', 
                                    padding: '15px',
                                    borderBottom: '1px solid #eee',
                                    minWidth: '850px',
                                    background: 'var(--white)'
                                }}>
                                    <div className="fw-bold" style={{ color: 'var(--text-dark)' }}>{p.tipo}</div>
                                    <div style={{ color: '#4b5563', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} title={p.asunto}>
                                        {p.asunto}
                                    </div>
                                    <div>
                                        <span className={`badge ${
                                            p.estado === 'PENDIENTE' ? 'bg-warning text-dark' : 
                                            p.estado === 'EN PROCESO' ? 'bg-info text-white' : 'bg-success text-white'
                                        }`} style={{ padding: '5px 10px', borderRadius: '5px', fontSize: '0.85rem', fontWeight: 'bold' }}>
                                            {p.estado || 'PENDIENTE'}
                                        </span>
                                    </div>
                                    <div style={{ color: '#4b5563', fontSize: '0.9rem', display: '-webkit-box', WebkitLineClamp: '2', WebkitBoxOrient: 'vertical', overflow: 'hidden', textOverflow: 'ellipsis' }} title={p.respuesta_admin || p.respuestaAdmin || ''}>
                                        {p.respuesta_admin || p.respuestaAdmin || <span className="text-muted italic">Sin responder</span>}
                                    </div>
                                    <div className="text-center d-flex justify-content-center gap-2">
                                        <button 
                                            className="btn-bs btn-success btn-sm" 
                                            style={{ 
                                                padding: '6px 12px', 
                                                opacity: respondida ? 0.4 : 1, 
                                                cursor: respondida ? 'not-allowed' : 'pointer' 
                                            }} 
                                            onClick={() => iniciarEdicion(p)}
                                            disabled={respondida}
                                            title={respondida ? "No se puede editar una PQRS respondida" : "Editar"}
                                        >
                                            <i className="fa-solid fa-pen"></i>
                                        </button>
                                        <button 
                                            className="btn-bs btn-danger btn-sm" 
                                            style={{ padding: '6px 12px' }} 
                                            onClick={() => eliminar(p.id_pqrs, p.asunto)}
                                        >
                                            <i className="fa-solid fa-trash"></i>
                                        </button>
                                    </div>
                                </div>
                            );
                        })
                    ) : (
                        <div className="p-4 text-center text-muted">No se registran solicitudes de PQRS en el sistema.</div>
                    )}
                </div>
            </div>
        </div>
    );
}