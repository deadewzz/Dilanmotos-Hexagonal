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
    const [mensaje, setMensaje] = useState(''); // Estado unificado para notificaciones

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
                setMensaje("❌ Sesión expirada. Por favor, vuelva a iniciar sesión.");
            }
        } catch (error) {
            console.log("Error al conectar con la API:", error);
            setMensaje("❌ No se pudieron cargar los mecánicos.");
        }
    };

    useEffect(() => {
        cargarMecanicos();
    }, []);

    // FUNCIÓN DE VALIDACIÓN LOGICA EN JS
    const validarFormulario = () => {
        const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/;
        const regexTelefono = /^[0-9]+$/;

        // Validar Nombre (Sin números)
        if (!regexNombre.test(nuevoMecanico.nombre.trim())) {
            setMensaje("❌ El nombre completo no puede contener números ni caracteres especiales.");
            return false;
        }

        // Validar Teléfono (Solo números, si se proporciona)
        if (nuevoMecanico.telefono && nuevoMecanico.telefono.trim() !== "") {
            if (!regexTelefono.test(nuevoMecanico.telefono.trim())) {
                setMensaje("❌ El número de teléfono debe contener únicamente números.");
                return false;
            }
        }

        return true;
    };

    const handleGuardar = async (e) => {
        e.preventDefault();
        const token = localStorage.getItem('token');
        setMensaje(''); // Limpiar alertas anteriores

        // Ejecutar las validaciones de negocio antes de la petición
        if (!validarFormulario()) return;

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
                body: JSON.stringify({
                    ...nuevoMecanico,
                    nombre: nuevoMecanico.nombre.trim(),
                    especialidad: nuevoMecanico.especialidad.trim(),
                    telefono: nuevoMecanico.telefono ? nuevoMecanico.telefono.trim() : ""
                })
            });

            if (response.ok) {
                setMensaje(editMode ? "✅ ¡Mecánico actualizado con éxito!" : "✅ ¡Mecánico guardado con éxito!");
                setNuevoMecanico({ nombre: "",专ialidad: "", telefono: "" });
                setNuevoMecanico({ nombre: "", especialidad: "", telefono: "" });
                setEditMode(false);
                cargarMecanicos();
            } else {
                const errorBackend = await response.text();
                setMensaje(`❌ Error: ${errorBackend || 'Ocurrió un inconveniente al procesar la solicitud.'}`);
            }
        } catch (error) {
            console.log("Error en la solicitud:", error);
            setMensaje("❌ Error de conexión con el servidor.");
        }
    };

    const iniciarEdicion = (mecanico) => {
        setEditMode(true);
        setNuevoMecanico(mecanico);
        setMensaje('');
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleEliminar = async (id) => {
        if (!id) return;
        const token = localStorage.getItem('token');

        if (window.confirm("¿Estás seguro de que deseas eliminar este mecánico?")) {
            try {
                const response = await fetch(`${API_URL}/${id}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                
                if (response.ok) {
                    setMecanicos(mecanicos.filter(m => m.idMecanico !== id));
                    setMensaje("✅ Registro eliminado con éxito.");
                } else {
                    setMensaje("❌ No se pudo eliminar el registro seleccionado.");
                }
            } catch (error) {
                console.log("Error al eliminar", error);
                setMensaje("❌ Error al intentar conectar para eliminar.");
            }
        }
    };

    return (
        <div className="main-content-inner">
            {/* PANEL DEL FORMULARIO DE REGISTRO/EDICIÓN */}
            <div className="card-panel">
                <h3 className="text-primary mb-4">
                    {editMode ? '✏️ Editar Información de Mecánico' : '👨‍🔧 Registrar Nuevo Mecánico'}
                </h3>

                {/* Banner de Mensajes Integrado */}
                {mensaje && (
                    <div className="alert alert-info fw-bold mb-3">
                        {mensaje}
                    </div>
                )}

                <form onSubmit={handleGuardar}>
                    <div className="mb-3">
                        <label className="form-label fw-bold">Nombre Completo</label>
                        <input
                            type="text"
                            className="input-bs"
                            placeholder="Ej: Juan Carlos Pérez"
                            value={nuevoMecanico.nombre || ""} 
                            onChange={e => setNuevoMecanico({ ...nuevoMecanico, nombre: e.target.value })}
                            // Validación Nativa: Solo letras y espacios
                            pattern="^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$"
                            title="El nombre completo solo debe contener letras y espacios."
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
                            type="text" 
                            className="input-bs"
                            placeholder="Ej: 3001234567"
                            value={nuevoMecanico.telefono || ""}
                            onChange={e => setNuevoMecanico({ ...nuevoMecanico, telefono: e.target.value })}
                            // Validación Nativa: Solo dígitos del 0 al 9
                            pattern="[0-9]+"
                            title="El teléfono debe contener únicamente números."
                        />
                    </div>

                    {/* SECCIÓN DE BOTONES VERTICALES */}
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginTop: '15px' }}>
                        <button 
                            type="submit" 
                            className={`btn-bs w-100 ${editMode ? 'btn-success' : 'btn-success'}`}
                            style={{ padding: '12px', fontSize: '1rem' }}
                        >
                            {editMode ? 'Actualizar Cambios' : 'Registrar Mecánico'}
                        </button>
                        {editMode && (
                            <button
                                type="button"
                                className="btn-bs btn-danger w-100"
                                onClick={() => {
                                    setEditMode(false);
                                    setNuevoMecanico({ nombre: "", especialidad: "", telefono: "" });
                                    setMensaje('');
                                }}
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
                <div className="row align-items-center mb-3">
                    <div className="col-md-6">
                        <h4 className="text-muted m-0">📚 Personal Técnico Registrado</h4>
                    </div>
                </div>

                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    
                    {/* CABECERA CON GRID DISPUESTA EN PROPORCIONES EXACTAS */}
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
                            <div key={m.idMecanico} className="table-row-hover-effect" style={{ 
                                display: 'grid', 
                                gridTemplateColumns: '2fr 2fr 1.5fr 1fr', 
                                gap: '15px', 
                                alignItems: 'center', 
                                padding: '15px',
                                borderBottom: '1px solid #eee',
                                minWidth: '700px',
                                background: 'var(--white)',
                                transition: '0.2s'
                            }}>
                                <div className="fw-bold" style={{ color: 'var(--text-dark)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} title={m.nombre}>
                                    {m.nombre}
                                </div>
                                <div style={{ color: '#4b5563', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} title={m.especialidad}>
                                    {m.especialidad}
                                </div>
                                <div style={{ color: '#4b5563' }}>
                                    {m.telefono || <span className="text-muted italic">No asignado</span>}
                                </div>
                                <div className="text-center d-flex justify-content-center gap-2">
                                    <button className="btn-bs btn-success btn-sm" style={{ padding: '6px 12px' }} onClick={() => iniciarEdicion(m)}>
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