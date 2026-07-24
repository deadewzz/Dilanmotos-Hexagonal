import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link, useLocation } from 'react-router-dom';

// --- IMPORTACIÓN DE COMPONENTES ---
import Login from './components/login';
import Register from './components/register'; 
import Dashboard from './components/dashboard';
import PerfilUsuario from './components/perfilUsuario';
import AsistenteMotos from './components/IA';
import Recomendaciones from './components/RecomendacionesPanel';
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
import MarcaProducto from './components/MarcaProducto'; // 👈 NUEVO COMPONENTE IMPORTADO
import HacerCotizacion from './components/HacerCotizacion';

import './global.css';

// --- PROTECCIÓN POR ROL ---

const PrivateRoute = ({ children, requireAdmin = false }) => {
    const auth = localStorage.getItem('isAuthenticated');
    const rol = localStorage.getItem('rolUsuario');

    if (auth !== 'true') return <Navigate to="/login" />;
    if (requireAdmin && rol !== 'ADMIN') return <Navigate to="/dashboard" />;

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
                    
                    {/* 👈 ENLACE AL NUEVO MÓDULO DE MARCAS DE PRODUCTO */}
                    <Link to="/marca-producto" className={`nav-link ${activeClass('/marca-producto')}`} onClick={handleNavClick}>
                        <i className="fa-solid fa-tag me-2"></i> Marcas de Producto
                    </Link>
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
                {/* RUTAS PÚBLICAS */}
                <Route path="/" element={<Navigate to="/dashboard" />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/catalogoKit" element={<CatalogoKit />} />
                <Route path="/catalogoAceites" element={<CatalogoAceites />} />
                <Route path="/catalogoLlantas" element={<CatalogoLlantas />} />
                <Route path="/fichaTecnica/:id" element={<FichaTecnica />} />
                <Route path="/hacer-cotizacion" element={<HacerCotizacion />} />
                <Route path="/dashboard" element={<Dashboard />} /> 

                {/* RUTAS PRIVADAS */}
                <Route path="/historial" element={<PrivateRoute><Historial /></PrivateRoute>} />
                <Route path="/nueva-pqrs" element={<PrivateRoute><CrearPqrs /></PrivateRoute>} />
                <Route path="/perfil" element={<PrivateRoute><PerfilUsuario /></PrivateRoute>} />
                <Route path="/asistente" element={<PrivateRoute><AsistenteMotos /></PrivateRoute>} />
                <Route path="/recomendaciones" element={<PrivateRoute><Recomendaciones /></PrivateRoute>} />

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

                {/* 👈 NUEVA RUTA ADMIN PARA MARCAS DE PRODUCTO */}
                <Route path="/marca-producto" element={
                    <PrivateRoute requireAdmin><AdminLayout><MarcaProducto /></AdminLayout></PrivateRoute>
                } />

                {/* REDIRECCIÓN POR DEFECTO */}
                <Route path="*" element={<Navigate to="/dashboard" />} />
            </Routes>
        </Router>
    );
}

export default App;