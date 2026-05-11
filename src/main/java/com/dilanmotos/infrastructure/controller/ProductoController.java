package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.ProductoUC;
import com.dilanmotos.infrastructure.dto.ProductoRequestDTO;
import com.dilanmotos.infrastructure.dto.ProductoResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoUC productoUC;

    public ProductoController(ProductoUC productoUC) {
        this.productoUC = productoUC;
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listar() {
        return ResponseEntity.ok(productoUC.listarTodos());
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(@RequestBody ProductoRequestDTO request) {
        return new ResponseEntity<>(productoUC.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable Integer id,
            @RequestBody ProductoRequestDTO request) {
        return ResponseEntity.ok(productoUC.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        productoUC.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}