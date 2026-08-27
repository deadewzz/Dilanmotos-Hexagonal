import React, { useState } from 'react';
import './BackupCSS.css'; 

export const BackupView = () => {
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);

  const handleDownloadBackup = async () => {
    setLoading(true);
    setMessage(null);

    try {
      const token = localStorage.getItem('token');
      const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

      const response = await fetch(`${API_URL}/api/backup/download`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        if (response.status === 403) {
          throw new Error('Permisos insuficientes (Requiere rol de Administrador).');
        }
        throw new Error('No se pudo generar la copia de seguridad.');
      }

      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;

      const dateStr = new Date().toISOString().slice(0, 10);
      a.download = `backup_dilanmotos_${dateStr}.sql`;

      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);

      setMessage({
        type: 'success',
        text: 'Copia de seguridad generada y descargada con éxito.'
      });
    } catch (error) {
      console.error(error);
      setMessage({
        type: 'danger',
        text: error.message || 'Error al conectar con el servidor.'
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="backup-wrapper">
      
      {/* Encabezado */}
      <div className="backup-header">
        <h2>Gestión de Respaldos</h2>
        <p>Administra y exporta la base de datos de Dilan Motos.</p>
      </div>

      {/* Tarjeta Principal */}
      <div className="backup-card">
        
        <div className="backup-content">
          <div className="backup-icon-box">
            <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M4 7v10c0 2.21 3.582 4 8 4s8-1.79 8-4V7M4 7c0 2.21 3.582 4 8 4s8-1.79 8-4M4 7c0-2.21 3.582-4 8-4s8 1.79 8 4m0 5c0 2.21-3.582 4-8 4s-8-1.79-8-4" />
            </svg>
          </div>

          <div className="backup-text">
            <h3>Copia de Seguridad (.sql)</h3>
            <p>
              Genera una instantánea completa con todas las tablas del sistema (motos, repuestos, usuarios y cotizaciones).
            </p>
          </div>
        </div>

        {/* Notificaciones de Estado */}
        {message && (
          <div className={`backup-alert ${message.type}`}>
            <span>{message.text}</span>
            <button className="backup-alert-close" onClick={() => setMessage(null)}>
              &times;
            </button>
          </div>
        )}

        {/* Botón de Acción */}
        <button
          onClick={handleDownloadBackup}
          disabled={loading}
          className="backup-btn"
        >
          {loading ? (
            <>
              <span className="spinner-minimal"></span>
              <span>Generando respaldo...</span>
            </>
          ) : (
            <>
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
                <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
              </svg>
              <span>Descargar Copia de Seguridad</span>
            </>
          )}
        </button>

      </div>
    </div>
  );
};

export default BackupView;