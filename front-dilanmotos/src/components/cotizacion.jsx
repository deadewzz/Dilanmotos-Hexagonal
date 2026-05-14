import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../global.css';

const API_URL = 'http://localhost:8080/cotizacion';

const Cotizacion = () => {

    const navigate = useNavigate();
    const [cotizacion, setCotizacion] = useState({ idUsuario: '', producto: '',cantidad: '',precioUnitario: '',fecha: '',productoAgregado: true});
    const [cotizaciones, setCotizaciones] = useState([]);
    const [mensaje, setMensaje] = useState('');

    useEffect(() => {

        const idUsuario = localStorage.getItem('idUsuario');

        if (!idUsuario) {
            navigate('/login');
        } else {

            setCotizacion(prev => ({
                ...prev,
                idUsuario: idUsuario
            }));

            cargarCotizaciones();
        }

    }, [navigate]);

    const cargarCotizaciones = async () => {

        try {

            const response = await fetch(API_URL);
            const data = await response.json();

            setCotizaciones(data);

        } catch (error) {
            console.error(error);
        }
    };

    const handleChange = (e) => {

        const { name, value, type, checked } = e.target;

        setCotizacion({
            ...cotizacion,
            [name]: type === 'checkbox' ? checked : value
        });
    };

    const calcularTotal = () => {

        return (
            Number(cotizacion.cantidad || 0) *
            Number(cotizacion.precioUnitario || 0)
        ).toFixed(2);
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            const response = await fetch(API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(cotizacion)
            });

            if (response.ok) {

                setMensaje('Cotización creada correctamente');

                cargarCotizaciones();

                setCotizacion({
                    idUsuario: localStorage.getItem('idUsuario'),
                    producto: '',
                    cantidad: '',
                    precioUnitario: '',
                    fecha: '',
                    productoAgregado: true
                });

            } else {
                setMensaje('Error al crear la cotización');
            }

        } catch (error) {

            console.error(error);
            setMensaje('Error de conexión con el servidor');
        }
    };

    const iniciarEdicion = (c) => {
        setCotizacion(c);
    };

    return (

        <div className="main-content-inner">

            {/* FORMULARIO */}

            <div className="card-panel">

                <h3 className="text-primary">
                    Nueva Cotización
                </h3>

                <hr />

                {mensaje && (
                    <div className="alert alert-info">
                        {mensaje}
                    </div>
                )}

                <form onSubmit={handleSubmit}>

                    <div className="mb-3">

                        <label className="fw-bold">
                            Producto
                        </label>

                        <input
                            className="input-bs"
                            type="text"
                            name="producto"
                            value={cotizacion.producto}
                            onChange={handleChange}
                            required
                        />

                    </div>

                    <div className="row">

                        <div className="col-md-6 mb-3">

                            <label className="fw-bold">
                                Cantidad
                            </label>

                            <input
                                className="input-bs"
                                type="number"
                                name="cantidad"
                                value={cotizacion.cantidad}
                                onChange={handleChange}
                                required
                            />

                        </div>

                        <div className="col-md-6 mb-3">

                            <label className="fw-bold">
                                Precio Unitario
                            </label>

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

                        <label className="fw-bold">
                            Fecha
                        </label>

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

                        <label className="fw-bold">
                            Estado
                        </label>

                        <select
                            className="input-bs"
                            name="productoAgregado"
                            value={cotizacion.productoAgregado}
                            onChange={(e) =>
                                setCotizacion({
                                    ...cotizacion,
                                    productoAgregado:
                                        e.target.value === 'true'
                                })
                            }
                        >
                            <option value={true}>
                                AGREGADO
                            </option>

                            <option value={false}>
                                PENDIENTE
                            </option>

                        </select>

                    </div>

                    <div className="mt-3 p-3 bg-light border rounded">

                        <h5>
                            Total: $
                            {calcularTotal()}
                        </h5>

                    </div>

                    <button
                        type="submit"
                        className="btn-bs btn-primary mt-3"
                    >
                        Guardar Cotización
                    </button>

                </form>

            </div>

            {/* TABLA */}

            <div className="card-panel mt-4">

                <h4 className="mb-4">
                    Listado General de Cotizaciones
                </h4>

                <div className="custom-table-container">

                    <div className="custom-table-header">
                        <div>Producto</div>
                        <div>Cantidad</div>
                        <div>Precio</div>
                        <div>Total</div>
                        <div>Fecha</div>
                        <div>Estado</div>
                        <div>Acciones</div>
                    </div>

                    {cotizaciones.map(c => (

                        <div
                            className="custom-table-row"
                            key={c.idCotizacion}
                        >

                            <div>{c.producto}</div>

                            <div>{c.cantidad}</div>

                            <div>${c.precioUnitario}</div>

                            <div>
                                $
                                {(c.cantidad * c.precioUnitario).toFixed(2)}
                            </div>

                            <div>{c.fecha}</div>

                            <div>

                                <span
                                    className={`badge ${
                                        c.productoAgregado
                                            ? 'bg-success'
                                            : 'bg-warning'
                                    }`}
                                >

                                    {c.productoAgregado
                                        ? 'AGREGADO'
                                        : 'PENDIENTE'}

                                </span>

                            </div>

                            <div className="d-flex gap-2">

                                <button
                                    className="btn-bs btn-success btn-sm"
                                    onClick={() => iniciarEdicion(c)}
                                >
                                    Editar
                                </button>

                                <button
                                    className="btn-bs btn-danger btn-sm"
                                    onClick={async () => {

                                        if(window.confirm('¿Eliminar cotización?')) {

                                            await fetch(
                                                `${API_URL}/${c.idCotizacion}`,
                                                {
                                                    method: 'DELETE'
                                                }
                                            );

                                            cargarCotizaciones();
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