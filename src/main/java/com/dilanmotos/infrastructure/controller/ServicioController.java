package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.ServicioUC;
import com.dilanmotos.infrastructure.dto.ServicioRequestDTO;
import com.dilanmotos.infrastructure.dto.ServicioResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "*")
public class ServicioController {

    private final ServicioUC uc;

    public ServicioController(ServicioUC uc) {
        this.uc = uc;
    }

    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> listarTodas() {
        return ResponseEntity.ok(uc.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ServicioResponseDTO> crear(@RequestBody ServicioRequestDTO request) {
        return new ResponseEntity<>(uc.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> actualizar(@PathVariable Integer id, @RequestBody ServicioRequestDTO request) {
        return ResponseEntity.ok(uc.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
