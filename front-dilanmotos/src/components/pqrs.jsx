import { useEffect, useState } from "react";
import '../global.css';

export default function Pqrs() {
    const idLogueado = localStorage.getItem("idUsuario");
    const token = localStorage.getItem("token");
    const [solicitudes, setSolicitudes] = useState([]);
    const [nuevo, setNuevo] = useState({ 
        tipo: 'Peticion', asunto: '', descripcion: '', 
        estado: 'PENDIENTE', respuesta_admin: '' 
    });
    
    const [editMode, setEditMode] = useState(false);
    const [selectedId, setSelectedId] = useState(null);

    const API_URL = 'http://localhost:8080/api/pqrs';

    const cargarDatos = async () => {
        try {
            const res = await fetch(API_URL, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (res.ok) {
                const data = await res.json();
                setSolicitudes(Array.isArray(data) ? data : []);
            }
        } catch (error) { 
            console.error("Error al cargar:", error); 
        }
    };

    useEffect(() => { cargarDatos(); }, []);

    const guardar = async (e) => {
        e.preventDefault();
        if (!idLogueado) return alert("Sesión no válida o expirada.");

        const url = editMode ? `${API_URL}/${selectedId}` : API_URL;
        const method = editMode ? 'PUT' : 'POST';

        let payload;
        if (editMode) {
            payload = {
                idUsuario: parseInt(idLogueado),
                estado: nuevo.estado,
                respuesta_admin: nuevo.respuesta_admin
            };
        } else {
            payload = {
                idUsuario: parseInt(idLogueado),
                tipo: nuevo.tipo,
                asunto: nuevo.asunto,
                descripcion: nuevo.descripcion,
                estado: nuevo.estado,
                respuesta_admin: nuevo.respuesta_admin
            };
        }

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
                alert(editMode ? "Actualizado" : "Creado");
                resetForm();
                await cargarDatos();
            } else {
                const error = await res.text();
                console.error("Error:", error);
                alert("Error al guardar");
            }
        } catch (err) { 
            alert("Error de conexión");
        }
    };

    const iniciarEdicion = (p) => {
        window.scrollTo(0, 0);
        setEditMode(true);
        setSelectedId(p.id_pqrs);
        setNuevo({
            tipo: p.tipo,
            asunto: p.asunto,
            descripcion: p.descripcion,
            estado: p.estado || 'PENDIENTE',
            respuesta_admin: p.respuesta_admin || ''
        });
    };

    const resetForm = () => {
        setNuevo({ tipo: 'Peticion', asunto: '', descripcion: '', estado: 'PENDIENTE', respuesta_admin: '' });
        setEditMode(false);
        setSelectedId(null);
    };

    const eliminar = async (id, asunto) => {
        if (!window.confirm(`¿Eliminar "${asunto}"?`)) return;
        try {
            const res = await fetch(`${API_URL}/${id}`, { 
                method: 'DELETE', 
                headers: { 'Authorization': `Bearer ${token}` } 
            });
            if (res.ok) {
                alert("Eliminado");
                cargarDatos();
            }
        } catch (error) {
            console.error("Error:", error);
        }
    };

    return (
        <div className="main-content-inner">
            <div className="card-panel shadow">
                <h3 className="text-primary">{editMode ? 'Gestionar Solicitud' : 'Radicar PQRS'}</h3>
                <hr />
                <form onSubmit={guardar}>
                    <div className="row">
                        <div className="col-md-6 mb-3">
                            <label className="fw-bold">Tipo</label>
                            <select className="input-bs" value={nuevo.tipo} onChange={e => setNuevo({...nuevo, tipo: e.target.value})} disabled={editMode}>
                                <option value="Peticion">Peticion</option>
                                <option value="Queja">Queja</option>
                                <option value="Reclamo">Reclamo</option>
                            </select>
                        </div>
                        {editMode && (
                            <div className="col-md-6 mb-3">
                                <label className="fw-bold text-danger">Estado</label>
                                <select className="input-bs" value={nuevo.estado} onChange={e => setNuevo({...nuevo, estado: e.target.value})}>
                                    <option value="PENDIENTE">PENDIENTE</option>
                                    <option value="EN PROCESO">EN PROCESO</option>
                                    <option value="RESPONDIDA">RESPONDIDA</option>
                                </select>
                            </div>
                        )}
                    </div>

                    <div className="mb-3">
                        <label className="fw-bold">Asunto</label>
                        <input className="input-bs" value={nuevo.asunto} onChange={e => setNuevo({...nuevo, asunto: e.target.value})} required disabled={editMode} />
                    </div>

                    <div className="mb-3">
                        <label className="fw-bold">Descripción</label>
                        <textarea className="input-bs" rows="3" value={nuevo.descripcion} onChange={e => setNuevo({...nuevo, descripcion: e.target.value})} required disabled={editMode} />
                    </div>

                    {editMode && (
                        <div className="mt-3 p-3 bg-light border rounded">
                            <label className="fw-bold text-success">Respuesta Admin</label>
                            <textarea className="input-bs" rows="3" value={nuevo.respuesta_admin} onChange={e => setNuevo({...nuevo, respuesta_admin: e.target.value})} />
                        </div>
                    )}

                    <div className="d-flex gap-2 mt-4">
                        <button type="submit" className="btn-bs btn-primary flex-grow-1">{editMode ? 'Guardar Cambios' : 'Enviar PQRS'}</button>
                        {editMode && <button type="button" className="btn-bs btn-secondary" onClick={resetForm}>Cancelar</button>}
                    </div>
                </form>
            </div>

            <div className="card-panel shadow mt-4">
                <h4>Listado General</h4>
                <div className="custom-table-container">
                    <div className="custom-table-header">
                        <div>TIPO</div>
                        <div>ASUNTO</div>
                        <div>ESTADO</div>
                        <div>RESPUESTA</div>
                        <div className="text-center">ACCIONES</div>
                    </div>
                    {solicitudes.map(p => (
                        <div className="custom-table-row" key={p.id_pqrs}>
                            <div className="fw-bold">{p.tipo}</div>
                            <div>{p.asunto}</div>
                            <div>
                                <span className={`badge ${p.estado === 'PENDIENTE' ? 'bg-warning text-dark' : 
                                    p.estado === 'EN PROCESO' ? 'bg-info' : 'bg-success'}`}>
                                    {p.estado || 'PENDIENTE'}
                                </span>
                            </div>
                            <div style={{maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis'}}>
                                {p.respuesta_admin || '-'}
                            </div>
                            <div className="text-center d-flex gap-2 justify-content-center">
                                <button className="btn-bs btn-success btn-sm" onClick={() => iniciarEdicion(p)}>Editar</button>
                                <button className="btn-bs btn-danger btn-sm" onClick={() => eliminar(p.id_pqrs, p.asunto)}>Borrar</button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}