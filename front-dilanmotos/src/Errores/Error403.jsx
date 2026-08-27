import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../global.css';

export default function Error403() {
    const navigate = useNavigate();

    return (
        <div className="d-flex flex-column align-items-center justify-content-center vh-100 bg-light text-center p-4">
            <div className="card-panel shadow-lg p-5" style={{ maxWidth: '500px', borderRadius: '15px' }}>
                <h1 className="display-1 fw-bold text-danger mb-0">403</h1>
                <i className="fa-solid fa-shield-halved text-danger my-3" style={{ fontSize: '4rem' }}></i>
                <h3 className="fw-bold text-dark mb-2">Acceso Denegado</h3>
                <p className="text-muted mb-4">
                    No tienes los permisos o el rol necesario (Administrador) para acceder al módulo de gestión de PQRS.
                </p>
                <div className="d-flex justify-content-center gap-3">
                    <button 
                        onClick={() => navigate(-1)} 
                        className="btn-bs btn-secondary px-4 py-2"
                    >
                        <i className="fa-solid fa-arrow-left me-2"></i>Volver
                    </button>
                    <button 
                        onClick={() => navigate('/dashboard')} 
                        className="btn-bs btn-primary px-4 py-2"
                    >
                        <i className="fa-solid fa-house me-2"></i>Ir al Inicio
                    </button>
                </div>
            </div>
        </div>
    );
}