import { useEffect, useState } from "react";
import '../global.css';
import { authFetch } from "../api";

export default function Pqrs() {
    const idLogueado = localStorage.getItem("idUsuario");
    const token = localStorage.getItem("token");
    const [solicitudes, setSolicitudes] = useState([]);
    const [nuevo, setNuevo] = useState({ 
        tipo: 'Peticion', 
        asunto: '', 
        descripcion: '', 
        comentario_usuario: '', 
        estado: 'PENDIENTE',
        respuesta_admin: '',
        calificacion_servicio: '-',
        comentario_servicio: '-'
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
            console.error("Error al cargar PQRS:", error);
        }
    };

    useEffect(() => {
        cargarDatos();
    }, []);

    const guardar = async (e) => {
        e.preventDefault();

        const payload = {
            idUsuario: parseInt(idLogueado),
            tipo: nuevo.tipo,
            asunto: nuevo.asunto,
            descripcion: nuevo.descripcion,
            comentario_usuario: nuevo.comentario_usuario || nuevo.descripcion,
            estado: nuevo.estado || 'PENDIENTE',
            respuesta_admin: nuevo.respuesta_admin || "Sin respuesta",
            calificacion_servicio: nuevo.calificacion_servicio || "-",
            comentario_servicio: nuevo.comentario_servicio || "-"
        };

        const url = editMode ? `${API_URL}/${selectedId}` : API_URL;
        const method = editMode ? 'PUT' : 'POST';

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
                alert(editMode ? "Registro actualizado" : "Registro creado");
                resetForm();
                cargarDatos();
            } else {
                alert("Error 500 en el servidor");
            }
        } catch (err) {
            alert("Error de conexion");
        }
    };

    const resetForm = () => {
        setNuevo({ 
            tipo: 'Peticion', asunto: '', descripcion: '', 
            comentario_usuario: '', estado: 'PENDIENTE',
            respuesta_admin: '', calificacion_servicio: '-', comentario_servicio: '-'
        });
        setEditMode(false);
        setSelectedId(null);
    };

    const iniciarEdicion = (p) => {
        window.scrollTo(0, 0);
        setEditMode(true);
        setSelectedId(p.idPqrs);
        setNuevo({
            tipo: p.tipo,
            asunto: p.asunto,
            descripcion: p.descripcion,
            comentario_usuario: p.comentario_usuario || '',
            estado: p.estado,
            respuesta_admin: p.respuesta_admin || '',
            calificacion_servicio: p.calificacion_servicio || '-',
            comentario_servicio: p.comentario_servicio || '-'
        });
    };

    return (
        <div className="main-content-inner">
            <div className="card-panel">
                <h3 className="text-primary">
                    {editMode ? 'Gestionar Solicitud' : 'Nueva PQRS'}
                </h3>
                <hr />
                <form onSubmit={guardar}>
                    <div className="row">
                        <div className="col-md-6 mb-3">
                            <label className="fw-bold">Tipo de Tramite</label>
                            <select className="input-bs" value={nuevo.tipo} onChange={e => setNuevo({...nuevo, tipo: e.target.value})} disabled={editMode}>
                                <option value="Peticion">Peticion</option>
                                <option value="Queja">Queja</option>
                                <option value="Reclamo">Reclamo</option>
                                <option value="Sugerencia">Sugerencia</option>
                            </select>
                        </div>
                        {editMode && (
                            <div className="col-md-6 mb-3">
                                <label className="fw-bold text-danger">Estado Actual</label>
                                <select className="input-bs" value={nuevo.estado} onChange={e => setNuevo({...nuevo, estado: e.target.value})}>
                                    <option value="PENDIENTE">PENDIENTE</option>
                                    <option value="EN PROCESO">EN PROCESO</option>
                                    <option value="RESPONDIDA">RESPONDIDA</option>
                                    <option value="CERRADA">CERRADA</option>
                                </select>
                            </div>
                        )}
                    </div>

                    <div className="mb-3">
                        <label className="fw-bold">Asunto</label>
                        <input className="input-bs" value={nuevo.asunto} onChange={e => setNuevo({...nuevo, asunto: e.target.value})} required disabled={editMode} />
                    </div>

                    <div className="mb-3">
                        <label className="fw-bold">Descripcion</label>
                        <textarea className="input-bs" rows="3" value={nuevo.descripcion} onChange={e => setNuevo({...nuevo, descripcion: e.target.value})} required disabled={editMode} />
                    </div>

                    {editMode && (
                        <div className="mt-3 p-3 bg-light border rounded">
                            <label className="fw-bold text-success">Respuesta del Administrador</label>
                            <textarea className="input-bs" rows="3" value={nuevo.respuesta_admin} onChange={e => setNuevo({...nuevo, respuesta_admin: e.target.value})} />
                        </div>
                    )}

                    <div className="d-flex gap-2 mt-4">
                        <button type="submit" className="btn-bs btn-primary flex-grow-1">
                            {editMode ? 'Guardar Cambios' : 'Crear PQRS'}
                        </button>
                        {editMode && <button type="button" className="btn-bs btn-secondary" onClick={resetForm}>Cancelar</button>}
                    </div>
                </form>
            </div>

            <div className="card-panel mt-4">
                <h4 className="mb-4">Listado General de PQRS</h4>
                <div className="custom-table-container">
                    <div className="custom-table-header">
                        <div>Tipo</div>
                        <div>Asunto</div>
                        <div>Estado</div>
                        <div className="text-center">Acciones</div>
                    </div>
                    {solicitudes.map(p => (
                        <div className="custom-table-row" key={p.idPqrs}>
                            <div className="fw-bold">{p.tipo}</div>
                            <div>{p.asunto}</div>
                            <div>
                                <span className={`badge ${p.estado === 'PENDIENTE' ? 'bg-warning' : 'bg-success'}`}>
                                    {p.estado}
                                </span>
                            </div>
                            <div className="text-center d-flex gap-2 justify-content-center">
                                <button className="btn-bs btn-success btn-sm" onClick={() => iniciarEdicion(p)}>
                                    Editar
                                </button>
                                <button className="btn-bs btn-danger btn-sm" onClick={async () => {
                                    if(window.confirm("¿Eliminar registro?")) {
                                        await fetch(`${API_URL}/${p.idPqrs}`, {
                                            method:'DELETE',
                                            headers: { 'Authorization': `Bearer ${token}` }
                                        });
                                        cargarDatos();
                                    }
                                }}>
                                    Borrar
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}