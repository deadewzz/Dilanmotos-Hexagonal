import React, { useEffect, useState } from 'react'
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
	const [items, setItems] = useState([])
	const [customer, setCustomer] = useState({ nombre: '', email: '' })
	const [quote, setQuote] = useState(null)

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

	function updateCantidad(id, delta) {
		setItems((prev) =>
			prev
				.map((it) => (it.id === id ? { ...it, cantidad: Math.max(1, it.cantidad + delta) } : it))
				.filter(Boolean),
		)
	}

	function removeItem(id) {
		setItems((prev) => prev.filter((it) => it.id !== id))
	}

	function clearAll() {
		setItems([])
	}

	const total = items.reduce((s, it) => s + it.precio * it.cantidad, 0)

	function generarCotizacion() {
		const q = {
			id: Date.now(),
			fecha: new Date().toISOString(),
			cliente: customer,
			items,
			total,
		}
		setQuote(q)
		try {
			navigator.clipboard && navigator.clipboard.writeText(JSON.stringify(q, null, 2))
		} catch (e) {}
	}

	function descargarJSON() {
		if (!quote) return
		const blob = new Blob([JSON.stringify(quote, null, 2)], { type: 'application/json' })
		const url = URL.createObjectURL(blob)
		const a = document.createElement('a')
		a.href = url
		a.download = `cotizacion_${quote.id}.json`
		a.click()
		URL.revokeObjectURL(url)
	}

	return (
		<div className="hacer-cotizacion" style={{ padding: 16 }}>
			<h2>Cotización</h2>

			{items.length === 0 ? (
				<p>No hay productos seleccionados. Añade productos para generar una cotización.</p>
			) : (
				<div style={{ overflowX: 'auto' }}>
					<table style={{ width: '100%', borderCollapse: 'collapse' }}>
						<thead>
							<tr>
								<th style={{ textAlign: 'left', padding: 8 }}>Producto</th>
								<th style={{ padding: 8 }}>Precio</th>
								<th style={{ padding: 8 }}>Cantidad</th>
								<th style={{ padding: 8 }}>Subtotal</th>
								<th style={{ padding: 8 }}>Acciones</th>
							</tr>
						</thead>
						<tbody>
							{items.map((it) => (
								<tr key={it.id}>
									<td style={{ padding: 8 }}>{it.nombre}</td>
									<td style={{ padding: 8 }}>{it.precio.toFixed(2)}</td>
									<td style={{ padding: 8 }}>
										<button onClick={() => updateCantidad(it.id, -1)} style={{ marginRight: 6 }}>-</button>
										{it.cantidad}
										<button onClick={() => updateCantidad(it.id, +1)} style={{ marginLeft: 6 }}>+</button>
									</td>
									<td style={{ padding: 8 }}>{(it.precio * it.cantidad).toFixed(2)}</td>
									<td style={{ padding: 8 }}>
										<button onClick={() => removeItem(it.id)}>Quitar</button>
									</td>
								</tr>
							))}
						</tbody>
					</table>
				</div>
			)}

			<div style={{ marginTop: 16, display: 'flex', gap: 16, flexWrap: 'wrap' }}>
				<div style={{ minWidth: 260 }}>
					<h4>Datos Personales</h4>
					<div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
						<input placeholder="Nombre" value={customer.nombre} readOnly />
						<input placeholder="Email" value={customer.email} readOnly />
					</div>
				</div>

				<div style={{ minWidth: 220 }}>
					<h4>Resumen</h4>
					<p>Total: <strong>{total.toFixed(2)}</strong></p>
					<div style={{ display: 'flex', gap: 8 }}>
						<button onClick={generarCotizacion} disabled={items.length === 0}>Generar Cotización</button>
						<button onClick={clearAll} disabled={items.length === 0}>Vaciar</button>
					</div>
				</div>
			</div>

			{quote && (
				<div className="invoice-card">
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
							<p><strong>No. Cotización:</strong> {quote.id}</p>
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
						<div>
							<span>Subtotal</span>
							<strong>${quote.total.toFixed(2)}</strong>
						</div>
						<div>
							<span>Total</span>
							<strong>${quote.total.toFixed(2)}</strong>
						</div>
					</div>

					<div className="invoice-actions">
						<button className="btn-green" onClick={descargarJSON}>Descargar JSON</button>
						<button className="btn-green" onClick={() => navigator.clipboard && navigator.clipboard.writeText(JSON.stringify(quote, null, 2))}>Copiar al portapapeles</button>
					</div>
				</div>
			)}
		</div>
	)
}

