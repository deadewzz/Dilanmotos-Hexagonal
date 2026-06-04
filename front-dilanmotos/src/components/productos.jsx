import { useEffect, useState } from "react";

export default function Productos() {
    const [productos, setProductos] = useState([]);
    const [marcas, setMarcas] = useState([]);
    const [categorias, setCategorias] = useState([]);
    
    const [nuevo, setNuevo] = useState({ 
        nombre: '', descripcion: '', precio: '', idMarca: '', idCategoria: '', imagenUrl: '' 
    });
    
    const [editMode, setEditMode] = useState(false);
    const [selectedId, setSelectedId] = useState(null);

    const token = localStorage.getItem('token');
    const API_URL = 'http://localhost:8080/api/productos';

    const cargarDatos = async () => {
        try {
            const headers = { 'Authorization': `Bearer ${token}` };
            const fetchJson = (url) => fetch(url, { headers }).then(r => r.ok ? r.json() : []);
            
            const [dataP, dataM, dataC] = await Promise.all([
                fetchJson(API_URL),
                fetchJson('http://localhost:8080/api/marcas'),
                fetchJson('http://localhost:8080/api/categorias')
            ]);

            setProductos(dataP);
            setMarcas(dataM);
            setCategorias(dataC);
        } catch (e) { 
            console.error("Error cargando datos:", e); 
        }
    };

    useEffect(() => { 
        cargarDatos(); 
    }, []);

    const prepararEdicion = (prod) => {
        setEditMode(true);
        setSelectedId(prod.idProducto);
        
        setNuevo({
            nombre: prod.nombre || '',
            descripcion: prod.descripcion || '',
            precio: prod.precio || '',
            imagenUrl: prod.imagenUrl || '',
            idMarca: prod.marca?.idMarca || prod.idMarca || '',
            idCategoria: prod.categoria?.idCategoria || prod.idCategoria || ''
        });
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const cancelarEdicion = () => {
        setEditMode(false);
        setSelectedId(null);
        setNuevo({ nombre: '', descripcion: '', precio: '', imagenUrl: '', idMarca: '', idCategoria: '' });
    };

    const guardar = async (e) => {
        e.preventDefault();
        
        const url = editMode ? `${API_URL}/${selectedId}` : API_URL;
        
        const payload = {
            nombre: nuevo.nombre,
            descripcion: nuevo.descripcion,
            precio: parseFloat(nuevo.precio),
            imagenUrl: nuevo.imagenUrl,
            marca: { idMarca: parseInt(nuevo.idMarca) },
            categoria: { idCategoria: parseInt(nuevo.idCategoria) }
        };

        try {
            const res = await fetch(url, {
                method: editMode ? 'PUT' : 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}` 
                },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                alert(editMode ? "✅ Producto actualizado" : "✅ Producto guardado");
                cancelarEdicion();
                await cargarDatos();
            } else {
                alert("❌ Error al procesar: " + res.status);
            }
        } catch (error) {
            console.error("Error en la petición:", error);
        }
    };

    const eliminarProducto = async (id) => {
        if (window.confirm("¿Estás seguro de eliminar este producto?")) {
            try {
                const res = await fetch(`${API_URL}/${id}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                if (res.ok) {
                    await cargarDatos();
                } else {
                    alert("No se pudo eliminar el producto.");
                }
            } catch (error) {
                console.error("Error en DELETE:", error);
            }
        }
    };

    return (
        <div className="main-content-inner">
            <div className="card-panel">
                <h3 className="text-primary mb-4">
                    {editMode ? '✏️ Editar Producto' : '📦 Registro de Productos'}
                </h3>
                <form onSubmit={guardar}>
                    <div className="row">
                        <div className="col-md-6 mb-3">
                            <label className="form-label">Nombre del Producto</label>
                            <input 
                                className="input-bs" 
                                placeholder="Ej: Kit de Arrastre" 
                                value={nuevo.nombre} 
                                onChange={e => setNuevo({...nuevo, nombre: e.target.value})} 
                                required 
                            />
                        </div>
                        <div className="col-md-6 mb-3">
                            <label className="form-label">Precio ($)</label>
                            <input 
                                className="input-bs" 
                                type="number" 
                                step="any"
                                placeholder="0.00" 
                                value={nuevo.precio} 
                                onChange={e => setNuevo({...nuevo, precio: e.target.value})} 
                                required 
                            />
                        </div>
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Descripción</label>
                        <textarea 
                            className="input-bs" 
                            rows="2" 
                            placeholder="Detalles del producto..." 
                            value={nuevo.descripcion} 
                            onChange={e => setNuevo({...nuevo, descripcion: e.target.value})} 
                            required 
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">URL de la imagen</label>
                        <textarea 
                            className="input-bs" 
                            rows="2" 
                            placeholder="ingresa la url..." 
                            value={nuevo.imagenUrl} 
                            onChange={e => setNuevo({...nuevo, imagenUrl: e.target.value})} 
                            required 
                        />
                    </div>
                    
                    <div className="row mb-3">
                        <div className="col-md-6 mb-3 mb-md-0">
                            <label className="form-label">Marca</label>
                            <select 
                                className="input-bs" 
                                value={nuevo.idMarca} 
                                onChange={e => setNuevo({...nuevo, idMarca: e.target.value})} 
                                required
                            >
                                <option value="">Seleccione marca...</option>
                                {marcas.map(m => (
                                    <option key={m.idMarca} value={m.idMarca}>{m.nombre}</option>
                                ))}
                            </select>
                        </div>
                        <div className="col-md-6">
                            <label className="form-label">Categoría</label>
                            <select 
                                className="input-bs" 
                                value={nuevo.idCategoria} 
                                onChange={e => setNuevo({...nuevo, idCategoria: e.target.value})} 
                                required
                            >
                                <option value="">Seleccione categoría...</option>
                                {categorias.map(c => (
                                    <option key={c.idCategoria} value={c.idCategoria}>{c.nombre}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    {/* SECCIÓN DE BOTONES ADAPTADA EN VERTICAL */}
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginTop: '15px' }}>
                        <button 
                            type="submit" 
                            className={`btn-bs w-100 ${editMode ? 'btn-success' : 'btn-success'}`}
                            style={{ padding: '12px', fontSize: '1rem' }}
                        >
                            {editMode ? 'Actualizar Cambios' : 'Registrar Producto'}
                        </button>
                        {editMode && (
                            <button 
                                type="button" 
                                className="btn-bs btn-danger w-100" 
                                onClick={cancelarEdicion}
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
                        <h4 className="text-muted m-0">📚 Productos Registrados</h4>
                    </div>
                </div>

                {/* Contenedor con bordes y radio unificados de tu CSS */}
                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    
                    {/* CABECERA CON COLOR DEL CSS (--header-table) Y ALINEACIÓN PERFECTA */}
                    <div style={{ 
                        display: 'grid', 
                        gridTemplateColumns: '3fr 1.5fr 1.2fr 1.3fr', 
                        gap: '15px', 
                        alignItems: 'center', 
                        padding: '15px',
                        background: 'var(--header-table)',
                        color: 'var(--white)',
                        fontWeight: 'bold',
                        minWidth: '600px'
                    }}>
                        <div>Producto</div>
                        <div>Marca</div>
                        <div>Precio</div>
                        <div style={{ display: 'flex', justifyContent: 'center' }}>Acciones</div>
                    </div>

                    {/* CUERPO DE LAS FILAS */}
                    {productos.length > 0 ? (
                        productos.map(p => (
                            <div key={p.idProducto} className="table-row-hover-effect" style={{ 
                                display: 'grid', 
                                gridTemplateColumns: '3fr 1.5fr 1.2fr 1.3fr', 
                                gap: '15px', 
                                alignItems: 'center', 
                                padding: '15px',
                                borderBottom: '1px solid #eee',
                                minWidth: '600px',
                                background: 'var(--white)',
                                transition: '0.2s'
                            }}>
                                <div className="fw-bold" style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', color: 'var(--text-dark)' }} title={p.nombre}>
                                    {p.nombre}
                                </div>
                                <div style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', color: '#7e8299' }} title={p.marca?.nombre || 'S/M'}>
                                    {p.marca?.nombre || 'S/M'}
                                </div>
                                <div className="fw-bold" style={{ whiteSpace: 'nowrap', color: 'var(--success)' }}>
                                    ${p.precio}
                                </div>
                                <div className="text-center">
                                    <button className="btn-bs btn-success btn-sm" style={{ padding: '6px 12px' }} onClick={() => prepararEdicion(p)}>
                                        <i className="fa-solid fa-pen"></i>
                                    </button>
                                    <button className="btn-bs btn-danger btn-sm" style={{ padding: '6px 12px' }} onClick={() => eliminarProducto(p.idProducto)}>
                                        <i className="fa-solid fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="p-4 text-center text-muted">No hay productos registrados.</div>
                    )}
                </div>
            </div>
        </div>
    );
}