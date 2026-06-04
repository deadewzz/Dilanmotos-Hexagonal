import React, { useState, useEffect } from 'react';
import '../global.css';

const TipoServicio = () => {
    const [tipoServicios, setTipoServicios] = useState([]); 
    const [busqueda, setBusqueda] = useState(""); 
    const [nuevoTipo, setNuevoTipo] = useState({ nombre: "", descripcion: "" }); 
    const [editMode, setEditMode] = useState(false); 
    const [mensaje, setMensaje] = useState(''); // Estado unificado para notificaciones

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
            setMensaje("❌ No se pudieron cargar los servicios.");
        }
    };

    useEffect(() => {
        cargarServicios();
    }, []);

    // FUNCIÓN DE VALIDACIÓN LOGICA EN JS
    const validarFormulario = () => {
        // Expresión regular: Permite letras, espacios, acentos, Ñ, guiones (-) y diagonales (/)
        // Rechaza categóricamente cualquier dígito numérico (0-9)
        const regexNombreServicio = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s\/\-]+$/;

        if (!regexNombreServicio.test(nuevoTipo.nombre.trim())) {
            setMensaje("❌ El nombre del servicio no puede contener números ni caracteres especiales complejos.");
            return false;
        }

        if (nuevoTipo.descripcion.trim().length < 5) {
            setMensaje("❌ Por favor, agregue una descripción un poco más detallada.");
            return false;
        }

        return true;
    };

    const handleGuardar = async (e) => {
        e.preventDefault();
        setMensaje(''); // Limpiar mensajes previos

        // Ejecutar validaciones de negocio antes de disparar la petición HTTP
        if (!validarFormulario()) return;

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
                body: JSON.stringify({
                    ...nuevoTipo,
                    nombre: nuevoTipo.nombre.trim(),
                    descripcion: nuevoTipo.descripcion.trim()
                })
            });

            if (response.ok) {
                setMensaje(editMode ? "✅ ¡Servicio actualizado con éxito!" : "✅ ¡Servicio guardado con éxito!");
                setNuevoTipo({ nombre: "", descripcion: "" });
                setEditMode(false);
                cargarServicios();
            } else {
                const errorBackend = await response.text();
                setMensaje(`❌ Error al procesar la solicitud: ${errorBackend || 'Revisa los datos.'}`);
            }
        } catch (error) {
            console.log("Error al procesar la solicitud:", error);
            setMensaje("❌ Error de conexión con el servidor.");
        }
    };

    const iniciarEdicion = (servicio) => {
        setEditMode(true);
        setNuevoTipo(servicio); 
        setMensaje('');
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleEliminar = async (id) => {
        if (!id) {
            console.error("ID no recibido en handleEliminar");
            return;
        }

        if (window.confirm("¿Estás seguro de que deseas eliminar este tipo de servicio?")) {
            try {
                const response = await fetch(`${API_URL}/${id}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${token}` }
                });
               
                if (response.status === 204 || response.ok) {
                    setTipoServicios(tipoServicios.filter(ts => ts.idTipo !== id));
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
            <div className="card-panel">
                <h3 className="text-primary mb-4">
                    {editMode ? '✏️ Editar Tipo de Servicio' : '🛠️ Registrar Nuevo Servicio'}
                </h3>

                {/* Banner de Mensajes Integrado */}
                {mensaje && (
                    <div className="alert alert-info fw-bold mb-3">
                        {mensaje}
                    </div>
                )}

                <form onSubmit={handleGuardar}>
                    <div className="mb-3">
                        <label className="form-label fw-bold">Nombre del Servicio</label>
                        <input
                            type="text"
                            className="input-bs"
                            placeholder="Ej: Mantenimiento General"
                            value={nuevoTipo.nombre}
                            onChange={e => setNuevoTipo({ ...nuevoTipo, nombre: e.target.value })}
                            // Restricción Nativa HTML5: Letras, espacios, acentos, diagonales y guiones
                            pattern="^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s\/\-]+$"
                            title="El nombre del servicio solo debe contener letras, espacios, guiones o diagonales. No se permiten números."
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

                    {/* SECCIÓN DE BOTONES VERTICALES */}
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginTop: '15px' }}>
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
                                onClick={() => {
                                    setEditMode(false);
                                    setNuevoTipo({ nombre: '', descripcion: '' });
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

            <div className="card-panel mt-4">
                <div className="row align-items-center mb-3">
                    <div className="col-md-6">
                        <h4 className="text-muted m-0">📚 Servicios Registrados</h4>
                    </div>
                </div>

                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    
                    {/* CABECERA DE LA TABLA */}
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

                    {/* CUERPO DE LAS FILAS */}
                    {tipoServicios.length > 0 ? (
                        tipoServicios.map(t => (
                            <div key={t.idTipo} className="table-row-hover-effect" style={{ 
                                display: 'grid', 
                                gridTemplateColumns: '1.5fr 3fr 1fr', 
                                gap: '15px', 
                                alignItems: 'center', 
                                padding: '15px',
                                borderBottom: '1px solid #eee',
                                minWidth: '600px',
                                background: 'var(--white)',
                                transition: '0.2s'
                            }}>
                                <div className="fw-bold" style={{ color: 'var(--text-dark)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} title={t.nombre}>
                                    {t.nombre}
                                </div>
                                <div style={{ whiteSpace: 'normal', display: '-webkit-box', WebkitLineClamp: '2', WebkitBoxOrient: 'vertical', overflow: 'hidden', textOverflow: 'ellipsis', color: '#4b5563' }} title={t.descripcion}>
                                    {t.descripcion}
                                </div>
                                <div className="text-center d-flex justify-content-center gap-2">
                                    <button className="btn-bs btn-success btn-sm" style={{ padding: '6px 12px' }} onClick={() => iniciarEdicion(t)}>
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