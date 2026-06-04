import { useEffect, useState } from "react";
import '../global.css';

export default function Usuarios() {
    const [usuarios, setUsuarios] = useState([]);
    const [nuevo, setNuevo] = useState({ nombre: '', correo: '', contrasena: '', idReferencia: 1 });
    const [editMode, setEditMode] = useState(false);
    const [selectedId, setSelectedId] = useState(null);
    const [mensaje, setMensaje] = useState('');

    const token = localStorage.getItem('token');

    const cargar = async () => {
        try {
            const r = await fetch('http://localhost:8080/api/usuarios', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            const d = await r.json();
            setUsuarios(Array.isArray(d) ? d : []);
        } catch (e) { 
            console.error("Error al cargar:", e); 
            setMensaje("❌ No se pudieron cargar los usuarios.");
        }
    };

    useEffect(() => { 
        cargar(); 
    }, []);

    // FUNCIÓN DE VALIDACIÓN MANUAL EN JS
    const validarFormulario = () => {
        const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/;
        const regexCorreo = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.com$/;

        if (!regexNombre.test(nuevo.nombre.trim())) {
            setMensaje("❌ El nombre completo no puede contener números ni caracteres especiales.");
            return false;
        }

        if (!regexCorreo.test(nuevo.correo.trim())) {
            setMensaje("❌ El correo electrónico debe tener un formato válido y terminar en '.com' (Ej: usuario@gmail.com).");
            return false;
        }

        // VALIDACIÓN DE CONTRASEÑA (Mínimo 6 caracteres)
        // En registro siempre es obligatoria. En edición solo se valida si el usuario escribió algo.
        if (!editMode || nuevo.contrasena.length > 0) {
            if (nuevo.contrasena.length < 6) {
                setMensaje("❌ La contraseña debe tener al menos 6 caracteres.");
                return false;
            }
        }

        return true;
    };

    const guardar = async (e) => {
        e.preventDefault();
        setMensaje('');

        if (!validarFormulario()) return;

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
                body: JSON.stringify({
                    ...nuevo,
                    nombre: nuevo.nombre.trim(),
                    correo: nuevo.correo.trim().toLowerCase()
                })
            });
            
            if (res.ok) {
                setMensaje(editMode ? "✅ ¡Usuario actualizado con éxito!" : "✅ ¡Usuario registrado exitosamente!");
                resetForm();
                cargar();
            } else {
                const errorData = await res.text();
                setMensaje(`❌ Error: ${errorData}`);
            }
        } catch (error) { 
            setMensaje("❌ Error de conexión con el servidor."); 
        }
    };

    const iniciarEdicion = (u) => {
        const id = u.idUsuario || u.id_usuario;
        setEditMode(true);
        setSelectedId(id);
        setNuevo({ nombre: u.nombre, correo: u.correo, contrasena: '', idReferencia: 1 });
        setMensaje('');
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const eliminar = async (id) => {
        if (!id || id === "undefined") return;
        if (!window.confirm("¿Estás seguro de que deseas eliminar este usuario?")) return;
        
        try {
            const res = await fetch(`http://localhost:8080/api/usuarios/${id}`, { 
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (res.ok) { 
                cargar(); 
                setMensaje("✅ Registro eliminado con éxito.");
            } else {
                setMensaje("❌ No se pudo eliminar el usuario seleccionado.");
            }
        } catch (e) { 
            setMensaje("❌ Error al intentar conectar para eliminar."); 
        }
    };

    const resetForm = () => {
        setNuevo({ nombre: '', correo: '', contrasena: '', idReferencia: 1 });
        setEditMode(false);
        setSelectedId(null);
    };

    const gridLayoutTabla = {
        display: 'grid',
        gridTemplateColumns: '80px 2fr 2fr 120px 150px',
        gap: '15px',
        alignItems: 'center',
        padding: '15px',
        minWidth: '800px'
    };

    return (
        <div className="main-content-inner">
            
            {/* PANEL DEL FORMULARIO */}
            <div className="card-panel">
                <h3 className="text-primary mb-4">
                    {editMode ? '📝 Editar Usuario' : '👥 Gestión de Usuarios'}
                </h3>

                {mensaje && (
                    <div className="alert alert-info fw-bold mb-3">
                        {mensaje}
                    </div>
                )}

                <form onSubmit={guardar}>
                    <div className="mb-3">
                        <label className="form-label fw-bold">Nombre Completo</label>
                        <input 
                            className="input-bs" 
                            type="text"
                            placeholder="Ej: Juan Pérez" 
                            value={nuevo.nombre} 
                            onChange={e => setNuevo({...nuevo, nombre: e.target.value})} 
                            pattern="^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$"
                            title="El nombre solo debe contener letras y espacios."
                            required 
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Correo Electrónico</label>
                        <input 
                            className="input-bs" 
                            type="text" 
                            placeholder="nombre@correo.com" 
                            value={nuevo.correo} 
                            onChange={e => setNuevo({...nuevo, correo: e.target.value})} 
                            pattern="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.com$"
                            title="El correo debe ser válido y terminar estrictamente en '.com'."
                            required 
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Contraseña</label>
                        <input 
                            className="input-bs" 
                            type="password" 
                            placeholder={editMode ? "Dejar vacío para mantener la contraseña actual" : "Asigne una contraseña segura (mín. 6 caracteres)"} 
                            value={nuevo.contrasena} 
                            onChange={e => setNuevo({...nuevo, contrasena: e.target.value})} 
                            // Propiedad HTML5: Valida la longitud mínima nativamente en el navegador
                            minLength={editMode ? undefined : 6}
                            title="La contraseña debe tener un mínimo de 6 caracteres."
                            required={!editMode} 
                        />
                    </div>
                    
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }} className="mt-4">
                        <button 
                            type="submit" 
                            className={`btn-bs w-100 ${editMode ? 'btn-success' : 'btn-success'}`}
                            style={{ padding: '12px', fontSize: '1rem' }}
                        >
                            {editMode ? 'Actualizar Cambios' : 'Registrar Usuario'}
                        </button>
                        {editMode && (
                            <button 
                                type="button" 
                                className="btn-bs btn-danger w-100" 
                                onClick={() => { resetForm(); setMensaje(''); }}
                                style={{ padding: '12px', fontSize: '1rem' }}
                            >
                                Cancelar Edición
                            </button>
                        )}
                    </div>
                </form>
            </div>

            {/* PANEL DE LA TABLA */}
            <div className="card-panel mt-4">
                <h4 className="mb-4">📋 Listado de Usuarios Registrados</h4>

                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    
                    {/* ENCABEZADO */}
                    <div style={{ 
                        ...gridLayoutTabla, 
                        background: 'var(--header-table)', 
                        color: 'var(--white)', 
                        fontWeight: 'bold' 
                    }}>
                        <div>ID</div>
                        <div>Nombre</div>
                        <div>Correo Electrónico</div>
                        <div>Estado</div>
                        <div style={{ display: 'flex', justifyContent: 'center' }}>Acciones</div>
                    </div>

                    {/* FILAS */}
                    {usuarios.length > 0 ? (
                        usuarios.map(u => {
                            const currentId = u.idUsuario || u.id_usuario;
                            return (
                                <div 
                                    className="table-row-hover-effect"
                                    style={{ 
                                        ...gridLayoutTabla, 
                                        borderBottom: '1px solid #eee',
                                        background: 'var(--white)',
                                        transition: '0.2s'
                                    }} 
                                    key={currentId}
                                >
                                    <div style={{ color: 'var(--text-dark)', fontWeight: 'bold' }}>#{currentId}</div>
                                    <div style={{ fontWeight: '600', color: '#212529' }}>{u.nombre}</div>
                                    <div style={{ color: '#4b5563', wordBreak: 'break-all' }}>{u.correo}</div>
                                    <div>
                                        <span style={{ backgroundColor: '#e8f5e9', color: '#2e7d32', padding: '5px 10px', borderRadius: '6px', fontSize: '0.78rem', fontWeight: 'bold' }}>
                                            ACTIVO
                                        </span>
                                    </div>
                                    <div style={{ display: 'flex', justifyContent: 'center', gap: '8px' }}>
                                        <button 
                                            className="btn-bs btn-success btn-sm" 
                                            style={{ padding: '6px 12px' }} 
                                            onClick={() => iniciarEdicion(u)}
                                        >
                                            <i className="fa-solid fa-pen"></i>
                                        </button>
                                        <button 
                                            className="btn-bs btn-danger btn-sm" 
                                            style={{ padding: '6px 12px' }} 
                                            onClick={() => eliminar(currentId)}
                                        >
                                            <i className="fa-solid fa-trash"></i>
                                        </button>
                                    </div>
                                </div>
                            );
                        })
                    ) : (
                        <div className="p-4 text-center text-muted">No se encontraron usuarios registrados en el sistema.</div>
                    )}
                </div>
            </div>
        </div>
    );
}