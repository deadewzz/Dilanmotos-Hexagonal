import React, { useState, useEffect } from 'react';
import '../global.css';

const TipoServicio = () => {
    const [tipoServicios, setTipoServicios] = useState([]); 
    const [busqueda, setBusqueda] = useState(""); 
    const [nuevoTipo, setNuevoTipo] = useState({ nombre: "", descripcion: "" }); 
    const [editMode, setEditMode] = useState(false); 

    const API_URL = "http://localhost:8080/api/tipoServicio";
    const token = localStorage.getItem('token');

    const cargarServicios = async () => {
        try {
            const url = busqueda ? `${API_URL}?search=${busqueda}` : API_URL;
            const response = await fetch(url, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                setTipoServicios(data);
            }
        } catch (error) {
            console.log("Error al conectar con la API:", error);
        }
    };

    useEffect(() => {
        cargarServicios();
    }, []);

    const handleGuardar = async (e) => {
        e.preventDefault();
        const metodo = editMode ? 'PUT' : 'POST';
        const idActual = nuevoTipo.idTipo;
        const url = editMode ? `${API_URL}/${idActual}` : API_URL;

        try {
            const response = await fetch(url, {
                method: metodo,
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(nuevoTipo)
            });

            if (response.ok) {
                alert(editMode ? "✅ Servicio actualizado con éxito!" : "✅ Servicio guardado con éxito!");
                setNuevoTipo({ nombre: "", descripcion: "" });
                setEditMode(false);
                cargarServicios();
            } else {
                alert("❌ Error al procesar la solicitud. Revisa los datos.");
            }
        } catch (error) {
            console.log("Error al procesar la solicitud:", error);
        }
    };

    const iniciarEdicion = (servicio) => {
        setEditMode(true);
        setNuevoTipo(servicio); 
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleEliminar = async (id) => {
        if (!id) {
            console.error("ID no recibido en handleEliminar");
            return;
        }

        if (window.confirm("¿Estás seguro de eliminar este servicio?")) {
            try {
                const response = await fetch(`${API_URL}/${id}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${token}` }
                });
               
                if (response.status === 204 || response.ok) {
                    setTipoServicios(tipoServicios.filter(ts => ts.idTipo !== id));
                    alert("✅ Registro eliminado con éxito!");
                } else {
                    alert("No se pudo eliminar el registro seleccionado");
                }
            } catch (error) {
                console.log("Error al eliminar", error);
            }
        }
    };

    return (
        <div className="main-content-inner">
            <div className="card-panel">
                <h3 className="text-primary mb-4">
                    {editMode ? '📝 Editar Tipo de Servicio' : '🛠️ Registrar Nuevo Servicio'}
                </h3>
                <form onSubmit={handleGuardar}>
                    <div className="mb-3">
                        <label className="form-label fw-bold">Nombre del Servicio</label>
                        <input
                            type="text"
                            className="input-bs"
                            placeholder="Ej: Mantenimiento General"
                            value={nuevoTipo.nombre}
                            onChange={e => setNuevoTipo({ ...nuevoTipo, nombre: e.target.value })}
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Descripción</label>
                        <input
                            type="text"
                            className="input-bs"
                            placeholder="Ej: Cambio de aceite, revisión de frenos y ajuste de cadena"
                            value={nuevoTipo.descripcion}
                            onChange={e => setNuevoTipo({ ...nuevoTipo, descripcion: e.target.value })}
                            required
                        />
                    </div>

                    <div className="d-flex gap-2 mt-2">
                        <button type="submit" className="btn-bs w-100 btn-primary">
                            {editMode ? 'Actualizar Cambios' : 'Registrar Servicio'}
                        </button>
                        {editMode && (
                            <button
                                type="button"
                                className="btn-bs btn-danger"
                                onClick={() => {
                                    setEditMode(false);
                                    setNuevoTipo({ nombre: '', descripcion: '' });
                                }}
                            >
                                Cancelar
                            </button>
                        )}
                    </div>
                </form>
            </div>

            <div className="card-panel mt-4">
                {/* Contenedor responsivo con bordes limpios */}
                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    
                    {/* CABECERA DE LA TABLA (Distribución fija: 1.5fr para Nombre, 3fr para Descripción y 1fr para Acciones) */}
                    <div style={{ 
                        display: 'grid', 
                        gridTemplateColumns: '1.5fr 3fr 1fr', 
                        gap: '15px', 
                        alignItems: 'center', 
                        padding: '15px',
                        background: 'var(--header-table)',
                        color: 'var(--white)',
                        fontWeight: 'bold',
                        minWidth: '600px'
                    }}>
                        <div>Nombre de Servicio</div>
                        <div>Descripción</div>
                        <div style={{ display: 'flex', justifyContent: 'center' }}>Acciones</div>
                    </div>

                    {tipoServicios.length > 0 ? (
                        tipoServicios.map(t => (
                            /* FILAS CON ALINEACIÓN REJILLA PERFECTA */
                            <div key={t.idTipo} style={{ 
                                display: 'grid', 
                                gridTemplateColumns: '1.5fr 3fr 1fr', 
                                gap: '15px', 
                                alignItems: 'center', 
                                padding: '15px',
                                borderBottom: '1px solid #eee',
                                minWidth: '600px',
                                background: 'var(--white)'
                            }}>
                                <div className="fw-bold" style={{ color: 'var(--text-dark)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                                    {t.nombre}
                                </div>
                                <div style={{ whiteSpace: 'normal', display: '-webkit-box', WebkitLineClamp: '2', WebkitBoxOrient: 'vertical', overflow: 'hidden', textOverflow: 'ellipsis', color: '#4b5563' }} title={t.descripcion}>
                                    {t.descripcion}
                                </div>
                                <div className="text-center d-flex justify-content-center gap-2">
                                    <button className="btn-bs btn-primary btn-sm" style={{ padding: '6px 12px' }} onClick={() => iniciarEdicion(t)}>
                                        <i className="fa-solid fa-pen"></i>
                                    </button>
                                    <button className="btn-bs btn-danger btn-sm" style={{ padding: '6px 12px' }} onClick={() => handleEliminar(t.idTipo)}>
                                        <i className="fa-solid fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="p-4 text-center text-muted">No hay tipos de servicios registrados en el sistema.</div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default TipoServicio;