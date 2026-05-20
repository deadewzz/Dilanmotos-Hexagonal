import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../global.css';
import { authFetch } from '../api';

const API_URL = 'http://localhost:8080/api/marcas';

const Marca = () => {
    const token = localStorage.getItem('token');

    const [marcas, setMarcas] = useState([]);
    const [nueva, setNueva] = useState({  nombre: ''});
    const [editMode, setEditMode] = useState(false);
    const [mensaje, setMensaje] = useState('');
    
    useEffect(() => { cargarMarcas(); }, []);

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
            setMensaje('No se pudieron cargar las marcas');
        }
    };

    const guardar = async (e) => {
        e.preventDefault();
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
                body: JSON.stringify(nueva)
            });

            if (response.ok) {
                alert(editMode ? "Marca actualizada!" : "Marca guardada con éxito!!");
                setNueva({ nombre: "" });
                setEditMode(false);
                cargarMarcas();
            }
        } catch (error) {
            console.log("Error al procesar la solicitud:", error);
        }
    };

    const iniciarEdicion = (marca) => {
        setNueva(marca);
        setEditMode(true);
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
                    headers: { 'Authorization': `Bearer ${token}` }
                });
               
                if (response.status === 204 || response.ok) {
                    setMarcas(marcas.filter(m => m.idMarca !== id));
                    alert("Registro eliminado con éxito!");
                } else {
                    alert("No se pudo eliminar el registro seleccionado");
                }
            } catch (error) {
                console.log("Error al eliminar", error);
            }
        }
    };
    const resetForm = () => {
        setNueva({
            nombre: ''
        });
        setEditMode(false);
    };

    return (

        <div className="main-content-inner">

            {/* FORMULARIO */}

            <div className="card-panel">

                <h3 className="text-primary">
                    {editMode
                        ? 'Editar Marca'
                        : 'Nueva Marca'}
                </h3>
                <hr />

                {mensaje && (
                    <div className="alert alert-info">
                        {mensaje}
                    </div>
                )}

                <form onSubmit={guardar}>

                    <div className="mb-3">

                        <label className="fw-bold">
                            Nombre de la Marca
                        </label>

                        <input
                            className="input-bs"
                            type="text"
                            value={nueva.nombre}
                            onChange={(e) =>
                                setNueva({
                                    ...nueva,
                                    nombre: e.target.value
                                })
                            }
                            required
                        />
                    </div>
                    <div className="d-flex gap-2 mt-4">
                        <button
                            type="submit"
                            className="btn-bs btn-primary"
                        >
                            {editMode
                                ? 'Guardar Cambios'
                                : 'Crear marca'}
                        </button>
                        {editMode && (
                            <button
                                type="button"
                                className="btn-bs btn-secondary"
                                onClick={resetForm}
                            >
                                Cancelar
                            </button>
                        )}
                    </div>
                </form>
            </div>

            {/* TABLA */}

            <div className="card-panel mt-4">

                <h4 className="mb-4">
                    Listado General de Marcas
                </h4>

                <div className="custom-table-container">

                    {/* HEADER */}

                    <div
                        className="custom-table-header"
                        style={{
                            display: 'grid',
                            gridTemplateColumns: '1fr 3fr 2fr',
                            alignItems: 'center'
                        }}
                    >
                        <div>ID</div>
                        <div>Nombre</div>
                        <div className="text-center">
                            Acciones
                        </div>
                    </div>

                    {/* FILAS */}

                    {marcas.map(m => (

                        <div
                            key={m.idMarca}
                            className="custom-table-row"
                            style={{
                                display: 'grid',
                                gridTemplateColumns: '1fr 3fr 2fr',
                                alignItems: 'center'
                            }}
                        >

                            <div className="fw-bold">
                                {m.idMarca}
                            </div>

                            <div>
                                {m.nombre}
                            </div>

                            <div
                                className="text-center d-flex gap-2 justify-content-center"
                            >

                                <button
                                    className="btn-bs btn-success btn-sm"
                                    onClick={() => iniciarEdicion(m)}
                                >
                                    Editar
                                </button>

                                <button
                                    className="btn-bs btn-danger btn-sm"
                                    onClick={() => eliminar(m.idMarca)}
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
export default Marca;