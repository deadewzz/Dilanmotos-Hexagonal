import React, { useEffect, useState } from 'react';
import '../global.css';
import { authFetch } from "../api";

const Servicio = () => {
    // Recuperamos el token para todas las peticiones
    const token = localStorage.getItem('token');

    // Estados para las listas
    const [usuarios, setUsuarios] = useState([]);
    const [motos, setMotos] = useState([]);
    const [tipos, setTipos] = useState([]);
    const [historial, setHistorial] = useState([]);
    
    // Estados para el modo edición
    const [editMode, setEditMode] = useState(false);
    const [editId, setEditId] = useState(null);

    // Estado del formulario
    const [form, setForm] = useState({
        usuario: { idUsuario: '' },
        moto: { idMoto: '' },
        tipoServicio: { id_tipo_servicio: '' }, // Nombre exacto para Java
        comentario: '',
        observacionesMecanico: '',
        fechaServicio: new Date().toISOString().split('T')[0],
        estadoServicio: 'FINALIZADO',
        puntuacion: 5,
        visibleEnHistorial: true
    });

    const API = 'http://localhost:8080/api/servicios';

    // 1. Carga Inicial de base (Usuarios y Tipos de Servicio)
    useEffect(() => {
        const cargarBase = async () => {
            try {
                const u = await fetch('http://localhost:8080/api/usuarios', {
                    headers: { 'Authorization': `Bearer ${token}` }
                }).then(r => r.json());
                
                const t = await fetch('http://localhost:8080/api/tiposervicio', {
                    headers: { 'Authorization': `Bearer ${token}` }
                }).then(r => r.json());
                
                setUsuarios(u);
                setTipos(t);
            } catch(e) { 
                console.error("Error cargando base de datos", e); 
            }
        };
        cargarBase();
    }, [token]);

    // 2. Carga dinámica al seleccionar un cliente (CON MALLA DE SEGURIDAD)
    useEffect(() => {
        if (!form.usuario.idUsuario) {
            setMotos([]);
            setHistorial([]);
            return;
        }

        const cargarDatosCliente = async () => {
            try {
                const [resMotos, resHistorial] = await Promise.all([
                    fetch(`http://localhost:8080/api/motos/usuario/${form.usuario.idUsuario}`, {
                        headers: { 'Authorization': `Bearer ${token}` }
                    }).then(r => r.json()),
                    fetch(`${API}/usuario/${form.usuario.idUsuario}`, {
                        headers: { 'Authorization': `Bearer ${token}` }
                    }).then(r => r.json())
                ]);

                setMotos(Array.isArray(resMotos) ? resMotos : (resMotos.idMoto ? [resMotos] : []));
                setHistorial(Array.isArray(resHistorial) ? resHistorial : (resHistorial.idServicio ? [resHistorial] : []));
                
            } catch (error) {
                console.error("Error al cargar datos del cliente:", error);
                setMotos([]);
                setHistorial([]);
            }
        };

        cargarDatosCliente();
    }, [form.usuario.idUsuario, token]);

    // 3. Lógica para EDITAR
    const handleEdit = (s) => {
        setEditMode(true);
        setEditId(s.idServicio);
        setForm({
            ...s, 
            usuario: { idUsuario: s.usuario.idUsuario },
            moto: { idMoto: s.moto.idMoto },
            tipoServicio: { id_tipo_servicio: s.tipoServicio.id_tipo_servicio }
        });
        window.scrollTo(0, 0); 
    };

    // 4. Lógica para BORRAR
    const handleDelete = async (id) => {
        if (window.confirm("¿Estás seguro de que deseas eliminar este servicio del historial?")) {
            try {
                const res = await fetch(`${API}/${id}`, { 
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                if (res.ok) {
                    window.location.reload();
                } else {
                    alert("Error al intentar eliminar.");
                }
            } catch (e) {
                console.error("Error de conexión al borrar", e);
            }
        }
    };

    // 5. Lógica para GUARDAR (Crear o Actualizar)
    const handleSubmit = async (e) => {
        e.preventDefault();
        const url = editMode ? `${API}/${editId}` : API;
        const method = editMode ? 'PUT' : 'POST';

        try {
            const res = await fetch(url, {
                method: method,
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(form)
            });

            if (res.ok) {
                alert(editMode ? "¡Registro actualizado con éxito!" : "¡Servicio registrado con éxito!");
                window.location.reload();
            } else {
                const errorTexto = await res.text();
                alert("Error del servidor: " + errorTexto);
            }
        } catch (e) {
            alert("Error de conexión con el servidor.");
            console.error(e);
        }
    };

    return (
        <div className="card-panel">
            <h3>{editMode ? '✏️ Editar Servicio' : '🛠️ Nuevo Servicio'}</h3>
            <form onSubmit={handleSubmit} className="row">
                
                <select className="col-md-4 input-bs" required value={form.usuario.idUsuario} 
                    onChange={e => setForm({...form, usuario: {idUsuario: e.target.value}})}>
                    <option value="">Cliente...</option>
                    {usuarios.map(u => <option key={`u-${u.idUsuario}`} value={u.idUsuario}>{u.nombre}</option>)}
                </select>

                <select className="col-md-4 input-bs" required value={form.moto.idMoto}
                    onChange={e => setForm({...form, moto: {idMoto: e.target.value}})}>
                    <option value="">Moto...</option>
                    {motos.map(m => <option key={`m-${m.idMoto}`} value={m.idMoto}>{m.modelo}</option>)}
                </select>

                <select className="col-md-4 input-bs" required value={form.tipoServicio.id_tipo_servicio}
                    onChange={e => setForm({...form, tipoServicio: {id_tipo_servicio: e.target.value}})}>
                    <option value="">Servicio...</option>
                    {tipos.map(t => <option key={`t-${t.id_tipo_servicio}`} value={t.id_tipo_servicio}>{t.nombre}</option>)}
                </select>

                <textarea className="col-12 input-bs mt-3" required value={form.comentario}
                    onChange={e => setForm({...form, comentario: e.target.value})} placeholder="Reporte o comentario del cliente..." />

                <button type="submit" className="btn-bs btn-primary w-100 mt-3">
                    {editMode ? 'Guardar Cambios' : 'Registrar Servicio'}
                </button>
                
                {editMode && (
                    <button type="button" onClick={() => window.location.reload()} className="btn-bs btn-secondary w-100 mt-2">
                        Cancelar Edición
                    </button>
                )}
            </form>

            <div className="mt-5">
                <h4>Historial de Mantenimientos</h4>
                {historial.length === 0 ? (
                    <div className="alert alert-info mt-2">No hay historial para este cliente.</div>
                ) : (
                    <div className="table-responsive">
                        <table className="table table-hover mt-3">
                            <thead className="table-dark">
                                <tr>
                                    <th>Fecha</th>
                                    <th>Tipo de Servicio</th>
                                    <th>Comentarios</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {historial.map(h => (
                                    <tr key={`h-${h.idServicio}`}>
                                        <td>{h.fechaServicio}</td>
                                        <td>{h.tipoServicio?.nombre}</td>
                                        <td>{h.comentario}</td>
                                        <td>
                                            <button type="button" onClick={() => handleEdit(h)} className="btn btn-sm btn-warning me-2" title="Editar">✏️</button>
                                            <button type="button" onClick={() => handleDelete(h.idServicio)} className="btn btn-sm btn-danger" title="Borrar">🗑️</button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Servicio;