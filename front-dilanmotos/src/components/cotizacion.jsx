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
        idProducto: '',
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
            const normalized = Array.isArray(data) ? data.map(item => ({
                ...item,
                productoAgregado: item.productoAgregado ?? item.producto_agregado ?? false
            })) : [];
            setCotizaciones(normalized);
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
                idProducto: selectedProducto.idProducto,
                producto: selectedProducto.nombre,
                precioUnitario: selectedProducto.precio
            });
        } else {
            setCotizacion({ ...cotizacion, idProducto: '', producto: '', precioUnitario: '' });
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

            const payload = {
                ...cotizacion,
                idCotizacion: idReal,
                id: idReal,
                producto_agregado: cotizacion.productoAgregado === true || cotizacion.productoAgregado === 'true'
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
                setMensaje(isEditing ? '✅ Cotización actualizada correctamente' : '✅ Cotización creada correctamente');
                cargarCotizaciones();
                setEditMode(false);
                setCotizacion({
                    idCotizacion: null,
                    idUsuario: localStorage.getItem('idUsuario'),
                    idProducto: '',
                    producto: '',
                    cantidad: '',
                    precioUnitario: '',
                    fecha: '',
                    productoAgregado: true
                });
            } else {
                setMensaje(isEditing ? '❌ Error al actualizar la cotización' : '❌ Error al crear la cotización');
            }
        } catch (error) {
            console.error(error);
            setMensaje('Error de conexión con el servidor');
        }
    };

    const iniciarEdicion = (c) => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
        setEditMode(true);
        const idReal = c.id || c.idCotizacion;
        setCotizacion({
            idCotizacion: idReal,
            id: idReal, 
            idUsuario: c.idUsuario,
            idProducto: c.idProducto ?? '',
            producto: c.producto,
            cantidad: c.cantidad,
            precioUnitario: c.precioUnitario,
            fecha: c.fecha,
            productoAgregado: c.productoAgregado ?? c.producto_agregado ?? false
        });
    };

    const cancelarEdicion = () => {
        setEditMode(false);
        setCotizacion({
            idCotizacion: null,
            idUsuario: localStorage.getItem('idUsuario'),
            idProducto: '',
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
                setMensaje('✅ Cotización eliminada correctamente');
            } else {
                setMensaje('❌ Error al eliminar la cotización');
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
        padding: '15px',
        minWidth: '850px'
    };

    return (
        <div className="main-content-inner">
            {/* PANEL DE FORMULARIO */}
            <div className="card-panel">
                <h3 className="text-primary mb-4">
                    {editMode ? '📝 Editar Cotización Existente' : '📩 Crear Nueva Cotización'}
                </h3>
                {mensaje && <div className="alert alert-info fw-bold mb-3">{mensaje}</div>}
                
                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label className="form-label fw-bold">Producto Seleccionable</label>
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

                    <div className="row">
                        <div className="col-md-6 mb-3">
                            <label className="form-label fw-bold">Cantidad</label>
                            <input
                                className="input-bs"
                                type="number"
                                name="cantidad"
                                placeholder="Ej: 5"
                                value={cotizacion.cantidad}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="col-md-6 mb-3">
                            <label className="form-label fw-bold">Precio Unitario ($)</label>
                            <input
                                className="input-bs"
                                type="number"
                                step="0.01"
                                name="precioUnitario"
                                placeholder="0.00"
                                value={cotizacion.precioUnitario}
                                readOnly
                                disabled
                                required
                            />
                            <small className="text-muted">Se toma automáticamente del producto seleccionado.</small>
                        </div>
                    </div>

                    <div className="row">
                        <div className="col-md-6 mb-3">
                            <label className="form-label fw-bold">Fecha de Registro</label>
                            <input
                                className="input-bs"
                                type="date"
                                name="fecha"
                                value={cotizacion.fecha}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="col-md-6 mb-3">
                            <label className="form-label fw-bold">Estado del Proceso</label>
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
                    </div>

                    <div className="mt-3 p-3 border rounded mb-4" style={{ background: '#f8f9fa', border: '1px solid #dee2e6' }}>
                        <h5 className="m-0 fw-bold text-dark"> Total Proyectado: ${calcularTotal()} </h5>
                    </div>

                    {/* SECCIÓN DE BOTONES EN VERTICAL PARA MANTENER LA CONSISTENCIA */}
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                        <button 
                            type="submit" 
                            className={`btn-bs w-100 ${editMode ? 'btn-success' : 'btn-success'}`}
                            style={{ padding: '12px', fontSize: '1rem' }}
                        >
                            {editMode ? 'Actualizar Cotización' : 'Guardar Cotización'}
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

            {/* PANEL DE LA LISTA / TABLA DE REGISTROS */}
            <div className="card-panel mt-4">
                <div className="row align-items-center mb-3">
                    <div className="col-md-6">
                        <h4 className="text-muted m-0">📚 Listado General de Cotizaciones</h4>
                    </div>
                </div>
                
                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    
                    {/* CABECERA CON GRID */}
                    <div style={{
                        ...gridStyle,
                        background: 'var(--header-table)',
                        color: 'var(--white)',
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

                    {/* CUERPO DINÁMICO DE LA TABLA CON EFECTOS HOVER */}
                    {cotizaciones.length > 0 ? (
                        cotizaciones.map(c => (
                            <div className="table-row-hover-effect" style={{
                                ...gridStyle,
                                borderBottom: '1px solid #eee',
                                background: 'var(--white)',
                                transition: '0.2s'
                            }} key={c.id || c.idCotizacion}>
                                <div className="fw-bold" style={{ color: 'var(--text-dark)' }}>{c.producto}</div>
                                <div style={{ color: '#4b5563' }}>{c.cantidad}</div>
                                <div style={{ color: '#4b5563' }}>${c.precioUnitario}</div>
                                <div style={{ fontWeight: '600', color: '#1e293b' }}>
                                    ${(c.cantidad * c.precioUnitario).toFixed(2)}
                                </div>
                                <div style={{ color: '#4b5563', fontSize: '0.9rem' }}>{c.fecha}</div>
                                
                                <div>
                                    <span className={`badge ${c.productoAgregado ? 'bg-success text-white' : 'bg-warning text-dark'}`} 
                                          style={{ padding: '5px 10px', borderRadius: '5px', fontSize: '0.85rem', fontWeight: 'bold' }}>
                                        {c.productoAgregado ? 'AGREGADO' : 'PENDIENTE'}
                                    </span>
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
                                        onClick={() => {
                                            const idParaEliminar = c.id || c.idCotizacion;
                                            if(window.confirm('¿Está seguro de que desea eliminar esta cotización?')) {
                                                eliminarCotizacion(idParaEliminar);
                                            }
                                        }}
                                    >
                                        <i className="fa-solid fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="p-4 text-center text-muted">No se registran cotizaciones en el sistema.</div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Cotizacion;