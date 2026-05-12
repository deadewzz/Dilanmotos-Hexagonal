package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.PqrsUC;
import com.dilanmotos.infrastructure.dto.PqrsRequestDTO;
import com.dilanmotos.infrastructure.dto.PqrsResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pqrs")
@CrossOrigin(origins = "*")
public class PqrsController {

    private final PqrsUC PqrsUC;

    public PqrsController(PqrsUC PqrsUC) {
        this.PqrsUC = PqrsUC;
    }

    @GetMapping
    public ResponseEntity<List<PqrsResponseDTO>> listarTodas() {
        return ResponseEntity.ok(PqrsUC.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PqrsResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(PqrsUC.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<PqrsResponseDTO> crear(@Valid @RequestBody PqrsRequestDTO request) {
        return new ResponseEntity<>(PqrsUC.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PqrsResponseDTO> actualizar(
            @PathVariable Integer id, 
            @Valid @RequestBody PqrsRequestDTO request) {
        return ResponseEntity.ok(PqrsUC.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        PqrsUC.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}