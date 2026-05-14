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

    const idLogueado = localStorage.getItem('idUsuario');
    const token = localStorage.getItem('token'); // Importante para autenticación
    const handleEnviar = async (e) => {
        e.preventDefault();
        
        if (!idLogueado || !token) {
            alert("Sesion expirada. Inicia sesion de nuevo.");
            return;
        }
        
        setEnviando(true);

        // PAYLOAD SINCRONIZADO CON TU MODELO JAVA
        const nuevaPqrs = {
            idUsuario: parseInt(idLogueado), 
            tipo: formulario.tipo,
            asunto: formulario.asunto,
            descripcion: formulario.descripcion,
            estado: 'PENDIENTE',
            
            comentario_usuario: formulario.descripcion, 
            // Inicializamos estos para evitar errores de nulos si la DB los pide
            respuesta_admin: "Sin respuesta",
            calificacion_servicio: "-",
            comentario_servicio: "-"
        };

        console.log("Enviando datos:", nuevaPqrs);

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
                alert("Solicitud radicada con exito.");
                navigate('/dashboard'); 
            } else {
                const errorTxt = await res.text();
                console.error("Error 500 detallado:", errorTxt);
                alert("Error en el servidor. Revisa que comentario_usuario no este vacio.");
            }
        } catch (error) {
            console.error("Error de conexion:", error);
            alert("No hay conexion con el servidor.");
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
                            <label className="fw-bold d-block mb-2">Tipo de Tramite</label>
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
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="fw-bold d-block mb-2">Mensaje / Descripcion</label>
                            <textarea 
                                className="input-bs" 
                                rows="6"
                                style={{resize: 'none'}}
                                value={formulario.descripcion}
                                onChange={(e) => setFormulario({...formulario, descripcion: e.target.value})}
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