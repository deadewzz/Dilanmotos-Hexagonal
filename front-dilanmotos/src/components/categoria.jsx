import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../global.css';
import { authFetch } from '../api';

const API_URL = 'http://localhost:8080/api/categorias';

const Categoria = () => {
    const token = localStorage.getItem('token');

    const [categorias, setCategorias] = useState([]);
    const [nueva, setNueva] = useState({  nombre: ''});
    const [editMode, setEditMode] = useState(false);
    const [mensaje, setMensaje] = useState('');
    
    useEffect(() => { cargarCategorias(); }, []);

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
            setMensaje('No se pudieron cargar las categorias');
        }
    };

    const guardar = async (e) => {

        e.preventDefault();

        try {

            const response = await fetch(
                editMode
                    ? `${API_URL}/${nueva.idCategoria}`
                    : API_URL,
                {
                    method: editMode ? 'PUT' : 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}` 
                    },
                    body: JSON.stringify(nueva)
                }
            );

            if(response.ok) {

                setMensaje(
                    editMode
                        ? 'Categoría actualizada correctamente'
                        : 'Categoría creada correctamente'
                );

                resetForm();
                cargarCategorias();

            } else {
                setMensaje('Error al guardar');
            }

        } catch (error) {
            console.error(error);
            setMensaje('Error de conexión');
        }
    };

    const iniciarEdicion = (categoria) => {
        setNueva(categoria);
        setEditMode(true);
    };

    const eliminar = async (id) => {
        if(window.confirm('¿Eliminar categoría?')) {
            try {
                await fetch(`${API_URL}/${id}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    }
                });
                cargarCategorias();
            } catch (error) {

                console.error(error);
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
                        ? 'Editar Categoría'
                        : 'Nueva Categoría'}
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
                            Nombre de la Categoría
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
                                : 'Crear Categoría'}
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
                    Listado General de Categorías
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

                    {categorias.map(c => (

                        <div
                            key={c.idCategoria}
                            className="custom-table-row"
                            style={{
                                display: 'grid',
                                gridTemplateColumns: '1fr 3fr 2fr',
                                alignItems: 'center'
                            }}
                        >

                            <div className="fw-bold">
                                {c.idCategoria}
                            </div>

                            <div>
                                {c.nombre}
                            </div>

                            <div
                                className="text-center d-flex gap-2 justify-content-center"
                            >

                                <button
                                    className="btn-bs btn-success btn-sm"
                                    onClick={() => iniciarEdicion(c)}
                                >
                                    Editar
                                </button>

                                <button
                                    className="btn-bs btn-danger btn-sm"
                                    onClick={() => eliminar(c.idCategoria)}
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
export default Categoria;