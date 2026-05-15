import { useEffect, useState } from "react";

export default function Productos() {
    const [productos, setProductos] = useState([]);
    const [marcas, setMarcas] = useState([]);
    const [categorias, setCategorias] = useState([]);
    
    const [nuevo, setNuevo] = useState({ 
        nombre: '', descripcion: '', precio: '', idMarca: '', idCategoria: '' 
    });
    
    // Estados para la edición
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

    useEffect(() => { cargarDatos(); }, []);

    const prepararEdicion = (prod) => {
        setEditMode(true);
        setSelectedId(prod.idProducto);
        setNuevo({
            nombre: prod.nombre,
            descripcion: prod.descripcion,
            precio: prod.precio,
            idMarca: prod.idMarca || '',
            idCategoria: prod.idCategoria || ''
        });
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const cancelarEdicion = () => {
        setEditMode(false);
        setSelectedId(null);
        setNuevo({ nombre: '', descripcion: '', precio: '', idMarca: '', idCategoria: '' });
    };

    const guardar = async (e) => {
        e.preventDefault();
        
        const url = editMode ? `${API_URL}/${selectedId}` : API_URL;
        
        // El payload debe enviar IDs planos para que ProductoRequestDTO los reciba bien
        const payload = {
            nombre: nuevo.nombre,
            descripcion: nuevo.descripcion,
            precio: parseFloat(nuevo.precio),
            idMarca: parseInt(nuevo.idMarca),
            idCategoria: parseInt(nuevo.idCategoria)
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
                <h3 className="text-primary">
                    {editMode ? '📝 Editar Producto' : '📦 Registro de Productos'}
                </h3>
                <form onSubmit={guardar}>
                    <div className="row">
                        <div className="col-md-6 mb-3">
                            <label className="form-label">Nombre del Producto</label>
                            <input className="input-bs" placeholder="Ej: Kit de Arrastre" value={nuevo.nombre} onChange={e => setNuevo({...nuevo, nombre: e.target.value})} required />
                        </div>
                        <div className="col-md-6 mb-3">
                            <label className="form-label">Precio ($)</label>
                            <input className="input-bs" type="number" placeholder="0.00" value={nuevo.precio} onChange={e => setNuevo({...nuevo, precio: e.target.value})} required />
                        </div>
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Descripción</label>
                        <textarea className="input-bs" rows="2" placeholder="Detalles del producto..." value={nuevo.descripcion} onChange={e => setNuevo({...nuevo, descripcion: e.target.value})} required />
                    </div>
                    
                    <div className="row mb-3">
                        <div className="col-md-6">
                            <label className="form-label">Marca</label>
                            <select className="input-bs" value={nuevo.idMarca} onChange={e => setNuevo({...nuevo, idMarca: e.target.value})} required>
                                <option value="">Seleccione marca...</option>
                                {marcas.map(m => <option key={m.idMarca} value={m.idMarca}>{m.nombre}</option>)}
                            </select>
                        </div>
                        <div className="col-md-6">
                            <label className="form-label">Categoría</label>
                            <select className="input-bs" value={nuevo.idCategoria} onChange={e => setNuevo({...nuevo, idCategoria: e.target.value})} required>
                                <option value="">Seleccione categoría...</option>
                                {categorias.map(c => <option key={c.idCategoria} value={c.idCategoria}>{c.nombre}</option>)}
                            </select>
                        </div>
                    </div>

                    <div className="d-flex gap-2">
                        <button type="submit" className={`btn-bs w-100 ${editMode ? 'btn-warning' : 'btn-primary'}`}>
                            {editMode ? 'Actualizar Cambios' : 'Registrar Producto'}
                        </button>
                        {editMode && (
                            <button type="button" className="btn-bs btn-secondary" onClick={cancelarEdicion}>
                                Cancelar
                            </button>
                        )}
                    </div>
                </form>
            </div>

            <div className="card-panel mt-4">
                <div className="custom-table-container">
                    <div className="custom-table-header">
                        <div>Producto</div><div>Marca</div><div>Precio</div><div className="text-center">Acciones</div>
                    </div>
                    {productos.length > 0 ? (
                        productos.map(p => (
                            <div className="custom-table-row" key={p.idProducto}>
                                <div className="fw-bold">{p.nombre}</div>
                                {/* Gracias al ajuste en el Backend UC, ahora m.marca?.nombre funcionará */}
                                <div>{p.marca?.nombre || 'S/M'}</div>
                                <div className="text-success fw-bold">${p.precio}</div>
                                <div className="text-center d-flex justify-content-center gap-2">
                                    <button className="btn-bs btn-warning btn-sm" onClick={() => prepararEdicion(p)}>
                                        <i className="fa-solid fa-pen"></i>
                                    </button>
                                    <button className="btn-bs btn-danger btn-sm" onClick={() => eliminarProducto(p.idProducto)}>
                                        <i className="fa-solid fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="p-3 text-center text-muted">No hay productos registrados.</div>
                    )}
                </div>
            </div>
        </div>
    );
}