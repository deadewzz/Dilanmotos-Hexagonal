import { useEffect, useState } from "react";
import { authFetch } from "../api";

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
            const opciones = {
                headers: { 'Authorization': `Bearer ${token}` }
            };
            const [resCar, resMoto] = await Promise.all([
                fetch(API_URL, opciones),
                fetch('http://localhost:8080/api/motos', opciones)
            ]);
            
            if (resCar.ok && resMoto.ok) {
                const dataCar = await resCar.json();
                const dataMoto = await resMoto.json();
                setCaracteristicas(dataCar);
                setMotos(dataMoto);
            }
        } catch (e) { console.error("Error cargando:", e); }
    };

    useEffect(() => { cargarDatos(); }, []);

    const guardar = async (e) => {
        e.preventDefault();
        
        const payload = {
            descripcion: nuevo.descripcion,
            moto: { idMoto: parseInt(nuevo.idMoto) }
        };

        try {
            const res = await fetch(editMode ? `${API_URL}/${selectedId}` : API_URL, {
                method: editMode ? 'PUT' : 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                alert("✅ Guardado correctamente");
                setNuevo({ descripcion: '', idMoto: '' });
                setEditMode(false);
                cargarDatos();
            } else {
                alert("Error 500: Revisa que la descripción no sea nula.");
            }
        } catch (error) { alert("Error de conexión"); }
    };

    return (
        <div className="main-content-inner">
            <div className="card-panel">
                <h3 className="text-primary">⚙️ Detalles de la Moto</h3>
                <hr />
                <form onSubmit={guardar}>
                    <label className="fw-bold">Descripción del detalle</label>
                    <textarea 
                        className="input-bs" 
                        placeholder="Ej: Motor 250cc de 4 tiempos con inyección electrónica..." 
                        value={nuevo.descripcion} 
                        onChange={e => setNuevo({...nuevo, descripcion: e.target.value})} 
                        required 
                        rows="3"
                    />
                    
                    <label className="fw-bold mt-2">Asignar a Vehículo</label>
                    <select className="input-bs" value={nuevo.idMoto} onChange={e => setNuevo({...nuevo, idMoto: e.target.value})} required>
                        <option value="">-- Seleccionar Moto --</option>
                        {motos.map(m => (
                            <option key={m.idMoto} value={m.idMoto}>{m.modelo} (ID: {m.idMoto})</option>
                        ))}
                    </select>

                    <button type="submit" className="btn-bs btn-primary w-100 mt-3">
                        {editMode ? 'Actualizar Detalle' : 'Registrar Detalle'}
                    </button>
                </form>
            </div>

            <div className="card-panel mt-4">
                <div className="custom-table-container">
                    <div className="custom-table-header">
                        <div>ID</div>
                        <div>Descripción Técnica</div>
                        <div>Moto</div>
                        <div className="text-center">Acciones</div>
                    </div>
                    {caracteristicas.map(c => (
                        <div className="custom-table-row" key={c.idCaracteristica}>
                            <div>#{c.idCaracteristica}</div>
                            <div className="text-wrap">{c.descripcion}</div>
                            <div className="fw-bold text-primary">{c.moto?.modelo || 'N/A'}</div>
                            <div className="text-center">
                                <button className="btn-bs btn-danger btn-sm" onClick={async () => {
                                    if(window.confirm("¿Borrar?")) {
                                        await fetch(`${API_URL}/${c.idCaracteristica}`, {
                                            method:'DELETE',
                                            headers: { 'Authorization': `Bearer ${token}` }
                                        });
                                        cargarDatos();
                                    }
                                }}>
                                    <i className="fa-solid fa-trash"></i>
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}