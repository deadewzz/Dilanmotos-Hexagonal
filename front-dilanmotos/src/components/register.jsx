import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../auth.css';

const Register = () => {
    const navigate = useNavigate();

    const [marcas, setMarcas] = useState([]);
    const [referencias, setReferencias] = useState([]);
    const [tiposServicio, setTiposServicio] = useState([]);

    const [formData, setFormData] = useState({
        nombre: '',
        correo: '',
        contrasena: '',
        idReferencia: '',
        idTipoServicio: ''
    });

    const [errorMensaje, setErrorMensaje] = useState(''); 

    // 1. Cargar marcas al montar
    useEffect(() => {
        fetch("http://localhost:8080/api/marcas")
            .then(res => {
                if (!res.ok) throw new Error("Error al obtener marcas");
                return res.json();
            })
            .then(data => setMarcas(data))
            .catch(err => console.error("Error cargando marcas:", err));

        // Cargar tipos de servicio también
        fetch("http://localhost:8080/api/tipoServicio")
            .then(res => {
                if (!res.ok) throw new Error("Error al obtener tipos de servicio");
                return res.json();
            })
            .then(data => setTiposServicio(Array.isArray(data) ? data : []))
            .catch(err => console.error("Error cargando tipos de servicio:", err));
    }, []);

    // 2. Cargar referencias al cambiar marca
    const handleMarcaChange = (e) => {
        const idMarca = e.target.value;
        setReferencias([]);
        setFormData(prev => ({ ...prev, idReferencia: '' }));

        if (idMarca) {
            fetch(`http://localhost:8080/api/referencias?marcaId=${idMarca}`)
                .then(res => {
                    if (!res.ok) throw new Error("Error al obtener modelos");
                    return res.json();
                })
                .then(data => {
                    const validos = data.filter(ref => ref.nombre && ref.nombre !== '');
                    setReferencias(validos);
                })
                .catch(err => console.error("Error al cargar referencias:", err));
        }
    };

    // 3. Enviar formulario


    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMensaje('');

        const payload = {
            ...formData,
            idReferencia: formData.idReferencia ? parseInt(formData.idReferencia) : null,
            idTipoServicio: formData.idTipoServicio && formData.idTipoServicio !== '0' ? parseInt(formData.idTipoServicio) : null
        };

        try {
            const res = await fetch("http://localhost:8080/api/usuarios", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                alert("¡Bienvenido a Dilan Motos! Registro exitoso.");
                navigate("/login");
            } else if (res.status === 409) {
                const data = await res.json();
                setErrorMensaje(data.mensaje || "Correo ya existente, inicia sesión");
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
                            name="nombre"
                            placeholder="Ej: Juan Perez"
                            value={formData.nombre}
                            onChange={e => setFormData({ ...formData, nombre: e.target.value })}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Correo Electrónico</label>
                        <input
                            className="auth-input"
                            type="email"
                            name="correo"
                            placeholder="correo@ejemplo.com"
                            value={formData.correo}
                            onChange={e => setFormData({ ...formData, correo: e.target.value })}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Contraseña</label>
                        <input
                            className="auth-input"
                            type="password"
                            name="contrasena"
                            placeholder="Asigne una contraseña segura (mín. 6, máx. 20 caracteres)"
                            value={formData.contrasena}
                            onChange={e => {
                                const valor = e.target.value.slice(0, 20);
                                setFormData({ ...formData, contrasena: valor });
                            }}
                            minLength={6}
                            maxLength={20}
                            title="La contraseña debe tener entre 6 y 20 caracteres."
                            required
                        />
                    </div>

                    <hr />
                    <h3>Información de tu Moto</h3>

                    <div className="form-group">
                        <label>Marca de tu moto</label>
                        <select
                         className="auth-input" 
                         name="marca" 
                         onChange={handleMarcaChange}
                          required>
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
                            name="modelo"
                            onChange={e => setFormData({ ...formData, idReferencia: e.target.value })}
                            disabled={referencias.length === 0}
                            required
                        >
                            <option value="">
                                {referencias.length === 0 ? "Primero elige una marca" : "-- Elige el modelo --"}
                            </option>
                            {referencias.map(ref => (
                                <option key={ref.idReferencia} value={ref.idReferencia}>
                                    {ref.nombre}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label>Tipo de Servicio</label>
                        <select
                            className="auth-input"
                            name="tipoServicio"
                            value={formData.idTipoServicio}
                            onChange={e => setFormData({ ...formData, idTipoServicio: e.target.value })}
                            required
                        >
                            <option value="">-- Selecciona un tipo de servicio --</option>
                            <option value="0">N/A</option>
                            {tiposServicio.map(t => (
                                <option key={t.idTipoServicio} value={t.idTipoServicio}>
                                    {t.nombre}
                                </option>
                            ))}
                        </select>
                    </div>

{errorMensaje && (
    <p style={{ color: '#c0392b', textAlign: 'center', fontSize: '0.9rem', marginBottom: '10px' }}>
        {errorMensaje.includes('inicia sesión') ? (
            <>
                Correo ya existente,{' '}
                <span
                    onClick={() => navigate('/login')}
                    style={{ color: '#3b46d8', cursor: 'pointer', fontWeight: 'bold', textDecoration: 'underline' }}
                >
                    inicia sesión
                </span>
            </>
        ) : (
            errorMensaje
        )}
    </p>
)}

                    <button type="submit" className="auth-btn-primary" name="btn-registro">
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