package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.MarcaUC;
import com.dilanmotos.infrastructure.dto.MarcaRequestDTO;
import com.dilanmotos.infrastructure.dto.MarcaResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
public class MarcaController {
    
    private final MarcaUC uc;

    public MarcaController(MarcaUC uc) {
        this.uc = uc;
    }

    @GetMapping
    public ResponseEntity<List<MarcaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(uc.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarcaResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<MarcaResponseDTO> crear(@RequestBody MarcaRequestDTO request) {
        return new ResponseEntity<>(uc.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarcaResponseDTO> actualizar(@PathVariable Integer id, @RequestBody MarcaRequestDTO request) {
        return ResponseEntity.ok(uc.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
