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

    const handleEnviar = async (e) => {
        e.preventDefault();
        
        // 1. Recuperar datos de sesión
        const idAlmacenado = localStorage.getItem('idUsuario');
        const token = localStorage.getItem('token');
        
        // 2. Convertir y Validar el ID (Evita el envío de NaN)
        const idNumerico = parseInt(idAlmacenado);

        if (isNaN(idNumerico) || !token) {
            alert("Sesión no válida o expirada. Por favor, inicia sesión nuevamente.");
            navigate('/login'); // Redirigir si no hay sesión válida
            return;
        }
        
        setEnviando(true);

        // 3. Payload sincronizado exactamente con PqrsRequestDTO.java
        const nuevaPqrs = {
            idUsuario: idNumerico, 
            tipo: formulario.tipo,
            asunto: formulario.asunto,
            descripcion: formulario.descripcion
        };

        console.log("Enviando datos al servidor:", nuevaPqrs);

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
                // Mostrar el mensaje de error específico que viene de las validaciones de Java
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
                    <button onClick={() => navigate('/dashboard')} className="btn-bs btn-danger btn-sm">
                         Volver
                    </button>
                    <h3 className="text-white m-0">Nueva Solicitud</h3>
                    <div style={{width: '60px'}}></div>
                </div>
            </header>

            <main className="dashboard-content">
                <div className="card-panel shadow" style={{maxWidth: '600px', margin: '0 auto', background: 'white'}}>
                    <h2 className="text-primary mb-4">Radicar PQRS</h2>
                    <form onSubmit={handleEnviar}>
                        <div className="mb-3">
                            <label className="fw-bold d-block mb-2">Tipo de Trámite</label>
                            <select 
                                className="input-bs"
                                value={formulario.tipo}
                                onChange={(e) => setFormulario({...formulario, tipo: e.target.value})}
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
                                className="input-bs" 
                                value={formulario.asunto}
                                onChange={(e) => setFormulario({...formulario, asunto: e.target.value})}
                                placeholder="Ej: Problema con repuesto"
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="fw-bold d-block mb-2">Mensaje / Descripción</label>
                            <textarea 
                                className="input-bs" 
                                rows="6"
                                style={{resize: 'none'}}
                                value={formulario.descripcion}
                                onChange={(e) => setFormulario({...formulario, descripcion: e.target.value})}
                                placeholder="Detalla tu solicitud aquí..."
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