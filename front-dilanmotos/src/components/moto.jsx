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
            const [resM, resMa, resTs] = await Promise.all([
                fetch(API_URL, {
                    headers: { 'Authorization': `Bearer ${token}` }
                }),
                fetch(MARCAS_URL, {
                    headers: { 'Authorization': `Bearer ${token}` }
                }),
                fetch(TIPOS_SERVICIO_URL, {
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
                alert(editMode ? "¡Moto actualizada!" : "¡Moto guardada!");
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

    // Estilos inline de fuerza bruta aplicados a la estructura y tabla de Motos
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

    // Grid personalizado de 5 columnas para los datos de Motos
    const gridLayoutTabla = {
        display: 'grid',
        gridTemplateColumns: '2fr 2fr 1.5fr 2fr 150px', 
        alignItems: 'center',
        padding: '12px 15px',
        minWidth: '850px', 
        boxSizing: 'border-box'
    };

    return (
        <div style={containerForzado}>
            {/* PANEL DEL FORMULARIO DE REGISTRO */}
            <div style={panelForzado}>
                <h3 className="text-primary" style={{ margin: '0 0 15px 0', color: '#0d6efd' }}>
                    {editMode ? 'Editar Usuario' : 'Registro de Motos'}
                </h3>
                <hr style={{ border: '0', borderTop: '1px solid #eee', marginBottom: '15px' }} />
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

                    {/* SECCIÓN DE BOTONES CONFIGURADOS EN VERTICAL CON SUS RESPECTIVOS COLORES */}
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginTop: '15px' }}>
                        <button 
                            type="submit" 
                            className={`btn-bs ${editMode ? 'btn-success' : 'btn-success'}`}
                            style={{ width: '100%', padding: '12px', fontSize: '1rem' }}
                        >
                            {editMode ? 'Guardar Cambios' : 'Guardar Moto'}
                        </button>
                        
                        {editMode && (
                            <button 
                                type="button" 
                                className="btn-bs btn-danger" 
                                onClick={cancelarEdicion}
                                style={{ width: '100%', padding: '12px', fontSize: '1rem' }}
                            >
                                Cancelar Edición
                            </button>
                        )}
                    </div>
                </form>
            </div>

            {/* PANEL DE LA LISTA / TABLA */}
            <div style={panelForzado}>
                <div style={{ width: '100%', overflowX: 'auto', border: '1px solid #dee2e6', borderRadius: '8px', background: '#fff' }}>
                    
                    {/* ENCABEZADO DE LA TABLA */}
                    <div style={{ ...gridLayoutTabla, backgroundColor: '#343a40', color: '#ffffff', fontWeight: 'bold' }}>
                        <div>Marca</div>
                        <div>Modelo</div>
                        <div>Cilindraje</div>
                        <div>Tipo Servicio</div>
                        <div style={{ textAlign: 'center', display: 'block', width: '100%' }}>Acciones</div>
                    </div>

                    {/* CUERPO DE LA TABLA */}
                    {motos.length > 0 ? (
                        motos.map(m => (
                            <div style={{ ...gridLayoutTabla, borderBottom: '1px solid #f1f1f1' }} key={m.idMoto}>
                                <div style={{ fontWeight: '600', color: '#212529' }}>
                                    {m.marca?.nombre || 'S/M'}
                                </div>
                                <div style={{ color: '#495057' }}>{m.modelo}</div>
                                <div>
                                    <span style={{ backgroundColor: '#e9ecef', color: '#495057', padding: '3px 8px', borderRadius: '4px', fontSize: '13px' }}>
                                        {m.cilindraje}cc
                                    </span>
                                </div>
                                <div style={{ color: '#495057' }}>{m.tipoServicio?.nombre || 'No especificado'}</div>
                                <div style={{ display: 'flex', justifyContent: 'center', gap: '6px' }}>
                                    <button 
                                        className="btn-bs btn-success btn-sm" 
                                        style={{ padding: '4px 8px', height: 'auto' }}
                                        onClick={() => prepararEdicion(m)}
                                        title="Editar"
                                    >
                                        <i className="fa-solid fa-pen" style={{ fontSize: '12px' }}></i>
                                    </button>
                                    <button 
                                        className="btn-bs btn-danger btn-sm" 
                                        style={{ padding: '4px 8px', height: 'auto' }}
                                        onClick={() => eliminarMoto(m.idMoto)}
                                        title="Eliminar"
                                    >
                                        <i className="fa-solid fa-trash" style={{ fontSize: '12px' }}></i>
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div style={{ padding: '20px', textAlign: 'center', color: '#6c757d' }}>
                            No hay motos registradas.
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}