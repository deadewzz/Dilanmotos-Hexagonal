import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../global.css';

const API_URL = 'http://localhost:8080/api/cotizaciones';

const Cotizacion = () => {
    const token = localStorage.getItem('token');
    const navigate = useNavigate();
    
    const [cotizacion, setCotizacion] = useState({ 
        idCotizacion: null, 
        idUsuario: '', 
        producto: '', 
        cantidad: '', 
        precioUnitario: '', 
        fecha: '', 
        productoAgregado: true 
    });
    const [cotizaciones, setCotizaciones] = useState([]);
    const [productos, setProductos] = useState([]);
    const [mensaje, setMensaje] = useState('');
    const [editMode, setEditMode] = useState(false);

    useEffect(() => {
        const idUsuario = localStorage.getItem('idUsuario');
        if (!idUsuario || !token) {
            navigate('/login');
        } else {
            setCotizacion(prev => ({
                ...prev,
                idUsuario: idUsuario
            }));
            cargarCotizaciones();
            cargarProductos();
        }
    }, []);

    const cargarProductos = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/productos', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            if (!response.ok) throw new Error('Error al obtener productos');
            const data = await response.json();
            setProductos(data);
        } catch (error) {
            console.error(error);
        }
    };

    const cargarCotizaciones = async () => {
        try {
            const response = await fetch(API_URL, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            if (!response.ok) throw new Error('Error al obtener cotizaciones');
            const data = await response.json();
            setCotizaciones(data);
        } catch (error) {
            console.error(error);
            setMensaje('No se pudieron cargar las cotizaciones');
        }
    };

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setCotizacion({
            ...cotizacion,
            [name]: type === 'checkbox' ? checked : value
        });
    };

    const handleProductoChange = (e) => {
        const selectedProducto = productos.find(p => String(p.idProducto) === e.target.value);
        if (selectedProducto) {
            setCotizacion({
                ...cotizacion,
                producto: selectedProducto.nombre,
                precioUnitario: selectedProducto.precio
            });
        } else {
            setCotizacion({ ...cotizacion, producto: '', precioUnitario: '' });
        }
    };

    const calcularTotal = () => {
        return (Number(cotizacion.cantidad || 0) * Number(cotizacion.precioUnitario || 0)).toFixed(2);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const idReal = cotizacion.idCotizacion || cotizacion.id;
            const isEditing = idReal !== null && idReal !== undefined;
            const method = isEditing ? 'PUT' : 'POST';
            const url = isEditing ? `${API_URL}/${idReal}` : API_URL;

            // Preparamos el cuerpo asegurando compatibilidad del ID con el backend mapeado
            const payload = {
                ...cotizacion,
                idCotizacion: idReal,
                id: idReal
            };

            const response = await fetch(url, {
                method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                setMensaje(isEditing ? 'Cotización actualizada correctamente' : 'Cotización creada correctamente');
                cargarCotizaciones();
                setEditMode(false);
                setCotizacion({
                    idCotizacion: null,
                    idUsuario: localStorage.getItem('idUsuario'),
                    producto: '',
                    cantidad: '',
                    precioUnitario: '',
                    fecha: '',
                    productoAgregado: true
                });
            } else {
                setMensaje(isEditing ? 'Error al actualizar la cotización' : 'Error al crear la cotización');
            }
        } catch (error) {
            console.error(error);
            setMensaje('Error de conexión con el servidor');
        }
    };

    const iniciarEdicion = (c) => {
        setEditMode(true);
        const idReal = c.id || c.idCotizacion;
        setCotizacion({
            idCotizacion: idReal,
            id: idReal, 
            idUsuario: c.idUsuario,
            producto: c.producto,
            cantidad: c.cantidad,
            precioUnitario: c.precioUnitario,
            fecha: c.fecha,
            productoAgregado: c.productoAgregado
        });
    };

    const cancelarEdicion = () => {
        setEditMode(false);
        setCotizacion({
            idCotizacion: null,
            idUsuario: localStorage.getItem('idUsuario'),
            producto: '',
            cantidad: '',
            precioUnitario: '',
            fecha: '',
            productoAgregado: true
        });
    };

    const eliminarCotizacion = async (id) => {
        try {
            const response = await fetch(`${API_URL}/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if(response.ok) {
                cargarCotizaciones();
                setMensaje('Cotización管liminada correctamente');
            } else {
                setMensaje('Error al eliminar la cotización');
            }
        } catch (error) {
            console.error(error);
        }
    };

    const gridStyle = {
        display: 'grid',
        gridTemplateColumns: '2fr 1fr 1fr 1fr 1.5fr 1.5fr 2fr', 
        gap: '15px',
        alignItems: 'center',
        padding: '12px 15px'
    };

    return (
        <div className="main-content-inner">
            {/* FORMULARIO DE REGISTRO / EDICIÓN */}
            <div className="card-panel">
                <h3 className="text-primary">{editMode ? 'Editar Cotización' : 'Nueva Cotización'}</h3>
                <hr />
                {mensaje && <div className="alert alert-info">{mensaje}</div>}
                
                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label className="fw-bold">Producto</label>
                        <select
                            className="input-bs"
                            name="producto"
                            value={productos.find(p => p.nombre === cotizacion.producto)?.idProducto || ''}
                            onChange={handleProductoChange}
                            required
                        >
                            <option value="">Seleccione un producto...</option>
                            {productos.length === 0 ? (
                                <option value="" disabled>No hay productos disponibles</option>
                            ) : (
                                productos.map((producto) => (
                                    <option key={producto.idProducto} value={producto.idProducto}>
                                        {producto.nombre}
                                    </option>
                                ))
                            )}
                        </select>
                    </div>

                    <div className="row" style={{ display: 'flex', gap: '20px', marginBottom: '15px' }}>
                        <div style={{ flex: 1 }}>
                            <label className="fw-bold">Cantidad</label>
                            <input
                                className="input-bs"
                                type="number"
                                name="cantidad"
                                value={cotizacion.cantidad}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div style={{ flex: 1 }}>
                            <label className="fw-bold">Precio Unitario</label>
                            <input
                                className="input-bs"
                                type="number"
                                step="0.01"
                                name="precioUnitario"
                                value={cotizacion.precioUnitario}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="mb-3">
                        <label className="fw-bold">Fecha</label>
                        <input
                            className="input-bs"
                            type="date"
                            name="fecha"
                            value={cotizacion.fecha}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="fw-bold">Estado</label>
                        <select
                            className={`input-bs ${cotizacion.productoAgregado ? 'text-success fw-bold' : 'text-warning fw-bold'}`}
                            name="productoAgregado"
                            value={cotizacion.productoAgregado}
                            onChange={(e) =>
                                setCotizacion({
                                    ...cotizacion,
                                    productoAgregado: e.target.value === 'true'
                                })
                            }
                        >
                            <option value="true" className="text-success fw-bold">AGREGADO</option>
                            <option value="false" className="text-warning fw-bold">PENDIENTE</option>
                        </select>
                    </div>

                    <div className="mt-3 p-3 bg-light border rounded" style={{ background: '#f8fafc', padding: '15px', border: '1px solid #e2e8f0', borderRadius: '6px' }}>
                        <h5 style={{ margin: 0 }}> Total: ${calcularTotal()} </h5>
                    </div>

                    <div style={{ display: 'flex', gap: '10px' }}>
                        <button type="submit" className="btn-bs btn-primary mt-3">
                            {editMode ? 'Actualizar Cotización' : 'Guardar Cotización'}
                        </button>
                        {editMode && (
                            <button type="button" className="btn-bs btn-danger mt-3" onClick={cancelarEdicion}>
                                Cancelar
                            </button>
                        )}
                    </div>
                </form>
            </div>

            {/* TABLA CON LA BARRA DE ENCABEZADO GRIS OSCURO */}
            <div className="card-panel mt-4">
                <h4 className="mb-4">Listado General de Cotizaciones</h4>
                <div className="custom-table-container">
                    
                    {/* Encabezado con color exacto #2d3748 */}
                    <div className="grid-cotizaciones-header" style={{
                        ...gridStyle,
                        backgroundColor: '#2d3748',
                        color: '#ffffff',
                        borderRadius: '6px 6px 0 0',
                        fontWeight: 'bold'
                    }}>
                        <div>Producto</div>
                        <div>Cantidad</div>
                        <div>Precio</div>
                        <div>Total</div>
                        <div>Fecha</div>
                        <div>Estado</div>
                        <div style={{ display: 'flex', justifyContent: 'center' }}>Acciones</div>
                    </div>

                    {/* Filas de la Tabla */}
                    {cotizaciones.map(c => (
                        <div className="grid-cotizaciones-row" style={{
                            ...gridStyle,
                            borderBottom: '1px solid #e2e8f0'
                        }} key={c.id || c.idCotizacion}>
                            <div style={{ fontWeight: '500', color: '#334155' }}>{c.producto}</div>
                            <div>{c.cantidad}</div>
                            <div>${c.precioUnitario}</div>
                            <div style={{ fontWeight: '600', color: '#1e293b' }}>
                                ${(c.cantidad * c.precioUnitario).toFixed(2)}
                            </div>
                            <div>{c.fecha}</div>
                            
                            {/* ESTADO LIMPIO Y ESTÁTICO (Badge tradicional) */}
                            <div>
                                <span className={`badge-cotizacion ${c.productoAgregado ? 'bg-success-badge' : 'bg-warning-badge'}`}>
                                    {c.productoAgregado ? 'AGREGADO' : 'PENDIENTE'}
                                </span>
                            </div>
                            
                            {/* Botones de acción originales */}
                            <div style={{ display: 'flex', gap: '8px', justifyContent: 'center' }}>
                                <button
                                    className="btn-bs btn-success"
                                    style={{ padding: '6px 12px', fontSize: '13px', borderRadius: '4px' }}
                                    onClick={() => iniciarEdicion(c)}
                                >
                                    Editar
                                </button>
                                <button
                                    className="btn-bs btn-danger"
                                    style={{ padding: '6px 12px', fontSize: '13px', borderRadius: '4px' }}
                                    onClick={() => {
                                        const idParaEliminar = c.id || c.idCotizacion;
                                        if(window.confirm('¿Eliminar cotización?')) {
                                            eliminarCotizacion(idParaEliminar);
                                        }
                                    }}
                                >
                                    Borrar
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default Cotizacion;