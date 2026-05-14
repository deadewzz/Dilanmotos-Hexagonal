package com.dilanmotos.infrastructure.controller;


import com.dilanmotos.application.UseCases.MecanicoUC;
import com.dilanmotos.infrastructure.dto.MecanicoRequestDTO;
import com.dilanmotos.infrastructure.dto.MecanicoResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mecanicos")
@CrossOrigin(origins = "*")
public class MecanicoController {

    private final MecanicoUC uc;

    public MecanicoController(MecanicoUC uc) {
        this.uc = uc;
    }

    @GetMapping
    public ResponseEntity<List<MecanicoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(uc.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MecanicoResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<MecanicoResponseDTO> crear(@RequestBody MecanicoRequestDTO request) {
        return new ResponseEntity<>(uc.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MecanicoResponseDTO> actualizar(@PathVariable Integer id, @RequestBody MecanicoRequestDTO request) {
        return ResponseEntity.ok(uc.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    
}
