import React, { useEffect, useState } from 'react';
import '../global.css';
import { API_BASE_URL } from '../api';

// ⚠️ AJUSTA ESTA URL AL ENDPOINT EXACTO DE TU BACKEND PARA MARCAS DE PRODUCTO
const API_MARCAS = `${API_BASE_URL}/api/marcas-producto`; 
const API_CATEGORIAS = `${API_BASE_URL}/api/categorias`;

const MarcaProducto = () => {
    const token = localStorage.getItem('token');

    const [marcas, setMarcas] = useState([]);
    const [categorias, setCategorias] = useState([]);
    
    const [formData, setFormData] = useState({
        idMarcaProducto: null,
        nombre: '',
        categoriaId: ''
    });

    const [editMode, setEditMode] = useState(false);
    const [mensaje, setMensaje] = useState('');

    useEffect(() => {
        cargarCategorias();
        cargarMarcas();
    }, []);

    // 1. Cargar Categorías
    const cargarCategorias = async () => {
        try {
            const response = await fetch(API_CATEGORIAS, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                setCategorias(data);
            }
        } catch (error) {
            console.error("Error al cargar categorías:", error);
        }
    };

    // 2. Cargar Marcas de Productos
    const cargarMarcas = async () => {
        try {
            const response = await fetch(API_MARCAS, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                console.log("Respuesta de Marcas Backend:", data); // 👈 Revisa esto en F12
                setMarcas(data);
            } else {
                throw new Error('Error al obtener marcas de productos');
            }
        } catch (error) {
            console.error(error);
            setMensaje('❌ No se pudieron cargar las marcas de productos.');
        }
    };

    // Mapeo flexible para obtener la categoría según la estructura del JSON
    const obtenerNombreCategoria = (marca) => {
    if (!marca || !marca.idCategoria) return 'Sin Categoría';
    
    // Cruza el idCategoria con la lista de categorias cargadas desde API_CATEGORIAS
    const catEncontrada = categorias.find(
        c => Number(c.idCategoria || c.id) === Number(marca.idCategoria)
    );

    return catEncontrada ? catEncontrada.nombre : 'Sin Categoría';
};

    const validarFormulario = () => {
        if (!formData.categoriaId) {
            setMensaje("❌ Debes seleccionar una categoría asociada.");
            return false;
        }

        if (formData.nombre.trim().length < 2) {
            setMensaje("❌ El nombre de la marca debe tener al menos 2 caracteres.");
            return false;
        }

        return true;
    };

    const guardar = async (e) => {
    e.preventDefault();
    setMensaje('');

    if (!validarFormulario()) return;

    const metodo = editMode ? 'PUT' : 'POST';
    const url = editMode ? `${API_MARCAS}/${formData.idMarcaProducto}` : API_MARCAS;

    // 💡 PAYLOAD MATCH EXACTO CON MARCAPRODUCTO.JAVA
    const payload = {
        idMarcaProducto: editMode ? formData.idMarcaProducto : null,
        nombre: formData.nombre.trim(),
        idCategoria: Number(formData.categoriaId) // 👈 Directo como Integer
    };

    try {
        const response = await fetch(url, {
            method: metodo,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            setMensaje(editMode ? "✅ ¡Marca de producto actualizada con éxito!" : "✅ ¡Marca de producto guardada con éxito!");
            resetForm();
            cargarMarcas();
        } else {
            console.error("Status Backend:", response.status);
            setMensaje(`❌ Error ${response.status}: Ocurrió un problema en el servidor.`);
        }
    } catch (error) {
        console.error("Error al guardar marca:", error);
        setMensaje("❌ Error de conexión con el servidor.");
    }
};

    const iniciarEdicion = (marca) => {
    setFormData({
        idMarcaProducto: marca.idMarcaProducto,
        nombre: marca.nombre,
        categoriaId: marca.idCategoria || ''
    });
    setEditMode(true);
    setMensaje('');
    window.scrollTo({ top: 0, behavior: 'smooth' });
};

    const eliminar = async (id) => {
        if (!id) return;

        if (window.confirm("¿Estás seguro de eliminar esta marca de producto?")) {
            try {
                const response = await fetch(`${API_MARCAS}/${id}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                if (response.status === 204 || response.ok) {
                    setMarcas(marcas.filter(m => (m.idMarcaProducto || m.idMarca || m.id) !== id));
                    setMensaje("✅ Registro eliminado con éxito.");
                } else {
                    setMensaje("❌ No se pudo eliminar la marca.");
                }
            } catch (error) {
                console.error("Error al eliminar marca:", error);
                setMensaje("❌ Error de conexión.");
            }
        }
    };

    const resetForm = () => {
        setFormData({ idMarcaProducto: null, nombre: '', categoriaId: '' });
        setEditMode(false);
        setMensaje('');
    };

    const gridStyle = {
        display: 'grid',
        gridTemplateColumns: '1fr 2fr 2fr 1.5fr',
        gap: '15px',
        alignItems: 'center',
        padding: '15px',
        minWidth: '650px'
    };

    return (
        <div className="main-content-inner">
            {/* FORMULARIO */}
            <div className="card-panel">
                <h3 className="text-primary mb-4">
                    {editMode ? '📝 Editar Marca de Producto' : '🏷️ Gestión de Marcas de Productos'}
                </h3>

                {mensaje && (
                    <div className="alert alert-info fw-bold mb-3">
                        {mensaje}
                    </div>
                )}

                <form onSubmit={guardar}>
                    <div className="mb-3">
                        <label className="form-label fw-bold">Categoría de Producto</label>
                        <select
                            className="input-bs"
                            value={formData.categoriaId}
                            onChange={(e) => setFormData({ ...formData, categoriaId: e.target.value })}
                            required
                        >
                            <option value="">Seleccione Categoría...</option>
                            {categorias.map(cat => (
                                <option key={cat.idCategoria || cat.id} value={cat.idCategoria || cat.id}>
                                    {cat.nombre}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Nombre Marca del Producto</label>
                        <input
                            className="input-bs"
                            type="text"
                            placeholder="Ej: Motul, Mobil, Michelin..."
                            value={formData.nombre}
                            onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                            required
                        />
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }} className="mt-4">
                        <button
                            type="submit"
                            className="btn-bs btn-success w-100"
                            style={{ padding: '12px', fontSize: '1rem' }}
                        >
                            {editMode ? 'Actualizar Cambios' : 'Guardar Marca de Producto'}
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

            {/* TABLA DE RESULTADOS */}
            <div className="card-panel mt-4">
                <h4 className="mb-4">📋 Listado de Marcas por Categoría de Producto</h4>

                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    <div style={{ ...gridStyle, background: 'var(--header-table)', color: 'var(--white)', fontWeight: 'bold' }}>
                        <div>ID</div>
                        <div>Categoría</div>
                        <div>Nombre Marca Producto</div>
                        <div style={{ textAlign: 'center' }}>Acciones</div>
                    </div>

                    {marcas.length === 0 ? (
                        <div className="p-4 text-center text-muted">No hay marcas de productos registradas.</div>
                    ) : (
                        marcas.map(m => {
                            const idReg = m.idMarcaProducto || m.idMarca || m.id;
                            const nombreCategoria = obtenerNombreCategoria(m);
                            return (
                                <div
                                    key={idReg}
                                    className="table-row-hover-effect"
                                    style={{ ...gridStyle, borderBottom: '1px solid #eee', background: 'var(--white)' }}
                                >
                                    <div className="fw-bold" style={{ color: 'var(--text-dark)' }}>{idReg}</div>
                                    <div style={{ color: '#4b5563', fontWeight: '500' }}>{nombreCategoria}</div>
                                    <div style={{ color: '#4b5563', fontWeight: '600' }}>{m.nombre}</div>
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
                                            onClick={() => eliminar(idReg)}
                                        >
                                            <i className="fa-solid fa-trash"></i>
                                        </button>
                                    </div>
                                </div>
                            );
                        })
                    )}
                </div>
            </div>
        </div>
    );
};

export default MarcaProducto;