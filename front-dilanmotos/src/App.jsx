import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link, NavLink, useLocation } from 'react-router-dom';

// --- IMPORTACIÓN DE COMPONENTES ---
import Login from './components/login';
import Register from './components/register'; 
import Dashboard from './components/dashboard';
import PerfilUsuario from './components/perfilUsuario';
import AsistenteMotos from './components/IA';
import RecomendACIONES from './components/RecomendacionesPanel';
import CrearPqrs from './components/CrearPqrs'; 
import ServicioAdmin from './components/Servicio';
import CatalogoKit from './components/catalogoKit';
import CatalogoAceites from './components/catalogoAceites';
import CatalogoLlantas from './components/catalogoLlantas';
import FichaTecnica from './components/fichaTecnica';

// Componentes de Gestión (ADMIN)
import Usuarios from './components/usuarios';
import Referencia from './components/referencia';
import Motos from './components/moto';
import Productos from './components/productos';
import Caracteristicas from './components/caracteristicas';
import TipoServicio from './components/tipoServicio';
import PqrsManager from './components/pqrs';
import Historial from './components/Historial';
import Mecanico from './components/mecanico';
import Cotizacion from './components/cotizacion';
import Categoria from './components/categoria';
import Marca from './components/marca';
import MarcaProducto from './components/MarcaProducto'; 
import HacerCotizacion from './components/HacerCotizacion';
import { BackupView } from './components/BackupView';

// VISTA DE ERROR DE EXCEPCIÓN
import Error403 from './Errores/Error403';

import './global.css';

// --- HELPER DE VALIDACIÓN ESTRUCTURAL Y DE EXPIRACIÓN DE TOKEN ---
const isTokenValid = (token) => {
    if (!token) return false;

    try {
        const parts = token.split('.');
        if (parts.length !== 3) return false;

        // Decodificar el payload Base64Url
        const payloadBase64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
        const decodedPayload = JSON.parse(window.atob(payloadBase64));

        // Validar si el token ya expiró
        if (decodedPayload.exp) {
            const currentTime = Math.floor(Date.now() / 1000);
            if (decodedPayload.exp < currentTime) {
                console.warn("⚠️ El token JWT ha expirado.");
                return false;
            }
        }

        return true;
    } catch (e) {
        console.error("❌ Token JWT malformado o inválido:", e);
        return false;
    }
};

// --- PROTECCIÓN POR ROL CON EXCEPCIÓN ---
const PrivateRoute = ({ children, requireAdmin = false }) => {
    const auth = localStorage.getItem('isAuthenticated');
    const rol = localStorage.getItem('rolUsuario');
    const token = localStorage.getItem('token');

    // Validación de sesión y de integridad del JWT
    if (auth !== 'true' || !isTokenValid(token)) {
        localStorage.clear();
        return <Navigate to="/login" replace />;
    }
    
    // Si requiere ADMIN y no lo es, redirige a la vista de error 403
    if (requireAdmin && rol !== 'ADMIN') {
        return <Navigate to="/403" replace />;
    }

    return children;
};

