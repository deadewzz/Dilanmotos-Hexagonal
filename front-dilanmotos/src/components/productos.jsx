import { useEffect, useState } from "react";
import { authFetch } from "../api";

export default function Productos() {
    const [productos, setProductos] = useState([]);
    const [marcas, setMarcas] = useState([]);
    const [categorias, setCategorias] = useState([]);
    
    const [nuevo, setNuevo] = useState({ 
        nombre: '', descripcion: '', precio: '', idMarca: '', idCategoria: '' 
    });

    // Recuperamos el token del localStorage para todas las peticiones
    const token = localStorage.getItem('token');

    const cargarDatos = async () => {
        try {
            // Definimos los headers con el Bearer Token
            const headers = { 'Authorization': `Bearer ${token}` };
            
            const fetchJson = (url) => fetch(url, { headers }).then(r => r.ok ? r.json() : []);
            
            // Cargamos todo en paralelo
            const [dataP, dataM, dataC] = await Promise.all([
                fetchJson('http://localhost:8080/api/productos'),
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

    const guardar = async (e) => {
        e.preventDefault();
        const payload = {
            nombre: nuevo.nombre,
            descripcion: nuevo.descripcion,
            precio: parseFloat(nuevo.precio),
            marca: { idMarca: parseInt(nuevo.idMarca) },
            categoria: { idCategoria: parseInt(nuevo.idCategoria) }
        };

        try {
            const res = await fetch('http://localhost:8080/api/productos', {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}` 
                },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                setNuevo({ nombre: '', descripcion: '', precio: '', idMarca: '', idCategoria: '' });
                await cargarDatos();
                alert("Producto guardado");
            } else {
                alert("Error al guardar: " + res.status);
            }
        } catch (error) {
            console.error("Error en POST:", error);
        }
    };

    const eliminarProducto = async (id) => {
        if (window.confirm("¿Estás seguro de eliminar este producto?")) {
            try {
                const res = await fetch(`http://localhost:8080/api/productos/${id}`, {
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
                <h3 className="text-primary">📦 Gestión de Productos</h3>
                <form onSubmit={guardar}>
                    <input className="input-bs" placeholder="Nombre Producto" value={nuevo.nombre} onChange={e => setNuevo({...nuevo, nombre: e.target.value})} required />
                    <textarea className="input-bs" placeholder="Descripción" value={nuevo.descripcion} onChange={e => setNuevo({...nuevo, descripcion: e.target.value})} required />
                    <input className="input-bs" type="number" placeholder="Precio" value={nuevo.precio} onChange={e => setNuevo({...nuevo, precio: e.target.value})} required />
                    
                    <div className="row">
                        <div className="col-md-6">
                            <label className="form-label text-muted small">Marca</label>
                            <select className="input-bs" value={nuevo.idMarca} onChange={e => setNuevo({...nuevo, idMarca: e.target.value})} required>
                                <option value="">Seleccione marca...</option>
                                {marcas.map(m => <option key={m.idMarca} value={m.idMarca}>{m.nombre}</option>)}
                            </select>
                        </div>
                        <div className="col-md-6">
                            <label className="form-label text-muted small">Categoría</label>
                            <select className="input-bs" value={nuevo.idCategoria} onChange={e => setNuevo({...nuevo, idCategoria: e.target.value})} required>
                                <option value="">Seleccione categoría...</option>
                                {categorias.map(c => <option key={c.idCategoria} value={c.idCategoria}>{c.nombre}</option>)}
                            </select>
                        </div>
                    </div>
                    <button type="submit" className="btn-bs btn-primary w-100 mt-3">Registrar Producto</button>
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
                                <div>{p.marca?.nombre || 'S/M'}</div>
                                <div className="text-success fw-bold">${p.precio}</div>
                                <div className="text-center">
                                    <button className="btn-bs btn-danger btn-sm" onClick={() => eliminarProducto(p.idProducto)}>
                                        <i className="fa-solid fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="p-3 text-center text-muted">No hay productos disponibles.</div>
                    )}
                </div>
            </div>
        </div>
    );
}