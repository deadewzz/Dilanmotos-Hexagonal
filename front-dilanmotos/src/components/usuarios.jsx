import { useEffect, useState } from "react";
import { authFetch } from "../api";

export default function Usuarios() {
    const [usuarios, setUsuarios] = useState([]);
    const [nuevo, setNuevo] = useState({ nombre: '', correo: '', contrasena: '', idReferencia: 1 });
    const [editMode, setEditMode] = useState(false);
    const [selectedId, setSelectedId] = useState(null);

    const token = localStorage.getItem('token');

    const cargar = async () => {
        try {
            const r = await fetch('http://localhost:8080/api/usuarios', {
                headers: { 'Authorization': `Bearer ${token}` }
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
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (res.ok) { cargar(); alert("Eliminado"); }
        } catch (e) { alert("Error al eliminar"); }
    };

    // Estilos inline de fuerza bruta con !important lógicos
    const containerForzado = {
        width: '100%',
        maxWidth: '100%',
        display: 'block',
        boxSizing: 'border-box'
    };

    const panelForzado = {
        width: '100%',
        maxWidth: '100%',
        background: '#ffffff',
        borderRadius: '12px',
        padding: '25px',
        boxSizing: 'border-box',
        marginBottom: '20px',
        boxShadow: '0 4px 12px rgba(0,0,0,0.05)'
    };

    const gridLayoutTabla = {
        display: 'grid',
        gridTemplateColumns: '80px 2fr 2fr 120px 150px', // Anchos explícitos fijos para cada celda
        alignItems: 'center',
        padding: '12px 15px',
        minWidth: '800px', // Evita que las columnas colapsen entre sí
        boxSizing: 'border-box'
    };

    return (
        <div style={containerForzado}>
            {/* PANEL DEL FORMULARIO */}
            <div style={panelForzado}>
                <h3 className="text-primary" style={{ margin: '0 0 15px 0', color: '#0d6efd' }}>
                    {editMode ? 'Editar Usuario' : 'Gestión de Usuarios'}
                </h3>
                <hr style={{ border: '0', borderTop: '1px solid #eee', marginBottom: '15px' }} />
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

            {/* PANEL DE LA TABLA */}
            <div style={panelForzado}>
                {/* Contenedor con scroll horizontal garantizado si la pantalla es pequeña */}
                <div style={{ width: '100%', overflowX: 'auto', border: '1px solid #dee2e6', borderRadius: '8px' }}>
                    
                    {/* ENCABEZADO */}
                    <div style={{ ...gridLayoutTabla, backgroundColor: '#343a40', color: '#ffffff', fontWeight: 'bold' }}>
                        <div>ID</div>
                        <div>Nombre</div>
                        <div>Correo</div>
                        <div>Estado</div>
                        <div style={{ textAlign: 'center', display: 'block', width: '100%' }}>Acciones</div>
                    </div>

                    {/* FILAS */}
                    {usuarios.map(u => {
                        const currentId = u.idUsuario || u.id_usuario;
                        return (
                            <div style={{ ...gridLayoutTabla, borderBottom: '1px solid #f1f1f1' }} key={currentId}>
                                <div style={{ color: '#6c757d' }}>#{currentId}</div>
                                <div style={{ fontWeight: '600', color: '#212529' }}>{u.nombre}</div>
                                <div style={{ wordBreak: 'break-all' }}>{u.correo}</div>
                                <div>
                                    <span style={{ backgroundColor: '#e8f5e9', color: '#2e7d32', padding: '4px 8px', borderRadius: '4px', fontSize: '12px', fontWeight: 'bold' }}>
                                        ACTIVO
                                    </span>
                                </div>
                                <div style={{ display: 'flex', justifyContent: 'center', gap: '6px' }}>
                                    <button className="btn-bs btn-success btn-sm" style={{ padding: '4px 8px', height: 'auto' }} onClick={() => iniciarEdicion(u)}>
                                        <i className="fa-solid fa-pen" style={{ fontSize: '12px' }}></i>
                                    </button>
                                    <button className="btn-bs btn-danger btn-sm" style={{ padding: '4px 8px', height: 'auto' }} onClick={() => eliminar(currentId)}>
                                        <i className="fa-solid fa-trash" style={{ fontSize: '12px' }}></i>
                                    </button>
                                </div>
                            </div>
                        );
                    })}

                    {usuarios.length === 0 && (
                        <div style={{ padding: '20px', textAlign: 'center', color: '#6c757d' }}>
                            No hay usuarios registrados.
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}