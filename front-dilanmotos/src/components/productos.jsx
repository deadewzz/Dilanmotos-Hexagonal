import React, { useEffect, useState } from 'react';
import '../global.css';

const BASE_URL = 'http://localhost:8080/api';

const Productos = () => {
    const token = localStorage.getItem('token');

    // Estados de listas
    const [productos, setProductos] = useState([]);
    const [categorias, setCategorias] = useState([]);
    const [marcasProducto, setMarcasProducto] = useState([]);
    const [marcasFiltradas, setMarcasFiltradas] = useState([]);

    // Estado del formulario (Incluye Stock)
    const [productoForm, setProductoForm] = useState({
        idProducto: null,
        nombre: '',
        precio: '',
        descripcion: '',
        imagenUrl: '',
        idCategoria: '',
        idMarca: '',
        stock: 0,
        disponible: true
    });

    const [editMode, setEditMode] = useState(false);
    const [mensaje, setMensaje] = useState('');

    // Fetch con fallback
    const fetchConFallback = async (endpoints) => {
        const headersWithAuth = token 
            ? { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' } 
            : { 'Content-Type': 'application/json' };
        const headersNoAuth = { 'Content-Type': 'application/json' };

        for (const endpoint of endpoints) {
            const url = `${BASE_URL}${endpoint}`;
            
            try {
                const res = await fetch(url, { headers: headersWithAuth });
                if (res.ok) {
                    const data = await res.json();
                    return Array.isArray(data) ? data : (data.content || []);
                }
            } catch (e) {
                console.warn(`Falló con auth en ${url}:`, e);
            }

            try {
                const res = await fetch(url, { headers: headersNoAuth });
                if (res.ok) {
                    const data = await res.json();
                    return Array.isArray(data) ? data : (data.content || []);
                }
            } catch (e) {
                console.warn(`Falló pública en ${url}:`, e);
            }
        }
        return null;
    };

    useEffect(() => {
        cargarTodo();
    }, []);

    const cargarTodo = async () => {
        setMensaje('');

        // 1. Cargar Categorías de Producto
        const resCat = await fetchConFallback(['/categorias-producto', '/categoria-producto', '/categorias']);
        if (resCat) setCategorias(resCat);

        // 2. Cargar MARCAS DE PRODUCTO (No las marcas de motos)
        const resMarcas = await fetchConFallback(['/marcas-producto', '/marca-producto', '/marcas-productos']);
        if (resMarcas) {
            setMarcasProducto(resMarcas);
        }

        // 3. Cargar Productos
        const resProds = await fetchConFallback(['/productos', '/producto']);
        if (resProds) {
            setProductos(resProds);
        } else {
            setMensaje('❌ No se pudieron cargar los productos registrados.');
        }
    };

    // Manejador del cambio de categoría y filtrado dinámico de marcas
    const handleCategoriaChange = (e) => {
        const idCat = e.target.value;
        
        // Al cambiar de categoría, reiniciamos la marca seleccionada
        setProductoForm(prev => ({
            ...prev,
            idCategoria: idCat,
            idMarca: '' 
        }));

        if (!idCat) {
            setMarcasFiltradas([]);
        } else {
            const idCatNum = Number(idCat);
            const filtradas = marcasProducto.filter(m => {
                const catId = m.idCategoria || m.categoria?.idCategoria || m.categoriaId;
                return Number(catId) === idCatNum;
            });
            setMarcasFiltradas(filtradas);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setProductoForm(prev => ({ ...prev, [name]: value }));
    };

    const guardarProducto = async (e) => {
        e.preventDefault();
        setMensaje('');

        if (!productoForm.nombre.trim()) return setMensaje('❌ Ingrese un nombre de producto.');
        if (!productoForm.precio || Number(productoForm.precio) <= 0) return setMensaje('❌ Ingrese un precio válido.');
        if (!productoForm.idCategoria) return setMensaje('❌ Seleccione primero una categoría.');
        if (!productoForm.idMarca) return setMensaje('❌ Seleccione una marca de producto.');
        if (productoForm.stock === '' || Number(productoForm.stock) < 0) return setMensaje('❌ Ingrese una cantidad de stock válida.');

        const endpoint = editMode ? `/productos/${productoForm.idProducto}` : '/productos';
        const url = `${BASE_URL}${endpoint}`;
        const metodo = editMode ? 'PUT' : 'POST';

        const payload = {
            idProducto: productoForm.idProducto ? Number(productoForm.idProducto) : null,
            idCategoria: Number(productoForm.idCategoria),
            idMarca: Number(productoForm.idMarca),
            nombre: productoForm.nombre.trim(),
            descripcion: productoForm.descripcion.trim(),
            precio: parseFloat(productoForm.precio),
            imagenUrl: productoForm.imagenUrl.trim(),
            stock: Number(productoForm.stock),
            disponible: Number(productoForm.stock) > 0
        };

        try {
            const response = await fetch(url, {
                method: metodo,
                headers: {
                    'Content-Type': 'application/json',
                    ...(token && { 'Authorization': `Bearer ${token}` })
                },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                setMensaje(editMode ? '✅ ¡Producto actualizado con éxito!' : '✅ ¡Producto registrado con éxito!');
                resetForm();
                cargarTodo();
            } else {
                setMensaje(`❌ Error (${response.status}): No se pudo guardar el producto.`);
            }
        } catch (error) {
            console.error('Error al guardar:', error);
            setMensaje('❌ Error de conexión al guardar el producto.');
        }
    };

    const iniciarEdicion = (p) => {
        const catId = p.idCategoria || p.categoria?.idCategoria || '';
        
        // Al editar, también filtramos las marcas según la categoría del producto
        if (catId) {
            const filtradas = marcasProducto.filter(m => {
                const cId = m.idCategoria || m.categoria?.idCategoria || m.categoriaId;
                return Number(cId) === Number(catId);
            });
            setMarcasFiltradas(filtradas);
        }

        setProductoForm({
            idProducto: p.idProducto || p.id,
            nombre: p.nombre || '',
            precio: p.precio || '',
            descripcion: p.descripcion || '',
            imagenUrl: p.imagenUrl || '',
            idCategoria: catId,
            idMarca: p.idMarca || p.marca?.idMarca || '',
            stock: p.stock ?? 0,
            disponible: p.disponible ?? true
        });
        setEditMode(true);
        setMensaje('');
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const eliminarProducto = async (id) => {
        if (!id) return;
        if (window.confirm('¿Desea eliminar este producto?')) {
            try {
                const response = await fetch(`${BASE_URL}/productos/${id}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                        ...(token && { 'Authorization': `Bearer ${token}` })
                    }
                });

                if (response.ok || response.status === 204) {
                    setMensaje('✅ Producto eliminado.');
                    cargarTodo();
                } else {
                    setMensaje('❌ No se pudo eliminar el producto.');
                }
            } catch (error) {
                console.error(error);
                setMensaje('❌ Error al eliminar el producto.');
            }
        }
    };

    const resetForm = () => {
        setProductoForm({
            idProducto: null,
            nombre: '',
            precio: '',
            descripcion: '',
            imagenUrl: '',
            idCategoria: '',
            idMarca: '',
            stock: 0,
            disponible: true
        });
        setMarcasFiltradas([]);
        setEditMode(false);
    };

    // Estructura de la tabla (Incluye columna Stock)
    const gridStyle = {
        display: 'grid',
        gridTemplateColumns: '0.6fr 2fr 1fr 0.8fr 1.3fr 1.3fr 1fr',
        gap: '12px',
        alignItems: 'center',
        padding: '14px',
        minWidth: '900px'
    };

    return (
        <div className="main-content-inner">
            <div className="card-panel">
                <h3 className="text-primary mb-4">
                    📦 {editMode ? 'Editar Producto' : 'Registro de Productos'}
                </h3>

                {mensaje && (
                    <div className="alert alert-info fw-bold mb-3">
                        {mensaje}
                    </div>
                )}

                <form onSubmit={guardarProducto}>
                    <div className="mb-3">
                        <label className="form-label fw-bold">Nombre del Producto</label>
                        <input
                            type="text"
                            name="nombre"
                            className="input-bs"
                            placeholder="Ej: Kit de Arrastre, Aceite 10W40..."
                            value={productoForm.nombre}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="row mb-3" style={{ display: 'flex', gap: '15px' }}>
                        <div style={{ flex: 1 }}>
                            <label className="form-label fw-bold">Precio ($)</label>
                            <input
                                type="number"
                                step="0.01"
                                name="precio"
                                className="input-bs"
                                placeholder="0.00"
                                value={productoForm.precio}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div style={{ flex: 1 }}>
                            <label className="form-label fw-bold">Stock (Cantidad)</label>
                            <input
                                type="number"
                                name="stock"
                                className="input-bs"
                                placeholder="Ej: 15"
                                value={productoForm.stock}
                                onChange={handleChange}
                                min="0"
                                required
                            />
                        </div>
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Descripción</label>
                        <textarea
                            name="descripcion"
                            className="input-bs"
                            rows="3"
                            placeholder="Detalles del producto..."
                            value={productoForm.descripcion}
                            onChange={handleChange}
                        ></textarea>
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">URL de la imagen</label>
                        <textarea
                            name="imagenUrl"
                            className="input-bs"
                            rows="2"
                            placeholder="ingresa la url..."
                            value={productoForm.imagenUrl}
                            onChange={handleChange}
                        ></textarea>
                    </div>

                    {/* SELECTOR 1: CATEGORÍA */}
                    <div className="mb-3">
                        <label className="form-label fw-bold">1. Categoría</label>
                        <select
                            name="idCategoria"
                            className="input-bs"
                            value={productoForm.idCategoria}
                            onChange={handleCategoriaChange}
                            required
                        >
                            <option value="">Seleccione categoría...</option>
                            {categorias.map(cat => {
                                const idCat = cat.idCategoria || cat.id;
                                return (
                                    <option key={idCat} value={idCat}>
                                        {cat.nombre}
                                    </option>
                                );
                            })}
                        </select>
                    </div>

                    {/* SELECTOR 2: MARCA (DESHABILITADO SI NO HAY CATEGORÍA SELECCIONADA) */}
                    <div className="mb-3">
                        <label className="form-label fw-bold">
                            2. Marca del Producto {!productoForm.idCategoria && '(Seleccione una categoría primero)'}
                        </label>
                        <select
                            name="idMarca"
                            className="input-bs"
                            value={productoForm.idMarca}
                            onChange={handleChange}
                            disabled={!productoForm.idCategoria}
                            required
                        >
                            <option value="">
                                {!productoForm.idCategoria 
                                    ? "🔒 Primero debe seleccionar una categoría" 
                                    : marcasFiltradas.length > 0 
                                        ? "Seleccione marca de producto..." 
                                        : "No hay marcas asociadas a esta categoría"}
                            </option>
                            {marcasFiltradas.map(m => {
                                const idM = m.idMarca || m.idMarcaProducto || m.id;
                                return (
                                    <option key={idM} value={idM}>
                                        {m.nombre}
                                    </option>
                                );
                            })}
                        </select>
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }} className="mt-4">
                        <button
                            type="submit"
                            className="btn-bs btn-success w-100"
                            style={{ padding: '12px', fontSize: '1rem' }}
                        >
                            {editMode ? 'Actualizar Producto' : 'Registrar Producto'}
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

            {/* TABLA DE PRODUCTOS REGISTRADOS */}
            <div className="card-panel mt-4">
                <h4 className="mb-4">📋 Productos Registrados</h4>

                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    <div style={{ ...gridStyle, background: 'var(--header-table)', color: 'var(--white)', fontWeight: 'bold' }}>
                        <div>ID</div>
                        <div>Nombre</div>
                        <div>Precio</div>
                        <div>Stock</div>
                        <div>Categoría</div>
                        <div>Marca Producto</div>
                        <div style={{ textAlign: 'center' }}>Acciones</div>
                    </div>

                    {productos.length === 0 ? (
                        <div className="p-4 text-center text-muted">
                            No se encontraron productos registrados.
                        </div>
                    ) : (
                        productos.map(p => (
                            <div
                                key={p.idProducto || p.id}
                                className="table-row-hover-effect"
                                style={{ ...gridStyle, borderBottom: '1px solid #eee', background: 'var(--white)' }}
                            >
                                <div className="fw-bold" style={{ color: 'var(--text-dark)' }}>
                                    {p.idProducto || p.id}
                                </div>
                                <div style={{ color: '#4b5563', fontWeight: '500' }}>
                                    {p.nombre}
                                </div>
                                <div style={{ color: '#10b981', fontWeight: 'bold' }}>
                                    ${Number(p.precio).toLocaleString('es-CO')}
                                </div>
                                <div>
                                    <span className={`badge ${p.stock > 0 ? 'bg-success' : 'bg-danger'}`} style={{ padding: '5px 10px', borderRadius: '5px', fontSize: '0.85rem' }}>
                                        {p.stock ?? 0} un.
                                    </span>
                                </div>
                                <div style={{ color: '#4b5563' }}>
                                    {p.nombreCategoria || p.categoria?.nombre || 'Sin Categoría'}
                                </div>
                                <div style={{ color: '#4b5563' }}>
                                    {p.nombreMarca || p.marca?.nombre || 'Sin Marca'}
                                </div>
                                <div className="text-center d-flex justify-content-center gap-2">
                                    <button
                                        className="btn-bs btn-success btn-sm"
                                        style={{ padding: '6px 12px' }}
                                        onClick={() => iniciarEdicion(p)}
                                    >
                                        <i className="fa-solid fa-pen"></i>
                                    </button>
                                    <button
                                        className="btn-bs btn-danger btn-sm"
                                        style={{ padding: '6px 12px' }}
                                        onClick={() => eliminarProducto(p.idProducto || p.id)}
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

export default Productos;