import { useEffect, useState } from "react";

export default function Motos() {
    const [motos, setMotos] = useState([]);
    const [marcas, setMarcas] = useState([]);
    const [tiposServicio, setTiposServicio] = useState([]); 
    const [nuevo, setNuevo] = useState({ 
        modelo: '', 
        cilindraje: '', 
        idMarca: '', 
        idTipoServicio: ''  
    });
    const [editMode, setEditMode] = useState(false);
    const [selectedId, setSelectedId] = useState(null);

    const token = localStorage.getItem('token');
    const API_URL = 'http://localhost:8080/api/motos';
    const MARCAS_URL = 'http://localhost:8080/api/marcas';
    const TIPOS_SERVICIO_URL = 'http://localhost:8080/api/tipoServicio';

    const cargarDatos = async () => {
        try {
            const [resM, resMa, resTs] = await Promise.all([  // ✅ Agregamos resTs
                fetch(API_URL, {
                    headers: { 'Authorization': `Bearer ${token}` }
                }),
                fetch(MARCAS_URL, {
                    headers: { 'Authorization': `Bearer ${token}` }
                }),
                fetch(TIPOS_SERVICIO_URL, {  // ✅ Cargar tipos de servicio
                    headers: { 'Authorization': `Bearer ${token}` }
                })
            ]);

            if (resM.ok && resMa.ok && resTs.ok) {
                const dataMotos = await resM.json();
                const dataMarcas = await resMa.json();
                const dataTiposServicio = await resTs.json();
                setMotos(Array.isArray(dataMotos) ? dataMotos : []);
                setMarcas(Array.isArray(dataMarcas) ? dataMarcas : []);
                setTiposServicio(Array.isArray(dataTiposServicio) ? dataTiposServicio : []);
            }
        } catch (error) {
            console.error("Error cargando datos:", error);
        }
    };

    useEffect(() => {
        cargarDatos();
    }, []);

    const prepararEdicion = (moto) => {
        setEditMode(true);
        setSelectedId(moto.idMoto);
        setNuevo({
            modelo: moto.modelo,
            cilindraje: moto.cilindraje,
            idMarca: moto.idMarca || '',
            idTipoServicio: moto.idTipoServicio || '' 
        });
        window.scrollTo({ top: 0, behavior: 'smooth' }); 
    };

    const guardar = async (e) => {
        e.preventDefault();
        
        const url = editMode ? `${API_URL}/${selectedId}` : API_URL;
        
     
        const payload = {
            idUsuario: 1,
            idMarca: parseInt(nuevo.idMarca),
            modelo: nuevo.modelo,
            cilindraje: parseFloat(nuevo.cilindraje),
            idTipoServicio: parseInt(nuevo.idTipoServicio)  
        };

        try {
            const res = await fetch(url, {
                method: editMode ? 'PUT' : 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                alert(editMode ? " Moto actualizada!" : " Moto guardada!");
                cancelarEdicion();
                await cargarDatos();
            } else {
                alert("Error al procesar la solicitud");
            }
        } catch (error) {
            alert("Error de conexión");
        }
    };

    const eliminarMoto = async (id) => {
        if (window.confirm("¿Estás seguro de eliminar esta moto?")) {
            try {
                const res = await fetch(`${API_URL}/${id}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                if (res.ok) {
                    await cargarDatos();
                }
            } catch (error) {
                console.error("Error al eliminar:", error);
            }
        }
    };

    const cancelarEdicion = () => {
        setEditMode(false);
        setSelectedId(null);
        setNuevo({ modelo: '', cilindraje: '', idMarca: '', idTipoServicio: '' });
    };

    return (
        <div className="main-content-inner">
            <div className="card-panel">
                <h3 className="text-primary">
                    {editMode ? ' Editar Moto' : ' Registro de Motos'}
                </h3>
                <form onSubmit={guardar}>
                    <div className="row">
                        <div className="col-md-6 mb-3">
                            <label className="form-label">Marca</label>
                            <select 
                                className="input-bs" 
                                value={nuevo.idMarca} 
                                onChange={e => setNuevo({...nuevo, idMarca: e.target.value})} 
                                required
                            >
                                <option value="">Seleccione marca</option>
                                {marcas.map(m => (
                                    <option key={m.idMarca} value={m.idMarca}>
                                        {m.nombre}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="col-md-6 mb-3">
                            <label className="form-label">Modelo (Año/Nombre)</label>
                            <input 
                                className="input-bs" 
                                value={nuevo.modelo} 
                                onChange={e => setNuevo({...nuevo, modelo: e.target.value})} 
                                placeholder="Ej: 2024 / Pulsar NS"
                                required 
                            />
                        </div>
                    </div>
                    
                    <div className="row">
                        <div className="col-md-6 mb-3">
                            <label className="form-label">Cilindraje (cc)</label>
                            <input 
                                className="input-bs" 
                                type="number" 
                                value={nuevo.cilindraje} 
                                onChange={e => setNuevo({...nuevo, cilindraje: e.target.value})} 
                                required 
                            />
                        </div>
                        <div className="col-md-6 mb-3">
                            <label className="form-label">Tipo de Servicio</label>
                            <select
                                className="input-bs"
                                value={nuevo.idTipoServicio}
                                onChange={e => setNuevo({...nuevo, idTipoServicio: e.target.value})}
                                required
                            >
                                <option value="">Seleccione un tipo de servicio</option>
                                {tiposServicio.map(t => (
                                    <option key={t.idTipoServicio} value={t.idTipoServicio}>
                                        {t.nombre}
                                    </option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div className="d-flex gap-2">
                        <button type="submit" className={`btn-bs w-100 ${editMode ? 'btn-warning' : 'btn-primary'}`}>
                            {editMode ? 'Actualizar Cambios' : 'Guardar Moto'}
                        </button>
                        {editMode && (
                            <button type="button" className="btn-bs btn-secondary" onClick={cancelarEdicion}>
                                Cancelar
                            </button>
                        )}
                    </div>
                </form>
            </div>

            <div className="card-panel mt-4">
                <div className="custom-table-container">
                    <div className="custom-table-header">
                        <div>Marca</div>
                        <div>Modelo</div>
                        <div>Cilindraje</div>
                        <div>Tipo Servicio</div>  {/*  Nueva columna */}
                        <div className="text-center">Acciones</div>
                    </div>
                    {motos.length > 0 ? (
                        motos.map(m => (
                            <div className="custom-table-row" key={m.idMoto}>
                                <div className="fw-bold">
                                    {m.marca?.nombre || 'S/M'}
                                </div>
                                <div>{m.modelo}</div>
                                <div>{m.cilindraje}cc</div>
                                <div>{m.tipoServicio?.nombre || 'No especificado'}</div>  {/*  Mostrar nombre del servicio */}
                                <div className="text-center d-flex justify-content-center gap-2">
                                    <button 
                                        className="btn-bs btn-warning btn-sm" 
                                        onClick={() => prepararEdicion(m)}
                                        title="Editar"
                                    >
                                        <i className="fa-solid fa-pen"></i>
                                    </button>
                                    <button 
                                        className="btn-bs btn-danger btn-sm" 
                                        onClick={() => eliminarMoto(m.idMoto)}
                                        title="Eliminar"
                                    >
                                        <i className="fa-solid fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="p-3 text-center text-muted">No hay motos registradas.</div>
                    )}
                </div>
            </div>
        </div>
    );
}