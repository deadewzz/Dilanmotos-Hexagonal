package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.PqrsUC;
import com.dilanmotos.infrastructure.dto.PqrsRequestDTO;
import com.dilanmotos.infrastructure.dto.PqrsResponseDTO;
import com.dilanmotos.infrastructure.dto.PqrsUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pqrs")
@CrossOrigin(origins = "*")
public class PqrsController {

    private final PqrsUC pqrsUC;

    public PqrsController(PqrsUC pqrsUC) {
        this.pqrsUC = pqrsUC;
    }

    @GetMapping
    public ResponseEntity<List<PqrsResponseDTO>> listarTodas() {
        System.out.println("GET /api/pqrs - Listando todas");
        return ResponseEntity.ok(pqrsUC.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PqrsResponseDTO> obtenerPorId(@PathVariable Integer id) {
        System.out.println("GET /api/pqrs/" + id);
        return ResponseEntity.ok(pqrsUC.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<PqrsResponseDTO> crear(@Valid @RequestBody PqrsRequestDTO request) {
        System.out.println("POST /api/pqrs - Creando: " + request);
        return new ResponseEntity<>(pqrsUC.crear(request), HttpStatus.CREATED);
    }

    // Endpoint para actualización completa (admin o usuario)
    @PutMapping("/{id}")
    public ResponseEntity<PqrsResponseDTO> actualizar(@PathVariable Integer id, @RequestBody PqrsRequestDTO request) {
        System.out.println("PUT /api/pqrs/" + id);
        System.out.println("Body recibido: " + request);
        PqrsResponseDTO response = pqrsUC.actualizar(id, request);
        return ResponseEntity.ok(response);
    }
    
    // NUEVO: Endpoint específico para que el admin actualice solo estado y respuesta
    @PutMapping("/admin/{id}")
    public ResponseEntity<PqrsResponseDTO> actualizarAdmin(@PathVariable Integer id, @RequestBody PqrsUpdateDTO request) {
        System.out.println("PUT /api/pqrs/admin/" + id);
        System.out.println("Body recibido: " + request);
        PqrsResponseDTO response = pqrsUC.actualizarAdmin(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        System.out.println("DELETE /api/pqrs/" + id);
        pqrsUC.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}