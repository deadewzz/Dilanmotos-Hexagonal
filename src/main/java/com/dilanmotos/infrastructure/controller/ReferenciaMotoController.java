package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.ReferenciaMotoUC;
import com.dilanmotos.infrastructure.dto.ReferenciaMotoRequestDTO;
import com.dilanmotos.infrastructure.dto.ReferenciaMotoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/referencias")
@CrossOrigin(origins = "*")
public class ReferenciaMotoController {
    
    public final ReferenciaMotoUC referenciaMotoUC;

    public ReferenciaMotoController(ReferenciaMotoUC referenciaMotoUC) {
        this.referenciaMotoUC = referenciaMotoUC;
    }

    @GetMapping
    public ResponseEntity<List<ReferenciaMotoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(referenciaMotoUC.listarTodas());
    }   

    @GetMapping("/{id}")
    public ResponseEntity<ReferenciaMotoResponseDTO> obtenerPorId(@PathVariable Integer id)
    {
        return ResponseEntity.ok(referenciaMotoUC.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ReferenciaMotoResponseDTO> crear(@Valid @RequestBody ReferenciaMotoRequestDTO request) {
    return new ResponseEntity<>(referenciaMotoUC.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReferenciaMotoResponseDTO> actualizar(
            @PathVariable Integer id, 
            @Valid @RequestBody ReferenciaMotoRequestDTO request) {
        return ResponseEntity.ok(referenciaMotoUC.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        referenciaMotoUC.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
