import React, { useState } from 'react';

export const BackupView = () => {
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);

  const handleDownloadBackup = async () => {
    setLoading(true);
    setMessage(null);

    try {
      const token = localStorage.getItem('token'); // Recupera el token JWT
      
      const response = await fetch('http://localhost:8080/api/backup/download', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error('No se pudo generar la copia de seguridad.');
      }

      // Convertir la respuesta a Blob para iniciar la descarga
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      
      // Formato de nombre con fecha actual
      const dateStr = new Date().toISOString().slice(0, 10);
      a.download = `backup_dilanmotos_${dateStr}.sql`;
      
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);

      setMessage({ type: 'success', text: '¡Copia de seguridad generada y descargada con éxito!' });
    } catch (error) {
      console.error(error);
      setMessage({ type: 'danger', text: 'Error al generar la copia de seguridad. Revisa la consola o logs del servidor.' });
    } finally {
        setLoading(false);
    }
  };

  return (
    <div className="container-fluid p-4">
      <div className="card shadow-sm border-0 rounded-3">
        <div className="card-body p-4">
          <h4 className="card-title fw-bold text-primary mb-3">
            📦 Gestión de Copias de Seguridad (Backup)
          </h4>
          <p className="text-muted">
            Desde este apartado puedes generar un respaldo completo de la base de datos de <strong>Dilan Motos</strong> en formato <code>.sql</code>.
          </p>

          <hr />

          {message && (
            <div className={`alert alert-${message.type} mt-3`} role="alert">
              {message.text}
            </div>
          )}

          <div className="d-flex align-items-center gap-3 mt-4">
            <button 
              onClick={handleDownloadBackup} 
              disabled={loading}
              className="btn btn-success px-4 py-2 fw-semibold"
            >
              {loading ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  Generando respaldo...
                </>
              ) : (
                '⬇️ Descargar Copia de Seguridad (.sql)'
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default BackupView;