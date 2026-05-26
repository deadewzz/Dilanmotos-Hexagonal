import React, { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom';
import './HacerCotizacion.css'

const STORAGE_KEYS = ['cart', 'selectedProducts', 'selected_items']

function readStoredItems() {
	for (const key of STORAGE_KEYS) {
		const raw = localStorage.getItem(key)
		if (raw) {
			try {
				const parsed = JSON.parse(raw)
				if (Array.isArray(parsed)) return parsed
			} catch (e) {
			}
		}
	}
	return []
}

export default function HacerCotizacion() {

	const navigate = useNavigate();
	const location = useLocation();
	const [items, setItems] = useState([])
	const [customer, setCustomer] = useState({ nombre: '', email: '' })
	const [quote, setQuote] = useState(null)
	const [products, setProducts] = useState([])
	const [selectedProductId, setSelectedProductId] = useState('')
	const [loadingProducts, setLoadingProducts] = useState(true)
	const [savingQuote, setSavingQuote] = useState(false)
	const [quoteMessage, setQuoteMessage] = useState('')

	useEffect(() => {
		const nombreGuardado = localStorage.getItem('nombreUsuario') || ''
		const correoGuardado = localStorage.getItem('correoUsuario') || localStorage.getItem('emailUsuario') || ''
		if (nombreGuardado || correoGuardado) {
			setCustomer({ nombre: nombreGuardado, email: correoGuardado })
		}

		try {
			const stored = readStoredItems()
			const normalized = stored.map((it) => ({
				id: it.id ?? it._id ?? Math.random().toString(36).slice(2, 9),
				nombre: it.nombre ?? it.name ?? it.titulo ?? 'Producto',
				precio: Number(it.precio ?? it.price ?? 0) || 0,
				cantidad: Number(it.cantidad ?? it.qty ?? it.quantity ?? 1) || 1,
			}))
			setItems(normalized)
		} catch (e) {
			setItems([])
		}
	}, [])

	useEffect(() => {
		const productoDesdeRuta = location.state?.producto
		if (!productoDesdeRuta) return

		setItems((prev) => {
			const currentId = String(productoDesdeRuta.id)
			const existing = prev.find((item) => String(item.id) === currentId)

			if (existing) {
				return prev.map((item) =>
					String(item.id) === currentId
						? { ...item, cantidad: Number(item.cantidad ?? 0) + Number(productoDesdeRuta.cantidad ?? 1) }
						: item,
				)
			}

			return [...prev, {
				id: currentId,
				nombre: productoDesdeRuta.nombre ?? 'Producto',
				precio: Number(productoDesdeRuta.precio ?? 0) || 0,
				cantidad: Number(productoDesdeRuta.cantidad ?? 1) || 1,
			}]
		})
	}, [location.state])

	useEffect(() => {
		const abortController = new AbortController()

		async function fetchProducts() {
			setLoadingProducts(true)
			try {
				const token = localStorage.getItem('token')
				const headers = token ? { Authorization: `Bearer ${token}` } : {}
				const response = await fetch('http://localhost:8080/api/productos', {
					signal: abortController.signal,
					headers,
				})
				if (!response.ok) throw new Error('No se pudieron cargar los productos')
				const data = await response.json()
				setProducts(Array.isArray(data) ? data : [])
			} catch (error) {
				// ignore load failure; dropdown stays empty
			} finally {
				setLoadingProducts(false)
			}
		}

		fetchProducts()

		return () => abortController.abort()
	}, [])

	useEffect(() => {
		try {
			localStorage.setItem('selectedProducts', JSON.stringify(items))
		} catch (e) {
			// ignore storage errors
		}
	}, [items])

	function addProduct(product) {
		if (!product) return
		const id = product.idProducto ?? product.id ?? Math.random().toString(36).slice(2, 9)
		const price = Number(product.precio ?? product.price ?? 0) || 0
		const name = product.nombre ?? product.titulo ?? 'Producto'

		setItems((prev) => {
			const found = prev.find((item) => String(item.id) === String(id))
			if (found) {
				return prev.map((item) =>
					String(item.id) === String(id)
						? { ...item, cantidad: item.cantidad + 1 }
						: item,
				)
			}
			return [...prev, { id, nombre: name, precio: price, cantidad: 1 }]
		})
	}

	function updateCantidad(id, delta) {
		setItems((prev) =>
			prev
				.map((it) => (String(it.id) === String(id) ? { ...it, cantidad: Math.max(1, it.cantidad + delta) } : it))
				.filter(Boolean),
		)
	}

	function removeItem(id) {
		setItems((prev) => prev.filter((it) => String(it.id) !== String(id)))
	}

	function clearAll() {
		setItems([])
	}

	const total = items.reduce((s, it) => s + it.precio * it.cantidad, 0)

	async function saveCotizacionToBackend(items) {
		const token = localStorage.getItem('token')
		const idUsuario = localStorage.getItem('idUsuario')
		if (!token || !idUsuario) {
			throw new Error('Es necesario iniciar sesión para guardar la cotización en la base de datos.')
		}

		const headers = {
			'Content-Type': 'application/json',
			Authorization: `Bearer ${token}`,
		}

		const fecha = new Date().toISOString()
		let quoteId = null
		const savedItems = []

		for (const item of items) {
			const payload = {
				idUsuario: Number(idUsuario),
				producto: item.nombre,
				cantidad: item.cantidad,
				precioUnitario: item.precio,
				fecha,
				producto_agregado: true,
			}

			const response = await fetch('http://localhost:8080/api/cotizaciones', {
				method: 'POST',
				headers,
				body: JSON.stringify(payload),
			})

			if (!response.ok) {
				const errorText = await response.text()
				throw new Error(errorText || 'Error al guardar la cotización')
			}

			const saved = await response.json()
			savedItems.push(saved)
			if (quoteId === null) {
				quoteId = saved.idCotizacion ?? saved.id
			}
		}

		return { quoteId, savedItems }
	}

	function formatDetalleCotizacion(items) {
		return items
			.map((item) => `${item.nombre} - ${item.cantidad} x $${item.precio.toFixed(2)} = $${(item.precio * item.cantidad).toFixed(2)}`)
			.join('\n')
	}

	async function enviarCotizacionPorCorreo(quote) {
		if (!quote?.cliente?.email) {
			throw new Error('No hay correo del cliente para enviar la cotización.')
		}

		const token = localStorage.getItem('token')
		const headers = {
			'Content-Type': 'application/json',
			...(token ? { Authorization: `Bearer ${token}` } : {}),
		}

		const payload = {
			destino: quote.cliente.email,
			nombreCliente: quote.cliente.nombre || 'Cliente',
			detalleCotizacion: formatDetalleCotizacion(quote.items),
			total: quote.total,
		}

		const response = await fetch('http://localhost:8080/cotizacion/generar', {
			method: 'POST',
			headers,
			body: JSON.stringify(payload),
		})

		if (!response.ok) {
			const errorText = await response.text()
			throw new Error(errorText || 'Error al enviar el correo de cotización.')
		}

		return response.text()
	}

	async function generarCotizacion() {
		if (items.length === 0) return
		setSavingQuote(true)
		setQuoteMessage('')
		try {
			const { quoteId } = await saveCotizacionToBackend(items)
			const q = {
				idCotizacion: quoteId,
				fecha: new Date().toISOString(),
				cliente: customer,
				items,
				total,
			}
			setQuote(q)

			let message = quoteId
				? `Cotización guardada en la base de datos con No. ${quoteId}`
				: 'Cotización generada localmente.'

			try {
				await enviarCotizacionPorCorreo(q)
				message += ' Correo enviado al cliente.'
			} catch (emailError) {
				console.error(emailError)
				message += ' No se pudo enviar el correo de cotización.'
			}

			setQuoteMessage(message)
			try {
				navigator.clipboard && navigator.clipboard.writeText(JSON.stringify(q, null, 2))
			} catch (e) {}
		} catch (error) {
			console.error(error)
			setQuoteMessage('No se pudo guardar la cotización en la base de datos. Revise la conexión o el inicio de sesión.')
			const q = {
				idCotizacion: Date.now(),
				fecha: new Date().toISOString(),
				cliente: customer,
				items,
				total,
			}
			setQuote(q)
		} finally {
			setSavingQuote(false)
		}
	}

	function descargarJSON() {
		if (!quote) return
		const blob = new Blob([JSON.stringify(quote, null, 2)], { type: 'application/json' })
		const url = URL.createObjectURL(blob)
		const a = document.createElement('a')
		a.href = url
		a.download = `cotizacion_${quote.idCotizacion ?? quote.id ?? 'sin-id'}.json`
		a.click()
		URL.revokeObjectURL(url)
	}

	const isSelected = (id) => items.some((item) => String(item.id) === String(id))

	const handleSelectProduct = (e) => {
		setSelectedProductId(e.target.value)
	}

	const addSelectedProduct = () => {
		if (!selectedProductId) return
		const product = products.find((product) => String(product.idProducto ?? product.id) === String(selectedProductId))
		if (product) {
			addProduct(product)
			setSelectedProductId('')
		}
	}

	return (
    <div className="dashboard-wrapper">
        <header className="dashboard-header">
            <div className="header-container">
                <button onClick={() => navigate('/dashboard')} className="btn-back-clean">
                    <i className="fa-solid fa-arrow-left"></i>
                </button>
                <h3 className="text-white m-0" style={{ color: '#ffffff' }}>Hacer Cotización</h3>
                <div style={{ width: '40px' }}></div>
            </div>
        </header>

        <main className="dashboard-content">
            <div className="card-panel shadow" style={{ maxWidth: '1000px', margin: '0 auto', background: 'white' }}>
                <h2 className="text-primary mb-4">Cotización</h2>

                {/* --- TABLA DE PRODUCTOS SELECCIONADOS --- */}
                {items.length === 0 ? (
                    <p className="text-muted">No hay productos seleccionados. Añade productos para generar una cotización.</p>
                ) : (
                    <div style={{ overflowX: 'auto', marginBottom: '1.5rem' }}>
                        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                            <thead>
                                <tr style={{ backgroundColor: '#343a40', color: 'white' }}>
                                    <th style={{ textAlign: 'left', padding: '10px 12px' }}>Producto</th>
                                    <th style={{ padding: '10px 12px' }}>Precio</th>
                                    <th style={{ padding: '10px 12px' }}>Cantidad</th>
                                    <th style={{ padding: '10px 12px' }}>Subtotal</th>
                                    <th style={{ padding: '10px 12px' }}>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {items.map((it) => (
                                    <tr key={it.id} style={{ borderBottom: '1px solid #eee' }}>
                                        <td style={{ padding: '10px 12px' }}>{it.nombre}</td>
                                        <td style={{ padding: '10px 12px', textAlign: 'center' }}>${it.precio.toFixed(2)}</td>
                                        <td style={{ padding: '10px 12px', textAlign: 'center' }}>
                                            <button className="btn-bs btn-primary btn-sm" onClick={() => updateCantidad(it.id, -1)}>-</button>
                                            <span style={{ margin: '0 10px', fontWeight: 'bold' }}>{it.cantidad}</span>
                                            <button className="btn-bs btn-primary btn-sm" onClick={() => updateCantidad(it.id, +1)}>+</button>
                                        </td>
                                        <td style={{ padding: '10px 12px', textAlign: 'center' }}>${(it.precio * it.cantidad).toFixed(2)}</td>
                                        <td style={{ padding: '10px 12px', textAlign: 'center' }}>
                                            <button className="btn-bs btn-danger btn-sm" onClick={() => removeItem(it.id)}>
                                                <i className="fa-solid fa-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {/* --- DATOS PERSONALES --- */}
                <div className="mb-3">
                    <label className="fw-bold d-block mb-2">Datos Personales</label>
                    <input className="input-bs" placeholder="Nombre" value={customer.nombre} readOnly />
                    <input className="input-bs" placeholder="Email" value={customer.email} readOnly />
                </div>

                {/* --- SELECTOR DE PRODUCTO --- */}
                <div className="mb-3">
                    <label className="fw-bold d-block mb-2">Agregar Producto</label>
                    <div style={{ display: 'flex', gap: '10px' }}>
                        <select
                            className="input-bs"
                            style={{ marginBottom: 0, flex: 1 }}
                            value={selectedProductId}
                            onChange={handleSelectProduct}
                            disabled={loadingProducts || products.length === 0}
                        >
                            <option value="">
                                {loadingProducts
                                    ? 'Cargando productos...'
                                    : products.length === 0
                                        ? 'No hay productos disponibles'
                                        : 'Seleccionar producto...'}
                            </option>
                            {products.map((product) => {
                                const id = product.idProducto ?? product.id
                                return (
                                    <option key={id} value={id}>
                                        {product.nombre ?? product.titulo ?? 'Producto'} - ${Number(product.precio ?? product.price ?? 0).toFixed(2)}
                                    </option>
                                )
                            })}
                        </select>
                        <button
                            className="btn-bs btn-primary"
                            onClick={addSelectedProduct}
                            disabled={!selectedProductId || isSelected(selectedProductId)}
                        >
                            {isSelected(selectedProductId) ? 'Agregado' : 'Agregar'}
                        </button>
                    </div>
                </div>

                {/* --- RESUMEN Y ACCIONES --- */}
                <div style={{ borderTop: '1px solid #eee', paddingTop: '1rem', marginBottom: '1rem' }}>
                    <p className="fw-bold" style={{ fontSize: '1.1rem' }}>
                        Total: <span className="text-primary">${total.toFixed(2)}</span>
                    </p>
                </div>

                <div style={{ display: 'flex', gap: '10px' }}>
                    <button
                        className="btn-bs btn-success w-100"
                        onClick={generarCotizacion}
                        disabled={savingQuote || items.length === 0}
                    >
                        <i className="fa-solid fa-file-invoice"></i>
                        {savingQuote ? 'Guardando...' : 'Generar Cotización'}
                    </button>
                    <button
                        className="btn-bs btn-danger"
                        onClick={clearAll}
                        disabled={items.length === 0}
                    >
                        <i className="fa-solid fa-trash"></i> Vaciar
                    </button>
                </div>

                {/* --- MENSAJE DE ESTADO --- */}
                {quoteMessage && (
                    <p style={{ marginTop: '1rem', color: '#198754', fontWeight: 'bold', textAlign: 'center' }}>
                        {quoteMessage}
                    </p>
                )}

                {/* --- INVOICE / COTIZACIÓN GENERADA --- */}
                {quote && (
                    <div className="invoice-card" style={{ marginTop: '1.5rem' }}>
                        <div className="invoice-header">
                            <div>
                                <span className="invoice-label">COTIZACIÓN</span>
                                <h3>Detalle de Repuesto</h3>
                            </div>
                            <div className="invoice-company">
                                <strong>Dilan Motos</strong>
                                <span>Servicios, repuestos y asesoría técnica</span>
                                <span>Soporte: 300-XXX-XXXX</span>
                            </div>
                        </div>

                        <div className="invoice-meta">
                            <div className="invoice-client">
                                <h4>Cliente</h4>
                                <p>{quote.cliente.nombre || 'Invitado'}</p>
                                <p>{quote.cliente.email || 'Sin correo'}</p>
                            </div>
                            <div className="invoice-details">
                                <p><strong>Fecha:</strong> {new Date(quote.fecha).toLocaleString()}</p>
                                <p><strong>No. Cotización:</strong> {quote.idCotizacion ?? quote.id}</p>
                                <p><strong>Total:</strong> ${quote.total.toFixed(2)}</p>
                            </div>
                        </div>

                        <div className="invoice-table-wrapper">
                            <table className="invoice-table">
                                <thead>
                                    <tr>
                                        <th>Producto</th>
                                        <th>Precio</th>
                                        <th>Cantidad</th>
                                        <th>Subtotal</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {quote.items.map((item) => (
                                        <tr key={item.id}>
                                            <td>{item.nombre}</td>
                                            <td>${item.precio.toFixed(2)}</td>
                                            <td>{item.cantidad}</td>
                                            <td>${(item.precio * item.cantidad).toFixed(2)}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>

                        <div className="invoice-summary">
                            <div><span>Total</span><strong>${quote.total.toFixed(2)}</strong></div>
                        </div>
							<h3 style={{ textAlign: 'center', color: '#732dcf' }}>¡Gracias por utilizar nuestros servicios!</h3>
                    </div>
                )}
            </div>
        </main>
    </div>
)
}

