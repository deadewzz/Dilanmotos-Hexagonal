import React, { useEffect, useState } from 'react';
import '../global.css';

const API_URL = 'http://localhost:8080/api/categorias';

const Categoria = () => {
    const token = localStorage.getItem('token');

    const [categorias, setCategorias] = useState([]);
    const [nueva, setNueva] = useState({ nombre: '' });
    const [editMode, setEditMode] = useState(false);
    const [mensaje, setMensaje] = useState('');
    
    useEffect(() => { 
        cargarCategorias(); 
    }, []);

    const cargarCategorias = async () => {
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
                throw new Error('Error al obtener las categorias');
            }   
            const data = await response.json();
            setCategorias(data);
        } catch (error) {
            console.error(error);
            setMensaje('❌ No se pudieron cargar las categorías.');
        }
    };

    // FUNCIÓN DE VALIDACIÓN LÓGICA EN JS
    const validarFormulario = () => {
        // Expresión regular: Permite letras, espacios, acentos, Ñ, guiones y barras diagonales (/)
        // Bloquea cualquier dígito numérico (0-9)
        const regexNombreCategoria = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s\/\-]+$/;

        if (!regexNombreCategoria.test(nueva.nombre.trim())) {
            setMensaje("❌ El nombre de la categoría no puede contener números ni caracteres especiales complejos.");
            return false;
        }

        if (nueva.nombre.trim().length < 3) {
            setMensaje("❌ El nombre de la categoría debe tener al menos 3 caracteres.");
            return false;
        }

        return true;
    };

    const guardar = async (e) => {
        e.preventDefault();
        setMensaje(''); // Limpiar alertas anteriores

        // Ejecutar las validaciones de negocio antes de disparar la petición
        if (!validarFormulario()) return;

        const metodo = editMode ? 'PUT' : 'POST';
        const idActual = nueva.idCategoria;
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
                    nombre: nueva.nombre.trim() // Desinfección de espacios laterales
                })
            });

            if (response.ok) {
                setMensaje(editMode ? '✅ ¡Categoría actualizada con éxito!' : '✅ ¡Categoría creada con éxito!');
                resetForm();
                cargarCategorias();
            } else {
                const errorBackend = await response.text();
                setMensaje(`❌ Error: ${errorBackend || 'Ocurrió un inconveniente al procesar la solicitud.'}`);
            }
        } catch (error) {
            console.error(error);
            setMensaje('❌ Error de conexión con el servidor.');
        }
    };

    const iniciarEdicion = (categoria) => {
        setNueva(categoria);
        setEditMode(true);
        setMensaje('');
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const eliminar = async (id) => {
        if (!id) return;

        if (window.confirm('¿Estás seguro de eliminar esta categoría?')) {
            try {
                const response = await fetch(`${API_URL}/${id}`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                if (response.ok) {
                    setCategorias(categorias.filter(c => c.idCategoria !== id));
                    setMensaje('✅ Registro eliminado con éxito.');
                } else {
                    setMensaje('❌ No se pudo eliminar el registro seleccionado debido a restricciones de integridad.');
                }
            } catch (error) {
                console.error(error);
                setMensaje('❌ Error al intentar conectar para eliminar.');
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
                    {editMode ? '📝 Editar Categoría' : '📁 Nueva Categoría'}
                </h3>
                
                {/* Banner de Mensajes Integrado */}
                {mensaje && (
                    <div className="alert alert-info fw-bold mb-3">
                        {mensaje}
                    </div>
                )}
                
                <form onSubmit={guardar}>
                    <div className="mb-3">
                        <label className="form-label fw-bold">Nombre de la Categoría</label>
                        <input
                            className="input-bs"
                            type="text"
                            placeholder="Ej: Repuestos / Accesorios / Motor"
                            value={nueva.nombre}
                            onChange={(e) => setNueva({ ...nueva, nombre: e.target.value })}
                            // Validación Nativa: Letras, espacios, diagonales y guiones (Sin Números)
                            pattern="^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s\/\-]+$"
                            title="El nombre de la categoría solo debe contener letras, espacios, guiones o barras diagonales. No se permiten números."
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
                            {editMode ? 'Actualizar Cambios' : 'Crear Categoría'}
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
                <h4 className="mb-4">📚 Listado General de Categorías</h4>
                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    
                    {/* CABECERA CON GRID */}
                    <div style={{ 
                        ...gridStyle,
                        background: 'var(--header-table)',
                        color: 'var(--white)',
                        fontWeight: 'bold'
                    }}>
                        <div>ID</div>
                        <div>Nombre de Categoría</div>
                        <div style={{ display: 'flex', justifyContent: 'center' }}>Acciones</div>
                    </div>

                    {/* CUERPO DINÁMICO CON TRANSICIONES Y EFECTO HOVER */}
                    {categorias.length > 0 ? (
                        categorias.map(c => (
                            <div 
                                key={c.idCategoria} 
                                className="table-row-hover-effect"
                                style={{ 
                                    ...gridStyle,
                                    borderBottom: '1px solid #eee',
                                    background: 'var(--white)',
                                    transition: '0.2s'
                                }}
                            >
                                <div className="fw-bold" style={{ color: 'var(--text-dark)' }}>
                                    {c.idCategoria}
                                </div>
                                <div style={{ color: '#4b5563', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} title={c.nombre}>
                                    {c.nombre}
                                </div>
                                <div className="text-center d-flex justify-content-center gap-2">
                                    <button 
                                        className="btn-bs btn-success btn-sm" 
                                        style={{ padding: '6px 12px' }} 
                                        onClick={() => iniciarEdicion(c)}
                                    >
                                        <i className="fa-solid fa-pen"></i>
                                    </button>
                                    <button 
                                        className="btn-bs btn-danger btn-sm" 
                                        style={{ padding: '6px 12px' }} 
                                        onClick={() => eliminar(c.idCategoria)}
                                    >
                                        <i className="fa-solid fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="p-4 text-center text-muted">No se encontraron categorías registradas en el sistema.</div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Categoria;