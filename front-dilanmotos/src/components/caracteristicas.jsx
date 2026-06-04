import { useEffect, useState } from "react";

export default function Caracteristicas() {
    const [caracteristicas, setCaracteristicas] = useState([]);
    const [motos, setMotos] = useState([]);
    const [nuevo, setNuevo] = useState({ descripcion: '', idMoto: '' });
    const [editMode, setEditMode] = useState(false);
    const [selectedId, setSelectedId] = useState(null);

    const API_URL = 'http://localhost:8080/api/caracteristicas';
    const token = localStorage.getItem('token');

    const cargarDatos = async () => {
        try {
            const opciones = { headers: { 'Authorization': `Bearer ${token}` } };
            const [resCar, resMoto] = await Promise.all([
                fetch(API_URL, opciones),
                fetch('http://localhost:8080/api/motos', opciones)
            ]);
            
            if (resCar.ok && resMoto.ok) {
                setCaracteristicas(await resCar.json());
                setMotos(await resMoto.json());
            }
        } catch (e) { 
            console.error("Error cargando:", e); 
        }
    };

    useEffect(() => { 
        cargarDatos(); 
    }, []);

    const prepararEdicion = (c) => {
        setEditMode(true);
        setSelectedId(c.idCaracteristica);
        setNuevo({
            descripcion: c.descripcion || '',
            idMoto: c.moto?.idMoto || c.idMoto || ''
        });
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const cancelarEdicion = () => {
        setEditMode(false);
        setSelectedId(null);
        setNuevo({ descripcion: '', idMoto: '' });
    };

    const guardar = async (e) => {
        e.preventDefault();
        const url = editMode ? `${API_URL}/${selectedId}` : API_URL;

        try {
            const res = await fetch(url, {
                method: editMode ? 'PUT' : 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}` 
                },
                body: JSON.stringify({
                    descripcion: nuevo.descripcion,
                    idMoto: parseInt(nuevo.idMoto)
                })
            });

            if (res.ok) {
                alert(editMode ? "✅ Detalle actualizado exitosamente" : "✅ Detalle guardado exitosamente");
                cancelarEdicion();
                cargarDatos();
            } else {
                alert("❌ Error " + res.status + ": Revisa los permisos o los datos.");
            }
        } catch (error) {
            alert("Error de conexión con el servidor");
        }
    };

    const eliminar = async (id) => {
        if(window.confirm("¿Estás seguro de eliminar este detalle?")) {
            try {
                const res = await fetch(`${API_URL}/${id}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                if (res.ok) {
                    cargarDatos();
                } else {
                    alert("No se pudo eliminar el registro.");
                }
            } catch (error) {
                console.error(error);
            }
        }
    };

    return (
        <div className="main-content-inner">
            <div className="card-panel">
                <h3 className="text-primary mb-4">
                    {editMode ? '📝 Editar Detalle' : '⚙️ Detalles de la Moto'}
                </h3>
                <form onSubmit={guardar}>
                    <div className="mb-3">
                        <label className="form-label fw-bold">Descripción Técnica</label>
                        <textarea 
                            className="input-bs" 
                            value={nuevo.descripcion} 
                            onChange={e => setNuevo({...nuevo, descripcion: e.target.value})} 
                            required 
                            rows="3" 
                            placeholder="Ej: Sistema de frenado ABS doble canal con discos ventilados..."
                        />
                    </div>
                    
                    <div className="mb-3">
                        <label className="form-label fw-bold">Moto Asignada</label>
                        <select 
                            className="input-bs" 
                            value={nuevo.idMoto} 
                            onChange={e => setNuevo({...nuevo, idMoto: e.target.value})} 
                            required
                        >
                            <option value="">-- Seleccionar Moto --</option>
                            {motos.map(m => (
                                <option key={m.idMoto} value={m.idMoto}>{m.modelo}</option>
                            ))}
                        </select>
                    </div>

                    <div className="d-flex gap-2 mt-2">
                        <button type="submit" className="btn-bs w-100 btn-primary">
                            {editMode ? 'Actualizar Cambios' : 'Registrar Características'}
                        </button>
                        {editMode && (
                            <button type="button" className="btn-bs btn-danger" onClick={cancelarEdicion}>
                                Cancelar
                            </button>
                        )}
                    </div>
                </form>
            </div>

            <div className="card-panel mt-4">
                {/* Contenedor responsivo con bordes unificados de tu CSS */}
                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    
                    {/* CABECERA CON COLOR DEL CSS (--header-table): Distribución ID, Descripción, Moto, Acciones */}
                    <div style={{ 
                        display: 'grid', 
                        gridTemplateColumns: '0.8fr 3fr 1.5fr 1.2fr', 
                        gap: '15px', 
                        alignItems: 'center', 
                        padding: '15px',
                        background: 'var(--header-table)',
                        color: 'var(--white)',
                        fontWeight: 'bold',
                        minWidth: '600px'
                    }}>
                        <div>ID</div>
                        <div>Descripción Técnica</div>
                        <div>Moto</div>
                        <div style={{ display: 'flex', justifyContent: 'center' }}>Acciones</div>
                    </div>

                    {caracteristicas.length > 0 ? (
                        caracteristicas.map(c => (
                            /* FILAS CON ALINEACIÓN PERFECTA E INTERSECCIONES */
                            <div key={c.idCaracteristica} style={{ 
                                display: 'grid', 
                                gridTemplateColumns: '0.8fr 3fr 1.5fr 1.2fr', 
                                gap: '15px', 
                                alignItems: 'center', 
                                padding: '15px',
                                borderBottom: '1px solid #eee',
                                minWidth: '600px',
                                background: 'var(--white)',
                                transition: '0.2s'
                            }}>
                                <div style={{ color: '#7e8299' }}>#{c.idCaracteristica}</div>
                                <div style={{ whiteSpace: 'normal', display: '-webkit-box', WebkitLineClamp: '2', WebkitBoxOrient: 'vertical', overflow: 'hidden', textOverflow: 'ellipsis', color: 'var(--text-dark)' }} title={c.descripcion}>
                                    {c.descripcion}
                                </div>
                                <div className="fw-bold" style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', color: 'var(--primary)' }}>
                                    {c.moto?.modelo || 'S/M'}
                                </div>
                                <div className="text-center">
                                    <button className="btn-bs btn-primary btn-sm" style={{ padding: '6px 12px' }} onClick={() => prepararEdicion(c)}>
                                        <i className="fa-solid fa-pen"></i>
                                    </button>
                                    <button className="btn-bs btn-danger btn-sm" style={{ padding: '6px 12px' }} onClick={() => eliminar(c.idCaracteristica)}>
                                        <i className="fa-solid fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="p-4 text-center text-muted">No hay especificaciones o detalles técnicos registrados.</div>
                    )}
                </div>
            </div>
        </div>
    );
}