// --- LAYOUT ADMIN ---
const AdminLayout = ({ children }) => {
    const location = useLocation();
    const [sidebarOpen, setSidebarOpen] = useState(false);

    const handleLogout = () => { 
        localStorage.clear(); 
        window.location.href = '/login'; 
    };

    const activeClass = (path) => location.pathname === path ? 'active' : '';

    const handleNavClick = () => {
        setSidebarOpen(false);
    };

    return (
        <div className="app-container">
            <button 
                className="hamburger-btn" 
                onClick={() => setSidebarOpen(!sidebarOpen)}
                aria-label="Abrir menú"
            >
                <i className={`fa-solid ${sidebarOpen ? 'fa-xmark' : 'fa-bars'}`}></i>
            </button>

            <div 
                className={`sidebar-overlay ${sidebarOpen ? 'active' : ''}`} 
                onClick={() => setSidebarOpen(false)}
            ></div>

            <aside className={`sidebar ${sidebarOpen ? 'sidebar-open' : ''}`}>
                <div className="sidebar-header">🛠️ PANEL CONTROL</div>
                <nav className="sidebar-nav">
                    <Link to="/dashboard" className="nav-link" onClick={handleNavClick}>
                        <i className="fa-solid fa-house me-2"></i> Inicio Usuario
                    </Link>
                    <hr style={{opacity: 0.1, margin: '15px 0'}}/>
                    
                    <Link to="/usuarios" className={`nav-link ${activeClass('/usuarios')}`} onClick={handleNavClick}>
                        <i className="fa-solid fa-users me-2"></i> Usuarios
                    </Link>
                    <Link to="/motos" className={`nav-link ${activeClass('/motos')}`} onClick={handleNavClick}>
                        <i className="fa-solid fa-motorcycle me-2"></i> Motos
                    </Link>
                    <Link to="/referencias" className={`nav-link ${activeClass('/referencias')}`} onClick={handleNavClick}>
                        <i className="fa-solid fa-tags me-2"></i> Referencias
                    </Link>
                    <Link to="/productos" className={`nav-link ${activeClass('/productos')}`} onClick={handleNavClick}>
                        <i className="fa-solid fa-box me-2"></i> Productos
                    </Link>
                    <Link to="/servicios" className={`nav-link ${activeClass('/servicios')}`} onClick={handleNavClick}>
                        <i className="fa-solid fa-wrench me-2"></i> Servicios
                    </Link>
                    <Link to="/caracteristicas" className={`nav-link ${activeClass('/caracteristicas')}`} onClick={handleNavClick}> 
                        <i className="fa-solid fa-list me-2"></i> Características
                    </Link>
                    <Link to="/tipo-servicio" className={`nav-link ${activeClass('/tipo-servicio')}`} onClick={handleNavClick}>
                        <i className="fa-solid fa-gear me-2"></i> Tipos de Servicio
                    </Link>
                    <Link to="/mecanico" className={`nav-link ${activeClass('/mecanico')}`} onClick={handleNavClick}>
                        <i className="fa-solid fa-user-gear me-2"></i> Mecánicos
                    </Link>
                    <Link to="/pqrs" className={`nav-link ${activeClass('/pqrs')}`} onClick={handleNavClick}>
                        <i className="fa-solid fa-comments me-2"></i> PQRS
                    </Link>
                    <Link to="/cotizacion" className={`nav-link ${activeClass('/cotizacion')}`} onClick={handleNavClick}>
                        <i className="fa-solid fa-file-invoice-dollar me-2"></i> Cotización
                    </Link>
                    <Link to="/categoria" className={`nav-link ${activeClass('/categoria')}`} onClick={handleNavClick}>
                        <i className="fa-solid fa-folder me-2"></i> Categorías
                    </Link>
                    <Link to="/marca" className={`nav-link ${activeClass('/marca')}`} onClick={handleNavClick}>
                        <i className="fa-solid fa-copyright me-2"></i> Marcas
                    </Link>
                    <Link to="/marca-producto" className={`nav-link ${activeClass('/marca-producto')}`} onClick={handleNavClick}>
                        <i className="fa-solid fa-tag me-2"></i> Marcas de Producto
                    </Link>
                    <NavLink 
                        to="/backup" 
                        className={({ isActive }) => isActive ? "nav-link active" : "nav-link"}
                        onClick={handleNavClick}
                    >
                        <i className="fa-solid fa-database me-2"></i>
                        <span>Copia de Seguridad</span>
                    </NavLink>
                </nav>
                <div className="sidebar-footer">
                    <button onClick={handleLogout} className="btn-bs btn-danger w-100">
                        <i className="fa-solid fa-power-off me-2"></i> Salir
                    </button>
                </div>
            </aside>
            <main className="main-content">
                {children}
            </main>
        </div>
    );
};

