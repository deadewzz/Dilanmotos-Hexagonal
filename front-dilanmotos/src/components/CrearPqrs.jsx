import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../global.css';

const CrearPqrs = () => {
    const navigate = useNavigate();
    const [enviando, setEnviando] = useState(false);
    const [formulario, setFormulario] = useState({
        asunto: '',
        descripcion: '',
        tipo: 'Peticion'
    });

    const handleChange = (e) => {
        setFormulario({
            ...formulario,
            [e.target.name]: e.target.value
        });
    };

    const handleEnviar = async (e) => {
        e.preventDefault();

        // Validar que la descripción tenga al menos 15 caracteres
        const textoDescripcion = (formulario.descripcion || '').trim();
        if (textoDescripcion.length < 15) {
            alert("La descripción debe tener al menos 15 caracteres.");
            return;
        }
        
        // 1. Recuperar datos de sesión
        const idAlmacenado = localStorage.getItem('idUsuario');
        const token = localStorage.getItem('token');
        
        // 2. Convertir y validar el ID
        const idNumerico = parseInt(idAlmacenado);

        if (isNaN(idNumerico) || !token) {
            alert("Sesión no válida o expirada. Por favor, inicia sesión nuevamente.");
            navigate('/login');
            return;
        }
        
        setEnviando(true);

        // 3. Payload sincronizado con PqrsRequestDTO.java
        const nuevaPqrs = {
            idUsuario: idNumerico, 
            tipo: formulario.tipo,
            asunto: formulario.asunto,
            descripcion: formulario.descripcion
        };

        try {
            const res = await fetch('http://localhost:8080/api/pqrs', {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(nuevaPqrs)
            });

            if (res.ok) {
                alert("Solicitud radicada con éxito.");
                navigate('/dashboard'); 
            } else {
                const errorData = await res.json();
                console.error("Error del servidor:", errorData);
                alert(errorData.message || "Error al radicar la solicitud. Revisa los campos.");
            }
        } catch (error) {
            console.error("Error de conexión:", error);
            alert("No hay conexión con el servidor. Verifica que XAMPP y el Backend estén corriendo.");
        } finally {
            setEnviando(false);
        }
    };

    return (
        <div className="dashboard-wrapper">
            <header className="dashboard-header">
                <div className="header-container">
                    <button onClick={() => navigate('/dashboard')} className="btn-back-clean">
                        <i className="fa-solid fa-arrow-left"></i>
                    </button>
                    <h3 className="text-white m-0" style={{ color: '#ffffff' }}>Nueva Solicitud</h3>
                    <div style={{ width: '60px' }}></div>
                </div>
            </header>

            <main className="dashboard-content">
                <div className="card-panel shadow" style={{ maxWidth: '600px', margin: '0 auto', background: 'white' }}>
                    <h2 className="text-primary mb-4">Radicar PQRS</h2>
                    <form onSubmit={handleEnviar}>
                        <div className="mb-3">
                            <label className="fw-bold d-block mb-2">Tipo de Trámite</label>
                            <select 
                                name="tipo"
                                className="input-bs"
                                value={formulario.tipo}
                                onChange={handleChange}
                            >
                                <option value="Peticion">Peticion</option>
                                <option value="Queja">Queja</option>
                                <option value="Reclamo">Reclamo</option>
                                <option value="Sugerencia">Sugerencia</option>
                            </select>
                        </div>

                        <div className="mb-3">
                            <label className="fw-bold d-block mb-2">Asunto</label>
                            <input 
                                type="text" 
                                name="asunto"
                                className="input-bs" 
                                value={formulario.asunto}
                                onChange={handleChange}
                                placeholder="Ej: Problema con repuesto"
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="fw-bold d-block mb-2">Mensaje / Descripción</label>
                            <textarea 
                                name="descripcion"
                                className="input-bs" 
                                rows="6"
                                style={{ resize: 'none' }}
                                value={formulario.descripcion}
                                onChange={handleChange}
                                placeholder="Detalla tu solicitud aquí (mínimo 15 caracteres)..."
                                required
                            ></textarea>
                        </div>

                        <button 
                            type="submit" 
                            className="btn-bs btn-primary w-100" 
                            disabled={enviando}
                        >
                            {enviando ? 'Enviando...' : 'Radicar PQRS'}
                        </button>
                    </form>
                </div>
            </main>
        </div>
    );
};

export default CrearPqrs;