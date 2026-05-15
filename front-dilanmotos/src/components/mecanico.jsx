import React, { useState, useEffect } from 'react';
import '../global.css';

const Mecanico = () => {
    const [mecanicos, setMecanicos] = useState([]); 
    const [busqueda, setBusqueda] = useState(""); 
    // Inicializamos con strings vacíos para evitar el error de "controlled input" de tu imagen
    const [nuevoMecanico, setNuevoMecanico] = useState({ 
        nombre: "", 
        especialidad: "", 
        telefono: "" 
    }); 
    const [editMode, setEditMode] = useState(false); 

    const API_URL = "http://localhost:8080/api/mecanicos";

    useEffect(() => {
        cargarMecanicos();
    }, []);

    const cargarMecanicos = async () => {
        const token = localStorage.getItem('token'); // Recuperar token actualizado
        try {
            const url = busqueda ? `${API_URL}?search=${busqueda}` : API_URL;
            const response = await fetch(url, {
                headers: { 
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            if (response.ok) {
                const data = await response.json();
                setMecanicos(data);
            } else if (response.status === 401) {
                console.error("Sesión expirada o token inválido");
            }
        } catch (error) {
            console.log("Error al conectar con la API:", error);
        }
    };

    const handleGuardar = async (e) => {
        e.preventDefault();
        const token = localStorage.getItem('token');
        const metodo = editMode ? 'PUT' : 'POST';
        
        // IMPORTANTE: Asegurar que tomamos id_mecanico
        const idActual = nuevoMecanico.id_mecanico;
        const url = editMode ? `${API_URL}/${idActual}` : API_URL;

        try {
            const response = await fetch(url, {
                method: metodo,
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(nuevoMecanico)
            });

            if (response.ok) {
                alert(editMode ? "¡Mecánico actualizado!" : "¡Mecánico guardado!");
                setNuevoMecanico({ nombre: "", especialidad: "", telefono: "" });
                setEditMode(false);
                cargarMecanicos();
            }
        } catch (error) {
            console.log("Error en la solicitud:", error);
        }
    };

    const handleEliminar = async (id) => {
        if (!id) return;
        const token = localStorage.getItem('token');

        if (window.confirm("¿Eliminar este mecánico?")) {
            try {
                const response = await fetch(`${API_URL}/${id}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                
                if (response.ok) {
                    setMecanicos(mecanicos.filter(m => m.id_mecanico !== id));
                    alert("Eliminado con éxito");
                }
            } catch (error) {
                console.log("Error al eliminar", error);
            }
        }
    };

    return (
        <div className="container mt-4">
            <h2 className="text-primary">Gestión de Mecánicos</h2>
            <div className="card-panel">
                <form onSubmit={handleGuardar}>
                    <input
                        className="input-bs"
                        placeholder="Nombre"
                        value={nuevoMecanico.nombre || ""} 
                        onChange={e => setNuevoMecanico({ ...nuevoMecanico, nombre: e.target.value })}
                        required
                    />
                    <input
                        className="input-bs"
                        placeholder="Especialidad"
                        value={nuevoMecanico.especialidad || ""}
                        onChange={e => setNuevoMecanico({ ...nuevoMecanico, especialidad: e.target.value })}
                        required
                    />
                    <input
                        className="input-bs"
                        placeholder="Teléfono"
                        value={nuevoMecanico.telefono || ""}
                        onChange={e => setNuevoMecanico({ ...nuevoMecanico, telefono: e.target.value })}
                    />
                    <button type="submit" className="btn-bs btn-primary w-100 mt-2">
                        {editMode ? 'Actualizar' : 'Registrar'}
                    </button>
                </form>
            </div>

            <div className="card-panel mt-4">
                <div className="custom-table-container">
                    <div className="custom-table-header">
                        <div>Nombre</div>
                        <div>Especialidad</div>
                        <div className="text-center">Acciones</div>
                    </div>
                    {mecanicos.map(m => (
                        <div className="custom-table-row" key={m.id_mecanico}>
                            <div>{m.nombre}</div>
                            <div>{m.especialidad}</div>
                            <div className="text-center">
                                <button className="btn-bs btn-success btn-sm" onClick={() => { setEditMode(true); setNuevoMecanico(m); }}>
                                    <i className="fa-solid fa-pen"></i>
                                </button>
                                <button className="btn-bs btn-danger btn-sm" onClick={() => handleEliminar(m.id_mecanico)}>
                                    <i className="fa-solid fa-trash"></i>
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default Mecanico;