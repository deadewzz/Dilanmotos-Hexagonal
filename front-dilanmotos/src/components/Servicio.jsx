import React, { useEffect, useState } from 'react';
import '../global.css';

const Servicio = () => {
    const token = localStorage.getItem('token');

    const [usuarios, setUsuarios] = useState([]);
    const [mecanicos, setMecanicos] = useState([]); // Nuevo: Para id_mecanico
    const [tipos, setTipos] = useState([]);
    const [historial, setHistorial] = useState([]);
    
    const [editMode, setEditMode] = useState(false);
    const [editId, setEditId] = useState(null);

    // Estado inicial basado exactamente en tu tabla DB
    const [form, setForm] = useState({
        id_usuario: '',
        id_mecanico: '',
        id_tipo_servicio: '',
        fecha_servicio: new Date().toISOString().split('T')[0],
        estado_servicio: 'PENDIENTE',
        comentario: '',
        puntuacion: 5,
        visible_en_historial: true
    });

    const API = 'http://localhost:8080/api/servicios';

    useEffect(() => {
        const cargarDatosMaestros = async () => {
            if (!token) return;
            try {
                // Carga paralela de todas las dependencias
                const [resU, resM, resT] = await Promise.all([
                    fetch('http://localhost:8080/api/usuarios', { headers: { 'Authorization': `Bearer ${token}` }}),
                    fetch('http://localhost:8080/api/mecanicos', { headers: { 'Authorization': `Bearer ${token}` }}),
                    fetch('http://localhost:8080/api/tipoServicio', { headers: { 'Authorization': `Bearer ${token}` }})
                ]);

                const dataU = await resU.json();
                const dataM = await resM.json();
                const dataT = await resT.json();

                setUsuarios(Array.isArray(dataU) ? dataU : []);
                setMecanicos(Array.isArray(dataM) ? dataM : []);
                setTipos(Array.isArray(dataT) ? dataT : []);
            } catch(e) { 
                console.error("Error cargando datos maestros:", e); 
            }
        };
        cargarDatosMaestros();
    }, [token]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const url = editMode ? `${API}/${editId}` : API;
        try {
            const res = await fetch(url, {
                method: editMode ? 'PUT' : 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(form)
            });

            if (res.ok) {
                alert(editMode ? "Actualizado" : "Registrado");
                window.location.reload();
            }
        } catch (e) { alert("Error al conectar"); }
    };

    return (
        <div className="card-panel">
            <h3>{editMode ? '✏️ Editar Registro' : '🛠️ Nuevo Registro de Servicio'}</h3>
            <form onSubmit={handleSubmit} className="row">
                
                {/* SELECT USUARIO */}
                <div className="col-md-4">
                    <label>Cliente</label>
                    <select className="input-bs" required value={form.id_usuario} 
                        onChange={e => setForm({...form, id_usuario: e.target.value})}>
                        <option value="">Seleccione Cliente...</option>
                        {usuarios.map(u => <option key={u.id_usuario} value={u.id_usuario}>{u.nombre}</option>)}
                    </select>
                </div>

                {/* SELECT MECANICO */}
                <div className="col-md-4">
                    <label>Mecánico Responsable</label>
                    <select className="input-bs" required value={form.id_mecanico}
                        onChange={e => setForm({...form, id_mecanico: e.target.value})}>
                        <option value="">Seleccione Mecánico...</option>
                        {mecanicos.map(m => <option key={m.id_mecanico} value={m.id_mecanico}>{m.nombre}</option>)}
                    </select>
                </div>

                {/* SELECT TIPO SERVICIO */}
                <div className="col-md-4">
                    <label>Tipo de Servicio</label>
                    <select className="input-bs" required value={form.id_tipo_servicio}
                        onChange={e => setForm({...form, id_tipo_servicio: e.target.value})}>
                        <option value="">Seleccione Servicio...</option>
                        {tipos.map(t => <option key={t.id_tipo_servicio} value={t.id_tipo_servicio}>{t.nombre}</option>)}
                    </select>
                </div>

                <div className="col-md-6 mt-3">
                    <label>Fecha</label>
                    <input type="date" className="input-bs" value={form.fecha_servicio}
                        onChange={e => setForm({...form, fecha_servicio: e.target.value})} />
                </div>

                <div className="col-md-6 mt-3">
                    <label>Puntuación (1-5)</label>
                    <input type="number" min="1" max="5" className="input-bs" value={form.puntuacion}
                        onChange={e => setForm({...form, puntuacion: e.target.value})} />
                </div>

                <textarea className="col-12 input-bs mt-3" required value={form.comentario}
                    onChange={e => setForm({...form, comentario: e.target.value})} placeholder="Comentario o reporte del cliente..." />

                <button type="submit" className="btn-bs btn-primary w-100 mt-3">
                    {editMode ? 'Actualizar Servicio' : 'Registrar en DB'}
                </button>
            </form>
        </div>
    );
};

export default Servicio;