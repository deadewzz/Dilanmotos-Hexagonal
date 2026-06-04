import React, { useState, useEffect } from 'react';

const Referencia = () => {
    const [marcas, setMarcas] = useState([]);
    const [todasLasReferencias, setTodasLasReferencias] = useState([]);
    const [filtroMarca, setFiltroMarca] = useState(''); 
    const [editMode, setEditMode] = useState(false);
    const [selectedId, setSelectedId] = useState(null);
    const [formData, setFormData] = useState({
        nombre: '', 
        cilindraje: '', 
        idMarca: ''
    });
    
    const token = localStorage.getItem('token');

    const cargarTodo = async () => {
        try {
            const [resMarcas, resRefs] = await Promise.all([
                fetch("http://localhost:8080/api/marcas", {
                    headers: { 'Authorization': `Bearer ${token}` }
                }),
                fetch("http://localhost:8080/api/referencias", {
                    headers: { 'Authorization': `Bearer ${token}` }
                })
            ]);
            if (resMarcas.ok) setMarcas(await resMarcas.json());
            if (resRefs.ok) setTodasLasReferencias(await resRefs.json());
        } catch (error) {
            console.error("Error cargando datos:", error);
        }
    };

    useEffect(() => { 
        cargarTodo(); 
    }, []);

    const referenciasFiltradas = filtroMarca === '' 
        ? todasLasReferencias 
        : todasLasReferencias.filter(ref => (ref.marca?.idMarca || ref.idMarca) === parseInt(filtroMarca));

    const prepararEdicion = (ref) => {
        setEditMode(true);
        setSelectedId(ref.idReferencia);
        setFormData({
            nombre: ref.nombre || '',
            cilindraje: ref.cilindraje || '',
            idMarca: ref.marca?.idMarca || ref.idMarca || ''
        });
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const url = editMode 
            ? `http://localhost:8080/api/referencias/${selectedId}` 
            : "http://localhost:8080/api/referencias";
        
        const payload = {
            nombre: formData.nombre,
            cilindraje: parseFloat(formData.cilindraje),
            marca: { idMarca: parseInt(formData.idMarca) }
        };

        try {
            const res = await fetch(url, {
                method: editMode ? "PUT" : "POST",
                headers: { 
                    "Content-Type": "application/json",
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                alert(editMode ? "✅ Modelo actualizado" : "✅ Modelo agregado al catálogo");
                setFormData({ nombre: '', cilindraje: '', idMarca: '' });
                setEditMode(false);
                cargarTodo();
            } else {
                alert("❌ Error en servidor: " + res.status);
            }
        } catch (err) {
            console.error(err);
        }
    };

    return (
        <div className="main-content-inner">
            <div className="card-panel">
                <h3 className="text-primary mb-4">
                    {editMode ? '✏️ Editar Modelo' : '⚙️ Nueva Referencia'}
                </h3>
                <form onSubmit={handleSubmit} className="row">
                    <div className="col-md-4 mb-3">
                        <label className="form-label">Marca</label>
                        <select 
                            className="input-bs" 
                            value={formData.idMarca} 
                            onChange={e => setFormData({...formData, idMarca: e.target.value})} 
                            required
                        >
                            <option value="">Seleccione Marca</option>
                            {marcas.map(m => (
                                <option key={m.idMarca} value={m.idMarca}>{m.nombre}</option>
                            ))}
                        </select>
                    </div>
                    <div className="col-md-4 mb-3">
                        <label className="form-label">Nombre del Modelo</label>
                        <input 
                            className="input-bs" 
                            type="text" 
                            value={formData.nombre} 
                            onChange={e => setFormData({...formData, nombre: e.target.value})} 
                            required 
                        />
                    </div>
                    <div className="col-md-4 mb-3">
                        <label className="form-label">Cilindraje</label>
                        <input 
                            className="input-bs" 
                            type="number" 
                            value={formData.cilindraje} 
                            onChange={e => setFormData({...formData, cilindraje: e.target.value})} 
                            required 
                        />
                    </div>
                    <div className="col-12 mt-2 d-flex gap-2">
                        <button className={`btn-bs w-100 ${editMode ? 'btn-primary' : 'btn-primary'}`} type="submit">
                            {editMode ? 'Actualizar Cambios' : 'Guardar en Catálogo'}
                        </button>
                        {editMode && (
                            <button 
                                type="button" 
                                className="btn-bs btn-danger" 
                                onClick={() => {
                                    setEditMode(false); 
                                    setFormData({nombre:'', cilindraje:'', idMarca:''});
                                }}
                            >
                                Cancelar
                            </button>
                        )}
                    </div>
                </form>
            </div>

            <div className="card-panel mt-4">
                <div className="row align-items-center mb-3">
                    <div className="col-md-6">
                        <h4 className="text-muted m-0">📚 Modelos Registrados</h4>
                    </div>
                    <div className="col-md-6 d-flex align-items-center gap-2">
                        <span className="text-nowrap fw-bold" style={{ color: 'var(--text-dark)' }}>Filtrar por:</span>
                        <select className="input-bs m-0" value={filtroMarca} onChange={(e) => setFiltroMarca(e.target.value)}>
                            <option value="">-- Ver todas las marcas --</option>
                            {marcas.map(m => (
                                <option key={m.idMarca} value={m.idMarca}>{m.nombre}</option>
                            ))}
                        </select>
                    </div>
                </div>

                {/* Contenedor con bordes y radio unificados de tu CSS */}
                <div style={{ width: '100%', overflowX: 'auto', background: 'var(--white)', borderRadius: '10px', border: '1px solid #dee2e6' }}>
                    
                    {/* CABECERA CON COLOR DEL CSS (--header-table) Y ALINEACIÓN PERFECTA */}
                    <div style={{ 
                        display: 'grid', 
                        gridTemplateColumns: '1.5fr 2.5fr 1.2fr 1.3fr', 
                        gap: '15px', 
                        alignItems: 'center', 
                        padding: '15px',
                        background: 'var(--header-table)',
                        color: 'var(--white)',
                        fontWeight: 'bold',
                        minWidth: '600px'
                    }}>
                        <div>Marca</div>
                        <div>Modelo</div>
                        <div>Cilindraje</div>
                        <div style={{ display: 'flex', justifyContent: 'center' }}>Acciones</div>
                    </div>

                    {referenciasFiltradas.length > 0 ? (
                        referenciasFiltradas.map(ref => (
                            /* FILA ALINEADA CON HOVER E INTERSECCIONES DEL CSS */
                            <div key={ref.idReferencia} style={{ 
                                display: 'grid', 
                                gridTemplateColumns: '1.5fr 2.5fr 1.2fr 1.3fr', 
                                gap: '15px', 
                                alignItems: 'center', 
                                padding: '15px',
                                borderBottom: '1px solid #eee',
                                minWidth: '600px',
                                background: 'var(--white)',
                                transition: '0.2s'
                            }}>
                                <div className="fw-bold" style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', color: 'var(--text-dark)' }} title={ref.marca?.nombre || 'S/M'}>
                                    {ref.marca?.nombre || 'S/M'}
                                </div>
                                <div style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', color: '#7e8299' }} title={ref.nombre}>
                                    {ref.nombre}
                                </div>
                                <div style={{ color: 'var(--text-dark)' }}>
                                    {ref.cilindraje} cc
                                </div>
                                <div className="text-center">
                                    <button className="btn-bs btn-primary btn-sm" style={{ padding: '6px 12px' }} onClick={() => prepararEdicion(ref)}>
                                        <i className="fa-solid fa-pen"></i>
                                    </button>
                                    <button 
                                        className="btn-bs btn-danger btn-sm" 
                                        style={{ padding: '6px 12px' }}
                                        onClick={async () => {
                                            if(window.confirm("¿Eliminar del catálogo?")) {
                                                await fetch(`http://localhost:8080/api/referencias/${ref.idReferencia}`, {
                                                    method:'DELETE',
                                                    headers: { 'Authorization': `Bearer ${token}` }
                                                });
                                                cargarTodo();
                                            }
                                        }}
                                    >
                                        <i className="fa-solid fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="p-4 text-center text-muted">No hay modelos para esta marca.</div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Referencia;