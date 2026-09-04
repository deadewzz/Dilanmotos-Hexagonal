import { useEffect, useState } from "react";
import '../global.css';
import { API_BASE_URL } from '../api';

export default function Usuarios() {
    const [usuarios, setUsuarios] = useState([]);
    const [marcas, setMarcas] = useState([]);
    const [referencias, setReferencias] = useState([]);
    const [tiposServicio, setTiposServicio] = useState([]);
    
    // Agregamos la placa al estado inicial
    const [nuevo, setNuevo] = useState({ nombre: '', correo: '', contrasena: '', idReferencia: '', idMarca: '', cilindraje: '', idTipoServicio: '', placa: '' });
    
    const [editMode, setEditMode] = useState(false);
    const [selectedId, setSelectedId] = useState(null);
    const [mensaje, setMensaje] = useState('');

    const token = localStorage.getItem('token');

    const cargar = async () => {
        try {
            const r = await fetch(`${API_BASE_URL}/api/usuarios`, {
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
        fetch(`${API_BASE_URL}/api/marcas`)
            .then(r => r.ok ? r.json() : [])
            .then(d => setMarcas(Array.isArray(d) ? d : []))
            .catch(() => setMarcas([]));

        fetch(`${API_BASE_URL}/api/tipoServicio`)
            .then(r => r.ok ? r.json() : [])
            .then(d => setTiposServicio(Array.isArray(d) ? d : []))
            .catch(() => setTiposServicio([]));
    }, []);

    const handleMarcaChange = (e) => {
        const idMarca = e.target.value;
        setReferencias([]);
        setNuevo(prev => ({ ...prev, idReferencia: '' , idMarca }));

        if (idMarca) {
            fetch(`${API_BASE_URL}/api/referencias?marcaId=${idMarca}`)
                .then(res => res.ok ? res.json() : [])
                .then(data => setReferencias(Array.isArray(data) ? data : []))
                .catch(() => setReferencias([]));
        }
    };

    const validarFormulario = () => {
        const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/;
        const regexCorreo = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.com$/;

        if (!regexNombre.test(nuevo.nombre.trim())) {
            setMensaje("❌ El nombre completo no puede contener números ni caracteres especiales.");
            return false;
        }

        if (!regexCorreo.test(nuevo.correo.trim())) {
            setMensaje("❌ El correo debe terminar en '.com' (Ej: usuario@gmail.com).");
            return false;
        }

        if (!editMode || nuevo.contrasena.length > 0) {
            if (nuevo.contrasena.length < 6 || nuevo.contrasena.length > 20) {
                setMensaje("❌ La contraseña debe tener al menos 6 caracteres y maximo 20.");
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
            ? `${API_BASE_URL}/api/usuarios/${selectedId}` 
            : `${API_BASE_URL}/api/usuarios`;
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
                    correo: nuevo.correo.trim().toLowerCase(),
                    placa: nuevo.placa ? nuevo.placa.trim().toUpperCase() : '',
                    idReferencia: nuevo.idReferencia ? parseInt(nuevo.idReferencia) : null,
                    idTipoServicio: nuevo.idTipoServicio && nuevo.idTipoServicio !== '0' ? parseInt(nuevo.idTipoServicio) : null,
                    cilindraje: nuevo.cilindraje ? parseFloat(nuevo.cilindraje) : null,
                    idMarca: nuevo.idMarca ? parseInt(nuevo.idMarca) : null
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
        setNuevo({ 
            nombre: u.nombre, 
            correo: u.correo, 
            contrasena: '', 
            idReferencia: u.idReferencia ?? u.id_referencia ?? '',
            idMarca: u.idMarca ?? u.id_marca ?? '',
            cilindraje: u.cilindraje ?? '',
            idTipoServicio: u.idTipoServicio ?? u.id_tipo_servicio ?? '',
            placa: (u.motos && u.motos.length > 0) ? u.motos[0].placa : ''
        });
        
        if (u.idMarca) {
            fetch(`${API_BASE_URL}/api/referencias?marcaId=${u.idMarca}`)
                .then(res => res.ok ? res.json() : [])
                .then(data => setReferencias(Array.isArray(data) ? data : []))
                .catch(() => setReferencias([]));
        }
        setMensaje('');
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const eliminar = async (id) => {
        if (!id || id === "undefined") return;
        if (!window.confirm("¿Estás seguro de que deseas eliminar este usuario?")) return;
        
        try {
            const res = await fetch(`${API_BASE_URL}/api/usuarios/${id}`, { 
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
        setNuevo({ nombre: '', correo: '', contrasena: '', idReferencia: '', idMarca: '', cilindraje: '', idTipoServicio: '', placa: '' });
        setEditMode(false);
        setSelectedId(null);
    };

    const gridLayoutTabla = {
        display: 'grid',
        gridTemplateColumns: '70px 1.5fr 1.5fr 1.5fr 100px 120px',
        gap: '15px',
        alignItems: 'center',
        padding: '15px',
        minWidth: '850px'
    };

    return (
        <div className="main-content-inner">
            <div className="card-panel">
                <h3 className="text-primary mb-4">{editMode ? '📝 Editar Usuario' : '👥 Gestión de Usuarios'}</h3>
                {mensaje && <div className="alert alert-info fw-bold mb-3">{mensaje}</div>}
                
                <form onSubmit={guardar}>
                    <div className="mb-3">
                        <label className="form-label fw-bold">Nombre Completo</label>
                        <input className="input-bs" type="text" value={nuevo.nombre} onChange={e => setNuevo({...nuevo, nombre: e.target.value})} pattern="^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$" required />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Correo Electrónico</label>
                        <input className="input-bs" type="text" value={nuevo.correo} onChange={e => setNuevo({...nuevo, correo: e.target.value})} pattern="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.com$" required />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Contraseña</label>
                        <input className="input-bs" type="password" value={nuevo.contrasena} onChange={e => setNuevo({...nuevo, contrasena: e.target.value})} minLength={editMode ? undefined : 6} required={!editMode} />
                    </div>
                    
                    <hr />
                    <h5 className="mb-3">Información de tu Moto</h5>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Marca de tu moto</label>
                        <select className="input-bs" value={nuevo.idMarca} onChange={handleMarcaChange} required={!editMode}>
                            <option value="">-- Selecciona una marca --</option>
                            {marcas.map(m => ( <option key={m.idMarca} value={m.idMarca}>{m.nombre}</option> ))}
                        </select>
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Modelo (de nuestro catálogo)</label>
                        <select className="input-bs" value={nuevo.idReferencia} onChange={e => setNuevo({ ...nuevo, idReferencia: e.target.value })} disabled={referencias.length === 0} required={!editMode}>
                            <option value="">{referencias.length === 0 ? "Primero elige una marca" : "-- Elige el modelo --"}</option>
                            {referencias.map(ref => ( <option key={ref.idReferencia} value={ref.idReferencia}>{ref.nombre}</option> ))}
                        </select>
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Placa de la Moto</label>
                        <input className="input-bs" type="text" value={nuevo.placa} onChange={e => setNuevo({ ...nuevo, placa: e.target.value.toUpperCase() })} maxLength={6} required={!editMode} />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Cilindraje (cc)</label>
                        <input className="input-bs" type="number" value={nuevo.cilindraje} onChange={e => setNuevo({ ...nuevo, cilindraje: e.target.value })} min={125} max={1400} step={1} required={!editMode} />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold">Tipo de Servicio</label>
                        <select className="input-bs" value={nuevo.idTipoServicio} onChange={e => setNuevo({ ...nuevo, idTipoServicio: e.target.value })} required={!editMode}>
                            <option value="">-- Selecciona un tipo de servicio --</option>
                            <option value="0">N/A</option>
                            {tiposServicio.map(t => ( <option key={(t.idTipoServicio ?? t.idTipo ?? t.id_tipo_servicio)} value={(t.idTipoServicio ?? t.idTipo ?? t.id_tipo_servicio)}>{t.nombre}</option> ))}
                        </select>
                    </div>
                    
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }} className="mt-4">
                        <button type="submit" className="btn-bs w-100 btn-success" style={{ padding: '12px', fontSize: '1rem' }}>
                            {editMode ? 'Actualizar Cambios' : 'Registrar Usuario'}
                        </button>
                        {editMode && (
                            <button type="button" className="btn-bs btn-danger w-100" onClick={() => { resetForm(); setMensaje(''); }} style={{ padding: '12px', fontSize: '1rem' }}>
                                Cancelar Edición
                            </button>
                        )}
                    </div>
                </form>
            </div>

            <div className="card-panel mt-4">
                <h4 className="mb-4">📋 Listado de Usuarios Registrados</h4>
                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    
                    <div style={{ ...gridLayoutTabla, background: 'var(--header-table)', color: 'var(--white)', fontWeight: 'bold' }}>
                        <div>ID</div>
                        <div>Nombre</div>
                        <div>Correo</div>
                        <div>Placa / Moto</div>
                        <div>Estado</div>
                        <div style={{ display: 'flex', justifyContent: 'center' }}>Acciones</div>
                    </div>

                    {usuarios.length > 0 ? (
                        usuarios.map(u => {
                            const currentId = u.idUsuario || u.id_usuario;
                            const moto = (u.motos && u.motos.length > 0) ? u.motos[0] : null;
                            return (
                                <div className="table-row-hover-effect" style={{ ...gridLayoutTabla, borderBottom: '1px solid #eee', background: 'var(--white)', transition: '0.2s' }} key={currentId}>
                                    <div style={{ color: 'var(--text-dark)', fontWeight: 'bold' }}>#{currentId}</div>
                                    <div style={{ fontWeight: '600', color: '#212529' }}>{u.nombre}</div>
                                    <div style={{ color: '#4b5563', wordBreak: 'break-all' }}>{u.correo}</div>
                                    <div style={{ color: '#0d6efd', fontWeight: 'bold' }}>
                                        {moto ? `${moto.placa || 'S/P'} - ${moto.modelo}` : <span className="text-muted">N/A</span>}
                                    </div>
                                    <div><span style={{ backgroundColor: '#e8f5e9', color: '#2e7d32', padding: '5px 10px', borderRadius: '6px', fontSize: '0.78rem', fontWeight: 'bold' }}>ACTIVO</span></div>
                                    <div style={{ display: 'flex', justifyContent: 'center', gap: '8px' }}>
                                        <button className="btn-bs btn-success btn-sm" style={{ padding: '6px 12px' }} onClick={() => iniciarEdicion(u)}><i className="fa-solid fa-pen"></i></button>
                                        <button className="btn-bs btn-danger btn-sm" style={{ padding: '6px 12px' }} onClick={() => eliminar(currentId)}><i className="fa-solid fa-trash"></i></button>
                                    </div>
                                </div>
                            );
                        })
                    ) : (
                        <div className="p-4 text-center text-muted">No se encontraron usuarios registrados.</div>
                    )}
                </div>
            </div>
        </div>
    );
}