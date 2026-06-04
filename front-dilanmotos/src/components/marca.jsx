import React, { useEffect, useState } from 'react';
import '../global.css';

const API_URL = 'http://localhost:8080/api/marcas';

const Marca = () => {
    const token = localStorage.getItem('token');

    const [marcas, setMarcas] = useState([]);
    const [nueva, setNueva] = useState({ nombre: '' });
    const [editMode, setEditMode] = useState(false);
    const [mensaje, setMensaje] = useState('');
    
    useEffect(() => { 
        cargarMarcas(); 
    }, []);

    const cargarMarcas = async () => {
        try {
            const response = await fetch(API_URL, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            if (!response.ok) {
                console.log("STATUS:", response.status);
                throw new Error('Error al obtener las marcas');
            }   
            const data = await response.json();
            setMarcas(data);
        } catch (error) {
            console.error(error);
            setMensaje('❌ No se pudieron cargar las marcas.');
        }
    };

    // FUNCIÓN DE VALIDACIÓN LOGICA EN JS
    const validarFormulario = () => {
        // Expresión regular: Permite letras (con acentos y Ñ), espacios, ampersand (&), puntos (.) y guiones (-)
        // Bloquea por completo cualquier tipo de número (0-9)
        const regexNombreMarca = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s\&\.\-]+$/;

        if (!regexNombreMarca.test(nueva.nombre.trim())) {
            setMensaje("❌ El nombre de la marca no puede contener números ni caracteres especiales complejos.");
            return false;
        }

        if (nueva.nombre.trim().length < 2) {
            setMensaje("❌ El nombre de la marca debe tener al menos 2 caracteres.");
            return false;
        }

        return true;
    };

    const guardar = async (e) => {
        e.preventDefault();
        setMensaje(''); // Limpiar mensajes previos

        // Ejecutar la validación de negocio antes de disparar el Fetch
        if (!validarFormulario()) return;

        const metodo = editMode ? 'PUT' : 'POST';
        const idActual = nueva.idMarca;
        const url = editMode ? `${API_URL}/${idActual}` : API_URL;

        try {
            const response = await fetch(url, {
                method: metodo,
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    ...nueva,
                    nombre: nueva.nombre.trim() // Limpieza de espacios extra
                })
            });

            if (response.ok) {
                setMensaje(editMode ? "✅ ¡Marca actualizada con éxito!" : "✅ ¡Marca guardada con éxito!");
                resetForm();
                cargarMarcas();
            } else {
                setMensaje("❌ Ocurrió un inconveniente al procesar la solicitud.");
            }
        } catch (error) {
            console.log("Error al procesar la solicitud:", error);
            setMensaje("❌ Error de conexión con el servidor.");
        }
    };

    const iniciarEdicion = (marca) => {
        setNueva(marca);
        setEditMode(true);
        setMensaje(''); 
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const eliminar = async (id) => {
        if (!id) {
            console.error("ID no recibido en handleEliminar");
            return;
        }

        if (window.confirm("¿Estás seguro de eliminar esta marca?")) {
            try {
                const response = await fetch(`${API_URL}/${id}`, {
                    method: 'DELETE',
                    headers: { 
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    }
                });
               
                if (response.status === 204 || response.ok) {
                    setMarcas(marcas.filter(m => m.idMarca !== id));
                    setMensaje("✅ Registro eliminado con éxito.");
                } else {
                    setMensaje("❌ No se pudo eliminar el registro seleccionado debido a restricciones de integridad.");
                }
            } catch (error) {
                console.log("Error al eliminar", error);
                setMensaje("❌ Error al intentar conectar para eliminar.");
            }
        }
    };

    const resetForm = () => {
        setNueva({ nombre: '' });
        setEditMode(false);
        setMensaje('');
    };

    const gridStyle = {
        display: 'grid',
        gridTemplateColumns: '1fr 4fr 1.5fr',
        gap: '15px',
        alignItems: 'center',
        padding: '15px',
        minWidth: '600px'
    };

    return (
        <div className="main-content-inner">

            {/* PANEL DEL FORMULARIO DE REGISTRO/EDICIÓN */}
            <div className="card-panel">
                <h3 className="text-primary mb-4">
                    {editMode ? '📝 Editar Marca' : '📁 Nueva Marca'}
                </h3>

                {/* Banner de Mensajes Integrado */}
                {mensaje && (
                    <div className="alert alert-info fw-bold mb-3">
                        {mensaje}
                    </div>
                )}

                <form onSubmit={guardar}>
                    <div className="mb-3">
                        <label className="form-label fw-bold">
                            Nombre de la Marca
                        </label>
                        <input
                            className="input-bs"
                            type="text"
                            placeholder="Nueva marca"
                            value={nueva.nombre}
                            onChange={(e) => setNueva({ ...nueva, nombre: e.target.value })}
                            // Validación Nativa: Letras, espacios, &, . y - (Sin Números)
                            pattern="^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s\&\.\-]+$"
                            title="El nombre de la marca solo debe contener letras, espacios, guiones, puntos o el símbolo &. No se permiten números."
                            required
                        />
                    </div>

                    {/* BLOQUE DE ACCIONES VERTICAL UNIFICADO */}
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }} className="mt-4">
                        <button 
                            type="submit" 
                            className={`btn-bs w-100 ${editMode ? 'btn-success' : 'btn-success'}`}
                            style={{ padding: '12px', fontSize: '1rem' }}
                        >
                            {editMode ? 'Actualizar Cambios' : 'Crear Marca'}
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
                <h4 className="mb-4">🔖 Listado General de Marcas</h4>

                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    
                    {/* CABECERA CON GRID */}
                    <div style={{
                        ...gridStyle,
                        background: 'var(--header-table)',
                        color: 'var(--white)',
                        fontWeight: 'bold'
                    }}>
                        <div>ID</div>
                        <div>Nombre de la Marca</div>
                        <div style={{ display: 'flex', justifyContent: 'center' }}>Acciones</div>
                    </div>

                    {/* CUERPO DINÁMICO CON TRANSICIONES Y EFECTO HOVER */}
                    {marcas.length === 0 ? (
                        <div className="p-4 text-center text-muted">No se encontraron marcas registradas en el sistema.</div>
                    ) : (
                        marcas.map(m => (
                            <div
                                key={m.idMarca}
                                className="table-row-hover-effect"
                                style={{
                                    ...gridStyle,
                                    borderBottom: '1px solid #eee',
                                    background: 'var(--white)',
                                    transition: '0.2s'
                                }}
                            >
                                <div className="fw-bold" style={{ color: 'var(--text-dark)' }}>
                                    {m.idMarca}
                                </div>
                                <div style={{ color: '#4b5563', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} title={m.nombre}>
                                    {m.nombre}
                                </div>
                                <div className="text-center d-flex justify-content-center gap-2">
                                    <button
                                        className="btn-bs btn-success btn-sm"
                                        style={{ padding: '6px 12px' }}
                                        onClick={() => iniciarEdicion(m)}
                                    >
                                        <i className="fa-solid fa-pen"></i>
                                    </button>
                                    <button
                                        className="btn-bs btn-danger btn-sm"
                                        style={{ padding: '6px 12px' }}
                                        onClick={() => eliminar(m.idMarca)}
                                    >
                                        <i className="fa-solid fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
};

export default Marca;