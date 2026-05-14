package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.TipoServicioUC;
import com.dilanmotos.infrastructure.dto.TipoServicioRequestDTO;
import com.dilanmotos.infrastructure.dto.TipoServicioResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipoServicio")
@CrossOrigin(origins = "*")
public class TipoServicioController {
    
    private final TipoServicioUC uc;

    public TipoServicioController(TipoServicioUC uc) {
        this.uc = uc;
    }

    @GetMapping
    public ResponseEntity<List<TipoServicioResponseDTO>> listarTodas() {
        return ResponseEntity.ok(uc.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoServicioResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TipoServicioResponseDTO> crear(@RequestBody TipoServicioRequestDTO request) {
        return new ResponseEntity<>(uc.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoServicioResponseDTO> actualizar(@PathVariable Integer id, @RequestBody TipoServicioRequestDTO request) {
        return ResponseEntity.ok(uc.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