// --- COMPONENTE PRINCIPAL ---
function App() {
    return (
        <Router>
            <Routes>
                {/* RUTAS PÚBLICAS Y SUS ALIAS */}
                <Route path="/" element={<Navigate to="/dashboard" replace />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                
                {/* Catalogos (Rutas exactas y aliases en minúsculas) */}
                <Route path="/catalogoKit" element={<CatalogoKit />} />
                <Route path="/catalogokit" element={<Navigate to="/catalogoKit" replace />} />
                <Route path="/kits" element={<Navigate to="/catalogoKit" replace />} />
                
                <Route path="/catalogoAceites" element={<CatalogoAceites />} />
                <Route path="/catalogoaceites" element={<Navigate to="/catalogoAceites" replace />} />
                
                <Route path="/catalogoLlantas" element={<CatalogoLlantas />} />
                <Route path="/catalogollantas" element={<Navigate to="/catalogoLlantas" replace />} />
                
                <Route path="/fichaTecnica/:id" element={<FichaTecnica />} />
                <Route path="/hacer-cotizacion" element={<HacerCotizacion />} />
                <Route path="/dashboard" element={<Dashboard />} /> 

                {/* VISTA DE EXCEPCIÓN 403 */}
                <Route path="/403" element={<Error403 />} />

                {/* RUTAS PRIVADAS */}
                <Route path="/historial" element={<PrivateRoute><Historial /></PrivateRoute>} />
                <Route path="/nueva-pqrs" element={<PrivateRoute><CrearPqrs /></PrivateRoute>} />
                <Route path="/perfil" element={<PrivateRoute><PerfilUsuario /></PrivateRoute>} />
                <Route path="/asistente" element={<PrivateRoute><AsistenteMotos /></PrivateRoute>} />
                
                {/* Ruta de Recomendaciones y alias en singular */}
                <Route path="/recomendaciones" element={<PrivateRoute><RecomendACIONES /></PrivateRoute>} />
                <Route path="/recomendacion" element={<Navigate to="/recomendaciones" replace />} />

                {/* RUTAS ADMINISTRATIVAS */}
                <Route path="/usuarios" element={
                    <PrivateRoute requireAdmin><AdminLayout><Usuarios /></AdminLayout></PrivateRoute>
                } />
                <Route path="/servicios" element={
                    <PrivateRoute requireAdmin><AdminLayout><ServicioAdmin /></AdminLayout></PrivateRoute>
                } />
                <Route path="/referencias" element={
                    <PrivateRoute requireAdmin><AdminLayout><Referencia /></AdminLayout></PrivateRoute>
                } />
                <Route path="/motos" element={
                    <PrivateRoute requireAdmin><AdminLayout><Motos /></AdminLayout></PrivateRoute>
                } />
                <Route path="/productos" element={
                    <PrivateRoute requireAdmin><AdminLayout><Productos /></AdminLayout></PrivateRoute>
                } />
                <Route path="/caracteristicas" element={
                    <PrivateRoute requireAdmin><AdminLayout><Caracteristicas /></AdminLayout></PrivateRoute>
                } />
                <Route path="/tipo-servicio" element={
                    <PrivateRoute requireAdmin><AdminLayout><TipoServicio /></AdminLayout></PrivateRoute>
                } />
                <Route path="/pqrs" element={
                    <PrivateRoute requireAdmin><AdminLayout><PqrsManager /></AdminLayout></PrivateRoute>
                } />
                <Route path="/mecanico" element={
                    <PrivateRoute requireAdmin><AdminLayout><Mecanico /></AdminLayout></PrivateRoute>
                } />
                <Route path="/cotizacion" element={
                    <PrivateRoute requireAdmin><AdminLayout><Cotizacion /></AdminLayout></PrivateRoute>
                } />
                <Route path="/categoria" element={
                    <PrivateRoute requireAdmin><AdminLayout><Categoria /></AdminLayout></PrivateRoute>
                } />
                <Route path="/marca" element={
                    <PrivateRoute requireAdmin><AdminLayout><Marca /></AdminLayout></PrivateRoute>
                } />
                <Route path="/marca-producto" element={
                    <PrivateRoute requireAdmin><AdminLayout><MarcaProducto /></AdminLayout></PrivateRoute>
                } />
                <Route path="/backup" element={
                    <PrivateRoute requireAdmin><AdminLayout><BackupView /></AdminLayout></PrivateRoute>
                } />

                {/* RUTAS NO ENCONTRADAS (FALLBACK DEFAULT A DASHBOARD) */}
                <Route path="*" element={<Navigate to="/dashboard" replace />} />
            </Routes>
        </Router>
    );
}

export default App;