import React, { useEffect, useState } from 'react';
import '../global.css';

const Servicio = () => {
    const token = localStorage.getItem('token');

    const [usuarios, setUsuarios] = useState([]);
    const [mecanicos, setMecanicos] = useState([]);
    const [tipos, setTipos] = useState([]);
    const [servicios, setServicios] = useState([]);
    const [editMode, setEditMode] = useState(false);
    const [editId, setEditId] = useState(null);

    const [form, setForm] = useState({
        idUsuario: '', idMecanico: '', idTipoServicio: '',
        fechaServicio: new Date().toISOString().split('T')[0],
        estadoServicio: 'PENDIENTE', comentario: '', puntuacion: 5, visibleEnHistorial: true
    });

    const API = 'http://localhost:8080/api/servicios';

    useEffect(() => { cargarDatos(); }, [token]);

    const cargarDatos = async () => {
        if (!token) return;
        try {
            const [resU, resM, resT, resS] = await Promise.all([
                fetch('http://localhost:8080/api/usuarios', { headers: { 'Authorization': `Bearer ${token}` }}),
                fetch('http://localhost:8080/api/mecanicos', { headers: { 'Authorization': `Bearer ${token}` }}),
                fetch('http://localhost:8080/api/tipoServicio', { headers: { 'Authorization': `Bearer ${token}` }}),
                fetch(API, { headers: { 'Authorization': `Bearer ${token}` }})
            ]);
            const [dataU, dataM, dataT, dataS] = await Promise.all([
                resU.json(), resM.json(), resT.json(), resS.json()
            ]);
            setUsuarios(Array.isArray(dataU) ? dataU : []);
            setMecanicos(Array.isArray(dataM) ? dataM : []);
            setTipos(Array.isArray(dataT) ? dataT : []);
            setServicios(Array.isArray(dataS) ? dataS : []);
        } catch(e) { console.error("Error cargando datos:", e); }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const url = editMode ? `${API}/${editId}` : API;
        try {
            const res = await fetch(url, {
                method: editMode ? 'PUT' : 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                body: JSON.stringify(form)
            });
            if (res.ok) {
                alert(editMode ? "Servicio actualizado!" : "Servicio registrado con éxito!");
                resetForm(); cargarDatos();
            } else { alert("Error al guardar: " + await res.text()); }
        } catch (e) { alert("Error al conectar"); }
    };

    const resetForm = () => {
        setForm({ idUsuario: '', idMecanico: '', idTipoServicio: '',
            fechaServicio: new Date().toISOString().split('T')[0],
            estadoServicio: 'PENDIENTE', comentario: '', puntuacion: 5, visibleEnHistorial: true });
        setEditMode(false); setEditId(null);
    };

    const iniciarEdicion = (s) => {
        setEditMode(true); setEditId(s.idServicio);
        setForm({
            idUsuario: s.idUsuario, idMecanico: s.idMecanico, idTipoServicio: s.idTipoServicio,
            fechaServicio: s.fechaServicio ? s.fechaServicio.split('T')[0] : '',
            estadoServicio: s.estadoServicio || 'PENDIENTE', comentario: s.comentario || '',
            puntuacion: s.puntuacion || 5, visibleEnHistorial: s.visibleEnHistorial ?? true
        });
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleEliminar = async (id) => {
        if (!id || !window.confirm("¿Estás seguro de eliminar este servicio?")) return;
        try {
            const res = await fetch(`${API}/${id}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` }});
            if (res.status === 204 || res.ok) {
                setServicios(prev => prev.filter(s => s.idServicio !== id));
                alert("Servicio eliminado!");
            } else { alert("No se pudo eliminar"); }
        } catch (e) { console.error(e); }
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

    // Inline grid styles para 7 columnas — independiente del global.css
    const gridCols = '1.2fr 1fr 1fr 0.9fr 1fr 1.8fr 0.9fr';
    const headerStyle = {
        display: 'grid', gridTemplateColumns: gridCols,
        backgroundColor: '#343a40', color: 'white', fontWeight: 'bold',
        padding: '12px 15px', minWidth: '900px'
    };
    const rowStyle = {
        display: 'grid', gridTemplateColumns: gridCols,
        padding: '12px 15px', borderBottom: '1px solid #eee',
        alignItems: 'center', minWidth: '900px', backgroundColor: 'white'
    };

    return (
        <div className="container mt-4">
            <h2 className="text-primary">Gestión de Servicios</h2>
            <div className="main-content-inner">

                {/* FORMULARIO */}
                <div className="card-panel">
                    <h3 className="text-primary">{editMode ? 'Editar Registro' : '🛠️ Nuevo Registro de Servicio'}</h3>
                    <hr />
                    <form onSubmit={handleSubmit} className="row">
                        <div className="col-md-4">
                            <label>Cliente</label>
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
                            <label>Mecánico Responsable</label>
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
                            <label>Tipo de Servicio</label>
                            <select className="input-bs" required value={form.idTipoServicio}
                                onChange={e => setForm({...form, idTipoServicio: e.target.value})}>
                                <option value="">Seleccione Servicio...</option>
                                {tipos.map(t => {
                                    const id = t.idTipo ?? t.id_tipo_servicio ?? t.idTipoServicio;
                                    return <option key={id} value={id}>{t.nombre}</option>;
                                })}
                            </select>
                        </div>
                        <div className="col-md-4 mt-3">
                            <label>Fecha</label>
                            <input type="date" className="input-bs" value={form.fechaServicio}
                                onChange={e => setForm({...form, fechaServicio: e.target.value})} />
                        </div>
                        <div className="col-md-4 mt-3">
                            <label>Estado</label>
                            <select className="input-bs" value={form.estadoServicio}
                                onChange={e => setForm({...form, estadoServicio: e.target.value})}>
                                <option value="PENDIENTE">PENDIENTE</option>
                                <option value="EN_PROCESO">EN PROCESO</option>
                                <option value="Completado">COMPLETADO</option>
                                <option value="CANCELADO">CANCELADO</option>
                            </select>
                        </div>
                        <div className="col-md-4 mt-3">
                            <label>Puntuación (1-5)</label>
                            <input type="number" min="1" max="5" className="input-bs" value={form.puntuacion}
                                onChange={e => setForm({...form, puntuacion: parseInt(e.target.value)})} />
                        </div>
                        <div className="col-12 mt-3">
                            <textarea className="input-bs w-100" required value={form.comentario}
                                onChange={e => setForm({...form, comentario: e.target.value})}
                                placeholder="Comentario o reporte del cliente..." rows={3} />
                        </div>
                        <div className="col-12 mt-3">
                            <button type="submit" className={`btn-bs ${editMode ? 'btn-success' : 'btn-primary'} w-100`}>
                                {editMode ? 'Guardar Cambios' : 'Registrar en DB'}
                            </button>
                            {editMode && (
                                <button type="button" className="btn-bs btn-danger w-100 mt-2" onClick={resetForm}>
                                    Cancelar
                                </button>
                            )}
                        </div>
                    </form>
                </div>

                {/* TABLA */}
                <div className="card-panel mt-4">
                    <div className="custom-table-container">
                        <div style={headerStyle}>
                            <div>Cliente / Punt.</div>
                            <div>Mecánico</div>
                            <div>Tipo Servicio</div>
                            <div>Fecha</div>
                            <div>Estado</div>
                            <div>Comentario</div>
                            <div style={{ textAlign: 'center' }}>Acciones</div>
                        </div>

                        {servicios.map((s, index) => (
                            <div key={s.idServicio ?? index} style={rowStyle}
                                onMouseEnter={e => e.currentTarget.style.backgroundColor = '#f8f9fa'}
                                onMouseLeave={e => e.currentTarget.style.backgroundColor = 'white'}>
                                <div>
                                    <div>
                                        <div style={{ fontWeight: '500' }}>{getNombreUsuario(s.idUsuario)}</div>
                                        <div style={{ color: '#f59e0b', fontSize: '0.82rem', marginTop: '2px' }}>
                                            {s.puntuacion} ⭐
                                        </div>
                                    </div>
                                </div>
                                <div>{getNombreMecanico(s.idMecanico)}</div>
                                <div>{getNombreTipo(s.idTipoServicio)}</div>
                                <div>{formatFecha(s.fechaServicio)}</div>
                                <div>{s.estadoServicio || '-'}</div>
                                <div style={{ whiteSpace: 'normal', fontSize: '0.83rem', color: '#555', lineHeight: '1.3' }}>
                                    {s.comentario || '-'}
                                </div>
                                <div style={{ display: 'flex', justifyContent: 'center', gap: '8px' }}>
                                    <button className="btn-bs btn-success btn-sm" onClick={() => iniciarEdicion(s)}>
                                        <i className="fa-solid fa-pen"></i>
                                    </button>
                                    <button className="btn-bs btn-danger btn-sm" onClick={() => handleEliminar(s.idServicio)}>
                                        <i className="fa-solid fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        ))}

                        {servicios.length === 0 && (
                            <p style={{ textAlign: 'center', padding: '1rem' }}>No hay servicios registrados.</p>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Servicio;
