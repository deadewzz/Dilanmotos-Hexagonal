package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.CategoriaUC;
import com.dilanmotos.infrastructure.dto.CategoriaRequestDTO;
import com.dilanmotos.infrastructure.dto.CategoriaResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    
    private final CategoriaUC uc;

    public CategoriaController(CategoriaUC uc) {
        this.uc = uc;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(uc.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crear(@RequestBody CategoriaRequestDTO request) {
        return new ResponseEntity<>(uc.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizar(@PathVariable Integer id, @RequestBody CategoriaRequestDTO request) {
        return ResponseEntity.ok(uc.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
}
