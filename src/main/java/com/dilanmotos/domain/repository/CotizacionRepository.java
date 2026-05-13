package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.Cotizacion;
import java.util.List;
import java.util.Optional;

public interface CotizacionRepository {
    Cotizacion guardar(Cotizacion cotizacion);
        
    List<Cotizacion> obtenerTodas(); 

    Optional<Cotizacion> buscarPorId(Integer id);

    Cotizacion actualizar(Cotizacion cotizacion); 

    void eliminar(Integer id); 
    
}
