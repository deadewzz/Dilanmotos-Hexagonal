import React, { useEffect, useState } from 'react';
import '../global.css';

const API_URL = 'http://localhost:8080/api/servicios';

const Servicio = () => {
    const token = localStorage.getItem('token');

    const [usuarios, setUsuarios] = useState([]);
    const [mecanicos, setMecanicos] = useState([]);
    const [tipos, setTipos] = useState([]);
    const [servicios, setServicios] = useState([]);
    const [editMode, setEditMode] = useState(false);
    const [editId, setEditId] = useState(null);
    const [mensaje, setMensaje] = useState('');

    const [form, setForm] = useState({
        idUsuario: '', idMecanico: '', idTipoServicio: '',
        fechaServicio: new Date().toISOString().split('T')[0],
        estadoServicio: 'PENDIENTE', comentario: '', puntuacion: 5, visibleEnHistorial: true
    });

    useEffect(() => { 
        cargarDatos(); 
    }, [token]);

    const cargarDatos = async () => {
        if (!token) return;
        try {
            const [resU, resM, resT, resS] = await Promise.all([
                fetch('http://localhost:8080/api/usuarios', { headers: { 'Authorization': `Bearer ${token}` }}),
                fetch('http://localhost:8080/api/mecanicos', { headers: { 'Authorization': `Bearer ${token}` }}),
                fetch('http://localhost:8080/api/tipoServicio', { headers: { 'Authorization': `Bearer ${token}` }}),
                fetch(API_URL, { headers: { 'Authorization': `Bearer ${token}` }})
            ]);
            const [dataU, dataM, dataT, dataS] = await Promise.all([
                resU.json(), resM.json(), resT.json(), resS.json()
            ]);
            setUsuarios(Array.isArray(dataU) ? dataU : []);
            setMecanicos(Array.isArray(dataM) ? dataM : []);
            setTipos(Array.isArray(dataT) ? dataT : []);
            setServicios(Array.isArray(dataS) ? dataS : []);
        } catch(e) { 
            console.error("Error cargando datos:", e); 
            setMensaje('❌ Error al cargar los datos del servidor.');
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const url = editMode ? `${API_URL}/${editId}` : API_URL;
        try {
            const res = await fetch(url, {
                method: editMode ? 'PUT' : 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                body: JSON.stringify(form)
            });
            if (res.ok) {
                setMensaje(editMode ? "✅ ¡Servicio actualizado con éxito!" : "✅ ¡Servicio registrado con éxito!");
                resetForm(); 
                cargarDatos();
            } else { 
                setMensaje("❌ Error al guardar: " + await res.text()); 
            }
        } catch (e) { 
            setMensaje("❌ Error de conexión con el servidor."); 
        }
    };

    const resetForm = () => {
        setForm({ idUsuario: '', idMecanico: '', idTipoServicio: '',
            fechaServicio: new Date().toISOString().split('T')[0],
            estadoServicio: 'PENDIENTE', comentario: '', puntuacion: 5, visibleEnHistorial: true });
        setEditMode(false); 
        setEditId(null);
        setMensaje('');
    };

    const iniciarEdicion = (s) => {
        setEditMode(true); 
        setEditId(s.idServicio);
        setForm({
            idUsuario: s.idUsuario, idMecanico: s.idMecanico, idTipoServicio: s.idTipoServicio,
            fechaServicio: s.fechaServicio ? s.fechaServicio.split('T')[0] : '',
            estadoServicio: s.estadoServicio || 'PENDIENTE', comentario: s.comentario || '',
            puntuacion: s.puntuacion || 5, visibleEnHistorial: s.visibleEnHistorial ?? true
        });
        setMensaje('');
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleEliminar = async (id) => {
        if (!id || !window.confirm("¿Estás seguro de eliminar este servicio?")) return;
        try {
            const res = await fetch(`${API_URL}/${id}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` }});
            if (res.status === 204 || res.ok) {
                setServicios(prev => prev.filter(s => s.idServicio !== id));
                setMensaje("✅ Servicio eliminado con éxito.");
            } else { 
                setMensaje("❌ No se pudo eliminar el registro seleccionado."); 
            }
        } catch (e) { 
            console.error(e); 
            setMensaje("❌ Error al intentar conectar para eliminar.");
        }
    };

    const getNombreUsuario = (id) => {
        const u = usuarios.find(u => String(u.id_usuario ?? u.idUsuario) === String(id));
        return u ? (u.nombre || u.name) : id;
    };
    const getNombreMecanico = (id) => {
        const m = mecanicos.find(m => String(m.id_mecanico ?? m.idMecanico) === String(id));
        return m ? (m.nombre || m.name) : id;
    };
    const getNombreTipo = (id) => {
        const t = tipos.find(t => String(t.idTipo ?? t.id_tipo_servicio ?? t.idTipoServicio) === String(id));
        return t ? t.nombre : id;
    };
    const formatFecha = (f) => f ? f.split('T')[0] : '-';

    const gridCols = '1.2fr 1fr 1fr 0.9fr 1fr 1.8fr 0.9fr';
    const baseGridStyle = {
        display: 'grid', 
        gridTemplateColumns: gridCols,
        gap: '15px',
        alignItems: 'center', 
        padding: '15px', 
        minWidth: '900px'
    };

    return (
        <div className="main-content-inner">

            {/* PANEL DEL FORMULARIO DE REGISTRO/EDICIÓN */}
            <div className="card-panel">
                <h3 className="text-primary mb-4">
                    {editMode ? '📝 Editar Registro de Servicio' : '🛠️ Nuevo Registro de Servicio'}
                </h3>
                
                {mensaje && (
                    <div className="alert alert-info fw-bold mb-3">
                        {mensaje}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="row g-3">
                    <div className="col-md-4">
                        <label className="form-label fw-bold">Cliente</label>
                        <select className="input-bs" required value={form.idUsuario}
                            onChange={e => setForm({...form, idUsuario: e.target.value})}>
                            <option value="">Seleccione Cliente...</option>
                            {usuarios.map(u => {
                                const id = u.id_usuario ?? u.idUsuario;
                                return <option key={id} value={id}>{u.nombre || u.name}</option>;
                            })}
                        </select>
                    </div>
                    
                    <div className="col-md-4">
                        <label className="form-label fw-bold">Mecánico Responsable</label>
                        <select className="input-bs" required value={form.idMecanico}
                            onChange={e => setForm({...form, idMecanico: e.target.value})}>
                            <option value="">Seleccione Mecánico...</option>
                            {mecanicos.map(m => {
                                const id = m.id_mecanico ?? m.idMecanico;
                                return <option key={id} value={id}>{m.nombre || m.name}</option>;
                            })}
                        </select>
                    </div>

                    <div className="col-md-4">
                        <label className="form-label fw-bold">Tipo de Servicio</label>
                        <select className="input-bs" required value={form.idTipoServicio}
                            onChange={e => setForm({...form, idTipoServicio: e.target.value})}>
                            <option value="">Seleccione Servicio...</option>
                            {tipos.map(t => {
                                const id = t.idTipo ?? t.id_tipo_servicio ?? t.idTipoServicio;
                                return <option key={id} value={id}>{t.nombre}</option>;
                            })}
                        </select>
                    </div>

                    <div className="col-md-4">
                        <label className="form-label fw-bold">Fecha</label>
                        <input type="date" className="input-bs" value={form.fechaServicio}
                            onChange={e => setForm({...form, fechaServicio: e.target.value})} />
                    </div>

                    <div className="col-md-4">
                        <label className="form-label fw-bold">Estado</label>
                        <select className="input-bs" value={form.estadoServicio}
                            onChange={e => setForm({...form, estadoServicio: e.target.value})}>
                            <option value="PENDIENTE">PENDIENTE</option>
                            <option value="EN_PROCESO">EN PROCESO</option>
                            <option value="Completado">COMPLETADO</option>
                            <option value="CANCELADO">CANCELADO</option>
                        </select>
                    </div>

                    <div className="col-md-4">
                        <label className="form-label fw-bold">Puntuación (1-5)</label>
                        <input type="number" min="1" max="5" className="input-bs" value={form.puntuacion}
                            onChange={e => setForm({...form, puntuacion: parseInt(e.target.value) || 5})} />
                    </div>

                    <div className="col-12">
                        <label className="form-label fw-bold">Comentarios / Diagnóstico</label>
                        <textarea className="input-bs w-100" required value={form.comentario}
                            onChange={e => setForm({...form, comentario: e.target.value})}
                            placeholder="Detalles del reporte o novedades del vehículo..." rows={3} />
                    </div>

                    {/* ACCIONES VERTICALES UNIFICADAS */}
                    <div className="col-12 mt-4" style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                        <button 
                            type="submit" 
                            className={`btn-bs w-100 ${editMode ? 'btn-success' : 'btn-success'}`}
                            style={{ padding: '12px', fontSize: '1rem' }}
                        >
                            {editMode ? 'Actualizar Cambios' : 'Registrar Servicio'}
                        </button>
                        {editMode && (
                            <button 
                                type="button" 
                                className="btn-bs btn-danger w-100" 
                                onClick={resetForm}
                                style={{ padding: '12px', fontSize: '1rem' }}
                            >
                                Cancelar Edición
                            </button>
                        )}
                    </div>
                </form>
            </div>

            {/* PANEL DE LA LISTA / TABLA DE REGISTROS */}
            <div className="card-panel mt-4">
                <h4 className="mb-4">📋 Listado General de Servicios</h4>
                
                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    
                    {/* CABECERA CON GRID */}
                    <div style={{ 
                        ...baseGridStyle,
                        background: 'var(--header-table)', 
                        color: 'var(--white)', 
                        fontWeight: 'bold'
                    }}>
                        <div>Cliente / Punt.</div>
                        <div>Mecánico</div>
                        <div>Tipo Servicio</div>
                        <div>Fecha</div>
                        <div>Estado</div>
                        <div>Comentario</div>
                        <div style={{ display: 'flex', justifyContent: 'center' }}>Acciones</div>
                    </div>

                    {/* CUERPO DINÁMICO */}
                    {servicios.length > 0 ? (
                        servicios.map((s, index) => (
                            <div 
                                key={s.idServicio ?? index} 
                                className="table-row-hover-effect"
                                style={{ 
                                    ...baseGridStyle,
                                    borderBottom: '1px solid #eee', 
                                    background: 'var(--white)',
                                    transition: '0.2s'
                                }}
                            >
                                <div>
                                    <div style={{ fontWeight: '600', color: 'var(--text-dark)' }}>
                                        {getNombreUsuario(s.idUsuario)}
                                    </div>
                                    <div style={{ color: '#f59e0b', fontSize: '0.82rem', marginTop: '2px' }}>
                                        {s.puntuacion} ⭐
                                    </div>
                                </div>
                                <div style={{ color: '#4b5563' }}>{getNombreMecanico(s.idMecanico)}</div>
                                <div style={{ color: '#4b5563' }}>{getNombreTipo(s.idTipoServicio)}</div>
                                <div style={{ color: '#4b5563' }}>{formatFecha(s.fechaServicio)}</div>
                                <div>
                                    <span className={`badge ${s.estadoServicio === 'Completado' ? 'bg-success' : 'bg-warning text-dark'}`} style={{ fontSize: '0.78rem', padding: '5px 10px', borderRadius: '6px' }}>
                                        {s.estadoServicio || '-'}
                                    </span>
                                </div>
                                <div style={{ whiteSpace: 'normal', fontSize: '0.83rem', color: '#555', lineHeight: '1.3', textOverflow: 'ellipsis', overflow: 'hidden' }}>
                                    {s.comentario || '-'}
                                </div>
                                <div style={{ display: 'flex', justifyContent: 'center', gap: '8px' }}>
                                    <button className="btn-bs btn-success btn-sm" style={{ padding: '6px 12px' }} onClick={() => iniciarEdicion(s)}>
                                        <i className="fa-solid fa-pen"></i>
                                    </button>
                                    <button className="btn-bs btn-danger btn-sm" style={{ padding: '6px 12px' }} onClick={() => handleEliminar(s.idServicio)}>
                                        <i className="fa-solid fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="p-4 text-center text-muted">No se encontraron servicios registrados en el sistema.</div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Servicio;