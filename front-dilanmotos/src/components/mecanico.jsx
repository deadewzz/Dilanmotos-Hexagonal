import React, { useState, useEffect } from 'react';
import '../global.css';

const Mecanico = () => {
    const [mecanicos, setMecanicos] = useState([]); 
    const [busqueda, setBusqueda] = useState(""); 
    const [nuevoMecanico, setNuevoMecanico] = useState({ 
        nombre: "", 
        especialidad: "", 
        telefono: "" 
    }); 
    const [editMode, setEditMode] = useState(false); 

    const API_URL = "http://localhost:8080/api/mecanicos";

    const cargarMecanicos = async () => {
        const token = localStorage.getItem('token'); 
        try {
            const url = busqueda ? `${API_URL}?search=${busqueda}` : API_URL;
            const response = await fetch(url, {
                headers: { 
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            if (response.ok) {
                const data = await response.json();
                setMecanicos(data);
            } else if (response.status === 401) {
                console.error("Sesión expirada o token inválido");
            }
        } catch (error) {
            console.log("Error al conectar con la API:", error);
        }
    };

    useEffect(() => {
        cargarMecanicos();
    }, []);

    const handleGuardar = async (e) => {
        e.preventDefault();
        const token = localStorage.getItem('token');
        const metodo = editMode ? 'PUT' : 'POST';
        const idActual = nuevoMecanico.idMecanico;
        const url = editMode ? `${API_URL}/${idActual}` : API_URL;

        try {
            const response = await fetch(url, {
                method: metodo,
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(nuevoMecanico)
            });

            if (response.ok) {
                alert(editMode ? "✅ ¡Mecánico actualizado con éxito!" : "✅ ¡Mecánico guardado con éxito!");
                setNuevoMecanico({ nombre: "", especialidad: "", telefono: "" });
                setEditMode(false);
                cargarMecanicos();
            } else {
                alert("❌ Ocurrió un inconveniente al procesar la solicitud.");
            }
        } catch (error) {
            console.log("Error en la solicitud:", error);
        }
    };

    const iniciarEdicion = (mecanico) => {
        setEditMode(true);
        setNuevoMecanico(mecanico);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleEliminar = async (id) => {
        if (!id) return;
        const token = localStorage.getItem('token');

        if (window.confirm("¿Estás seguro de eliminar este mecánico?")) {
            try {
                const response = await fetch(`${API_URL}/${id}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                
                if (response.ok) {
                    setMecanicos(mecanicos.filter(m => m.idMecanico !== id));
                    alert("✅ Registro eliminado con éxito.");
                } else {
                    alert("No se pudo eliminar el registro seleccionado.");
                }
            } catch (error) {
                console.log("Error al eliminar", error);
            }
        }
    };

    return (
        <div className="main-content-inner">
            {/* PANEL DEL FORMULARIO DE REGISTRO/EDICIÓN */}
            <div className="card-panel">
                <h3 className="text-primary mb-4">
                    {editMode ? '📝 Editar Información de Mecánico' : '👨‍🔧 Registrar Nuevo Mecánico'}
                </h3>
                <form onSubmit={handleGuardar}>
                    <div className="mb-3">
                        <label className="form-label fw-bold">Nombre Completo</label>
                        <input
                            type="text"
                            className="input-bs"
                            placeholder="Ej: Juan Carlos Pérez"
                            value={nuevoMecanico.nombre || ""} 
                            onChange={e => setNuevoMecanico({ ...nuevoMecanico, nombre: e.target.value })}
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Especialidad técnica</label>
                        <input
                            type="text"
                            className="input-bs"
                            placeholder="Ej: Inyección Electrónica / Motores 4T"
                            value={nuevoMecanico.especialidad || ""}
                            onChange={e => setNuevoMecanico({ ...nuevoMecanico, especialidad: e.target.value })}
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Número de Teléfono</label>
                        <input
                            type="tel"
                            className="input-bs"
                            placeholder="Ej: 3001234567"
                            value={nuevoMecanico.telefono || ""}
                            onChange={e => setNuevoMecanico({ ...nuevoMecanico, telefono: e.target.value })}
                        />
                    </div>

                    <div className="d-flex gap-2 mt-2">
                        <button type="submit" className="btn-bs w-100 btn-primary">
                            {editMode ? 'Actualizar Cambios' : 'Registrar Mecánico'}
                        </button>
                        {editMode && (
                            <button
                                type="button"
                                className="btn-bs btn-danger"
                                onClick={() => {
                                    setEditMode(false);
                                    setNuevoMecanico({ nombre: "", especialidad: "", telefono: "" });
                                }}
                            >
                                Cancelar
                            </button>
                        )}
                    </div>
                </form>
            </div>

            {/* PANEL DE LA LISTA / TABLA DE REGISTROS */}
            <div className="card-panel mt-4">
                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    
                    {/* CABECERA CON GRID DISPUESTA EN PROPORCIONES EXACTAS: 2fr | 2fr | 1.5fr | 1fr */}
                    <div style={{ 
                        display: 'grid', 
                        gridTemplateColumns: '2fr 2fr 1.5fr 1fr', 
                        gap: '15px', 
                        alignItems: 'center', 
                        padding: '15px',
                        background: 'var(--header-table)',
                        color: 'var(--white)',
                        fontWeight: 'bold',
                        minWidth: '700px'
                    }}>
                        <div>Nombre</div>
                        <div>Especialidad</div>
                        <div>Teléfono</div>
                        <div style={{ display: 'flex', justifyContent: 'center' }}>Acciones</div>
                    </div>

                    {/* CUERPO DINÁMICO DE LA TABLA */}
                    {mecanicos.length > 0 ? (
                        mecanicos.map(m => (
                            <div key={m.idMecanico} style={{ 
                                display: 'grid', 
                                gridTemplateColumns: '2fr 2fr 1.5fr 1fr', 
                                gap: '15px', 
                                alignItems: 'center', 
                                padding: '15px',
                                borderBottom: '1px solid #eee',
                                minWidth: '700px',
                                background: 'var(--white)'
                            }}>
                                <div className="fw-bold" style={{ color: 'var(--text-dark)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                                    {m.nombre}
                                </div>
                                <div style={{ color: '#4b5563', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                                    {m.especialidad}
                                </div>
                                <div style={{ color: '#4b5563' }}>
                                    {m.telefono || <span className="text-muted italic">No asignado</span>}
                                </div>
                                <div className="text-center d-flex justify-content-center gap-2">
                                    <button className="btn-bs btn-primary btn-sm" style={{ padding: '6px 12px' }} onClick={() => iniciarEdicion(m)}>
                                        <i className="fa-solid fa-pen"></i>
                                    </button>
                                    <button className="btn-bs btn-danger btn-sm" style={{ padding: '6px 12px' }} onClick={() => handleEliminar(m.idMecanico)}>
                                        <i className="fa-solid fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="p-4 text-center text-muted">No se encontraron mecánicos registrados en el sistema.</div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Mecanico;