import { useEffect, useState } from "react";
import { authFetch } from "../api";

export default function Usuarios() {
    const [usuarios, setUsuarios] = useState([]);
    const [nuevo, setNuevo] = useState({ nombre: '', correo: '', contrasena: '', idReferencia: 1 });
    const [editMode, setEditMode] = useState(false);
    const [selectedId, setSelectedId] = useState(null);

    // Obtener el token del localStorage
    const token = localStorage.getItem('token');

    const cargar = async () => {
        try {
            const r = await fetch('http://localhost:8080/api/usuarios', {
                headers: { 'Authorization': `Bearer ${token}` } // Agregado
            });
            const d = await r.json();
            setUsuarios(d);
        } catch (e) { console.error("Error al cargar:", e); }
    };

    useEffect(() => { cargar(); }, []);

    const guardar = async (e) => {
        e.preventDefault();
        const url = editMode 
            ? `http://localhost:8080/api/usuarios/${selectedId}` 
            : 'http://localhost:8080/api/usuarios';
        const method = editMode ? 'PUT' : 'POST';

        try {
            const res = await fetch(url, {
                method: method,
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}` 
                },
                body: JSON.stringify(nuevo)
            });
            
            if (res.ok) {
                setNuevo({ nombre: '', correo: '', contrasena: '', idReferencia: 1 });
                setEditMode(false);
                setSelectedId(null);
                cargar();
                alert(editMode ? "Usuario actualizado" : "Usuario registrado exitosamente");
            } else {
                const errorData = await res.text();
                alert("Error: " + errorData);
            }
        } catch (error) { alert("Error de conexión"); }
    };

    const iniciarEdicion = (u) => {
        window.scrollTo(0, 0);
        const id = u.idUsuario || u.id_usuario;
        setEditMode(true);
        setSelectedId(id);
        setNuevo({ nombre: u.nombre, correo: u.correo, contrasena: '', idReferencia: 1 });
    };

    const eliminar = async (id) => {
        if (!id || id === "undefined") return;
        if (!window.confirm("¿Eliminar este usuario?")) return;
        
        try {
            const res = await fetch(`http://localhost:8080/api/usuarios/${id}`, { 
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` } // Agregado
            });
            if (res.ok) { cargar(); alert("Eliminado"); }
        } catch (e) { alert("Error al eliminar"); }
    };

    return (
        <div className="main-content-inner">
            <div className="card-panel">
                <h3 className="text-primary">{editMode ? 'Editar Usuario' : 'Gestión de Usuarios'}</h3>
                <hr className="mb-3" />
                <form onSubmit={guardar}>
                    <input className="input-bs" placeholder="Nombre completo" value={nuevo.nombre} onChange={e => setNuevo({...nuevo, nombre: e.target.value})} required />
                    <input className="input-bs" placeholder="Correo electrónico" value={nuevo.correo} onChange={e => setNuevo({...nuevo, correo: e.target.value})} required />
                    <input className="input-bs" type="password" placeholder={editMode ? "Contraseña (dejar vacío para no cambiar)" : "Contraseña"} value={nuevo.contrasena} onChange={e => setNuevo({...nuevo, contrasena: e.target.value})} required={!editMode} />
                    
                    <button type="submit" className={`btn-bs ${editMode ? 'btn-success' : 'btn-primary'} w-100`}>
                        {editMode ? 'Guardar Cambios' : 'Registrar Usuario'}
                    </button>
                    {editMode && (
                        <button type="button" className="btn-bs btn-danger w-100 mt-2" onClick={() => {setEditMode(false); setNuevo({nombre:'', correo:'', contrasena:'', idReferencia: 1})}}>
                            Cancelar Edición
                        </button>
                    )}
                </form>
            </div>

            <div className="card-panel mt-4">
                <div className="custom-table-container">
                    <div className="custom-table-header">
                        <div>ID</div>
                        <div>Nombre</div>
                        <div>Correo</div>
                        <div>Estado</div>
                        <div className="text-center">Acciones</div>
                    </div>

                    {usuarios.map(u => {
                        const currentId = u.idUsuario || u.id_usuario;
                        return (
                            <div className="custom-table-row" key={currentId}>
                                <div>#{currentId}</div>
                                <div className="fw-bold">{u.nombre}</div>
                                <div>{u.correo}</div>
                                <div style={{color: 'var(--success)', fontWeight: 'bold'}}>ACTIVO</div>
                                <div className="text-center">
                                    <button className="btn-bs btn-success btn-sm" onClick={() => iniciarEdicion(u)}>
                                        <i className="fa-solid fa-pen"></i>
                                    </button>
                                    <button className="btn-bs btn-danger btn-sm" onClick={() => eliminar(currentId)}>
                                        <i className="fa-solid fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        );
                    })}
                </div>
            </div>
        </div>
    );
}