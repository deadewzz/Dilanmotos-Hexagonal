package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.HistorialUC;
import com.dilanmotos.infrastructure.dto.HistorialRequestDTO;
import com.dilanmotos.infrastructure.dto.HistorialResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historiales")
@CrossOrigin(origins = "*")
public class HistorialController {

    private final HistorialUC uc;

    public HistorialController(HistorialUC uc) {
        this.uc = uc;
    }

    @GetMapping
    public ResponseEntity<List<HistorialResponseDTO>> listarTodas() {
        return ResponseEntity.ok(uc.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<HistorialResponseDTO> crear(@RequestBody HistorialRequestDTO request) {
        return new ResponseEntity<>(uc.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistorialResponseDTO> actualizar(@PathVariable Integer id, @RequestBody HistorialRequestDTO request) {
        return ResponseEntity.ok(uc.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
