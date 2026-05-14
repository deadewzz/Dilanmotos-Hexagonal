import { useEffect, useState } from "react";
import { authFetch } from "../api";

export default function Servicio() {
    const [clientes, setClientes] = useState([]);
    const [motos, setMotos] = useState([]);
    const [tiposServicio, setTiposServicio] = useState([]);
    const [historial, setHistorial] = useState([]);
    
    const [nuevo, setNuevo] = useState({
        idUsuario: '',
        idMoto: '',
        idTipoServicio: '',
        reporte: ''
    });

    // Recuperamos el token del localStorage
    const token = localStorage.getItem('token');

    const cargarBase = async () => {
        if (!token) {
            console.error("No hay token disponible. Inicia sesión de nuevo.");
            return;
        }

        try {
            const opciones = {
                method: 'GET',
                headers: { 
                    'Authorization': `Bearer ${token}`, // Espacio vital después de Bearer
                    'Content-Type': 'application/json'
                }
            };

            // Ejecutamos las peticiones en paralelo para mayor velocidad
            const [resU, resM, resT] = await Promise.all([
                fetch('http://localhost:8080/api/usuarios', opciones),
                fetch('http://localhost:8080/api/motos', opciones),
                fetch('http://localhost:8080/api/tiposervicio', opciones)
            ]);

            // Solo ejecutamos .json() si el status es 200 (ok)
            if (resU.ok && resM.ok && resT.ok) {
                const dataU = await resU.json();
                const dataM = await resM.json();
                const dataT = await resT.json();
                
                setClientes(dataU);
                setMotos(dataM);
                setTiposServicio(dataT);
            } else {
                console.error("Error de autenticación o ruta no encontrada (401/404)");
            }
        } catch (error) {
            console.error("Error cargando base de datos:", error);
        }
    };

    useEffect(() => {
        cargarBase();
    }, []);

    const registrarServicio = async (e) => {
        e.preventDefault();
        
        const payload = {
            usuario: { idUsuario: parseInt(nuevo.idUsuario) },
            moto: { idMoto: parseInt(nuevo.idMoto) },
            tipoServicio: { idTipoServicio: parseInt(nuevo.idTipoServicio) },
            reporte: nuevo.reporte,
            fecha: new Date().toISOString()
        };

        try {
            const res = await fetch('http://localhost:8080/api/servicios', {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}` 
                },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                alert("Servicio registrado correctamente");
                setNuevo({ idUsuario: '', idMoto: '', idTipoServicio: '', reporte: '' });
                // Opcional: cargarHistorial(nuevo.idUsuario);
            } else {
                alert("Error al registrar: " + res.status);
            }
        } catch (error) {
            alert("Error de conexión");
        }
    };

    return (
        <div className="main-content-inner">
            <div className="card-panel">
                <h3 className="text-primary text-center">🛠️ Nuevo Servicio</h3>
                <form onSubmit={registrarServicio}>
                    <div className="mb-3">
                        <label className="form-label text-muted">Cliente</label>
                        <select className="input-bs" value={nuevo.idUsuario} onChange={e => setNuevo({...nuevo, idUsuario: e.target.value})} required>
                            <option value="">Seleccione Cliente...</option>
                            {clientes.map(u => <option key={u.idUsuario} value={u.idUsuario}>{u.nombre}</option>)}
                        </select>
                    </div>

                    <div className="mb-3">
                        <label className="form-label text-muted">Moto</label>
                        <select className="input-bs" value={nuevo.idMoto} onChange={e => setNuevo({...nuevo, idMoto: e.target.value})} required>
                            <option value="">Seleccione Moto...</option>
                            {motos.map(m => <option key={m.idMoto} value={m.idMoto}>{m.modelo} - {m.placa || 'Sin Placa'}</option>)}
                        </select>
                    </div>

                    <div className="mb-3">
                        <label className="form-label text-muted">Tipo de Servicio</label>
                        <select className="input-bs" value={nuevo.idTipoServicio} onChange={e => setNuevo({...nuevo, idTipoServicio: e.target.value})} required>
                            <option value="">Seleccione Servicio...</option>
                            {tiposServicio.map(t => <option key={t.idTipoServicio} value={t.idTipoServicio}>{t.nombre}</option>)}
                        </select>
                    </div>

                    <div className="mb-3">
                        <textarea 
                            className="input-bs" 
                            placeholder="Reporte o comentario del cliente..." 
                            value={nuevo.reporte}
                            onChange={e => setNuevo({...nuevo, reporte: e.target.value})}
                            required
                        />
                    </div>

                    <button type="submit" className="btn-bs btn-primary w-100">Registrar Servicio</button>
                </form>

                <hr />
                <div className="mt-4 text-center">
                    <h5>Historial de Mantenimientos</h5>
                    {historial.length === 0 ? (
                        <p className="text-muted italic">No hay historial para mostrar.</p>
                    ) : (
                        <div className="list-group">
                            {/* Mapeo de historial aquí */}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}