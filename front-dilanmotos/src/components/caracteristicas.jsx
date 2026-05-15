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
        } catch (e) { console.error("Error cargando:", e); }
    };

    useEffect(() => { cargarDatos(); }, []);

    const prepararEdicion = (c) => {
        setEditMode(true);
        setSelectedId(c.idCaracteristica);
        setNuevo({
            descripcion: c.descripcion,
            idMoto: c.idMoto || ''
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
                alert(" Guardado exitosamente");
                cancelarEdicion();
                cargarDatos();
            } else {
                const errorData = await res.status;
                alert(" Error " + errorData + ": Revisa los permisos o los datos.");
            }
        } catch (error) {
            alert("Error de conexión con el servidor");
        }
    };

    const eliminar = async (id) => {
        if(window.confirm("¿Estás seguro de eliminar este detalle?")) {
            await fetch(`${API_URL}/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            cargarDatos();
        }
    };

    return (
        <div className="main-content-inner">
            <div className="card-panel">
                <h3 className="text-primary">{editMode ? '📝 Editar Detalle' : '⚙️ Detalles de la Moto'}</h3>
                <form onSubmit={guardar}>
                    <label className="fw-bold">Descripción Técnica</label>
                    <textarea className="input-bs" value={nuevo.descripcion} onChange={e => setNuevo({...nuevo, descripcion: e.target.value})} required rows="3" />
                    
                    <label className="fw-bold mt-2">Moto Asignada</label>
                    <select className="input-bs" value={nuevo.idMoto} onChange={e => setNuevo({...nuevo, idMoto: e.target.value})} required>
                        <option value="">-- Seleccionar Moto --</option>
                        {motos.map(m => <option key={m.idMoto} value={m.idMoto}>{m.modelo}</option>)}
                    </select>

                    <div className="d-flex gap-2 mt-3">
                        <button type="submit" className={`btn-bs w-100 ${editMode ? 'btn-warning' : 'btn-primary'}`}>
                            {editMode ? 'Actualizar' : 'Registrar'}
                        </button>
                        {editMode && <button type="button" className="btn-bs btn-secondary" onClick={cancelarEdicion}>Cancelar</button>}
                    </div>
                </form>
            </div>

            <div className="card-panel mt-4">
                <div className="custom-table-container">
                    <div className="custom-table-header">
                        <div>ID</div><div>Descripción</div><div>Moto</div><div className="text-center">Acciones</div>
                    </div>
                    {caracteristicas.map(c => (
                        <div className="custom-table-row" key={c.idCaracteristica}>
                            <div>#{c.idCaracteristica}</div>
                            <div className="text-wrap">{c.descripcion}</div>
                            {/* AQUÍ SE QUITA EL N/A USANDO EL OBJETO MOTO */}
                            <div className="fw-bold text-primary">{c.moto?.modelo || 'Cargando...'}</div>
                            <div className="text-center d-flex justify-content-center gap-2">
                                <button className="btn-bs btn-warning btn-sm" onClick={() => prepararEdicion(c)}><i className="fa-solid fa-pen"></i></button>
                                <button className="btn-bs btn-danger btn-sm" onClick={() => eliminar(c.idCaracteristica)}><i className="fa-solid fa-trash"></i></button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}