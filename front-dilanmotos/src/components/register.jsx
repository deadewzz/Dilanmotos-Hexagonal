import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../auth.css';

const Register = () => {
    const navigate = useNavigate();
    
    // Estados para los selectores
    const [marcas, setMarcas] = useState([]);
    const [referencias, setReferencias] = useState([]);
    
    // Estado del formulario 
    const [formData, setFormData] = useState({
        nombre: '', 
        correo: '', 
        contrasena: '', 
        idReferencia: ''
    });

    // 1. Cargar Marcas al montar el componente
    useEffect(() => {
        fetch("http://localhost:8080/api/marcas")
            .then(res => {
                if (!res.ok) throw new Error("Error al obtener marcas (401 o 500)");
                return res.json();
            })
            .then(data => setMarcas(data))
            .catch(err => console.error("Error cargando marcas:", err));
    }, []);

    // 2. Cargar Modelos cuando cambia la marca
    const handleMarcaChange = (e) => {
        const idMarca = e.target.value;
        
        // Limpiar modelos y selección previa
        setReferencias([]);
        setFormData(prev => ({ ...prev, idReferencia: '' }));

        if (idMarca) {
            fetch(`http://localhost:8080/api/referencias?marcaId=${idMarca}`)
                .then(res => {
                    if (!res.ok) throw new Error("Error al obtener modelos");
                    return res.json();
                })
                .then(data => {
                    // Filtrar por si acaso hay datos vacíos en el catálogo
                    const validos = data.filter(ref => ref.nombre && ref.nombre !== '');
                    setReferencias(validos);
                })
                .catch(err => console.error("Error al cargar referencias:", err));
        }
    };

    // 3. Manejo del envío del formulario
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        try {
            const res = await fetch("http://localhost:8080/api/usuarios", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData)
            });

            if (res.ok) {
                alert("¡Bienvenido a Dilan Motos! Registro exitoso.");
                navigate("/login");
            } else {
                const errorData = await res.text();
                alert("Error al registrarse: " + errorData);
            }
        } catch (error) {
            console.error("Error de red:", error);
            alert("No se pudo conectar con el servidor.");
        }
    };

    return (
        <div className="auth-body">
            <div className="auth-card">
                <h2>Crea tu cuenta</h2>
                <p>Únete al mejor taller para tu moto</p>
                
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Nombre Completo</label>
                        <input 
                            className="auth-input" 
                            type="text" 
                            placeholder="Ej: Juan Perez" 
                            value={formData.nombre}
                            onChange={e => setFormData({...formData, nombre: e.target.value})} 
                            required 
                        />
                    </div>

                    <div className="form-group">
                        <label>Correo Electrónico</label>
                        <input 
                            className="auth-input" 
                            type="email" 
                            placeholder="correo@ejemplo.com" 
                            value={formData.correo}
                            onChange={e => setFormData({...formData, correo: e.target.value})} 
                            required 
                        />
                    </div>

                    <div className="form-group">
                        <label>Contraseña</label>
                        <input 
                            className="auth-input" 
                            type="password" 
                            placeholder="Mínimo 6 caracteres" 
                            value={formData.contrasena}
                            onChange={e => setFormData({...formData, contrasena: e.target.value})} 
                            required 
                        />
                    </div>

                    <hr />
                    <h3>Información de tu Moto</h3>

                    <div className="form-group">
                        <label>Marca de tu moto</label>
                        <select className="auth-input" onChange={handleMarcaChange} required>
                            <option value="">-- Selecciona una marca --</option>
                            {marcas.map(m => (
                                <option key={m.idMarca} value={m.idMarca}>
                                    {m.nombre}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label>Modelo (de nuestro catálogo)</label>
                        <select 
                            className="auth-input" 
                            value={formData.idReferencia}
                            onChange={e => setFormData({...formData, idReferencia: e.target.value})}
                            disabled={referencias.length === 0}
                            required
                        >
                            <option value="">
                                {referencias.length === 0 ? "Primero elige una marca" : "-- Elige el modelo --"}
                            </option>
                            {referencias.map(ref => (
                                <option key={ref.idReferencia} value={ref.idReferencia}>
                                    {ref.nombre} ({ref.cilindraje} cc)
                                </option>
                            ))}
                        </select>
                    </div>

                    <button type="submit" className="auth-btn-primary">
                        Completar Registro
                    </button>

                    <p className="auth-footer">
                        ¿Ya tienes cuenta? <span onClick={() => navigate('/login')}>Inicia sesión</span>
                    </p>
                </form>
            </div>
        </div>
    );
};

export default Register;