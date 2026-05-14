package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.MotoUC;
import com.dilanmotos.infrastructure.dto.MotoRequestDTO;
import com.dilanmotos.infrastructure.dto.MotoResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/motos")
@CrossOrigin(origins = "*")
public class MotoController {

    private final MotoUC uc;

    public MotoController(MotoUC uc) {
        this.uc = uc;
    }

    @GetMapping
    public ResponseEntity<List<MotoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(uc.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotoResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<MotoResponseDTO> crear(@RequestBody MotoRequestDTO request) {
        return new ResponseEntity<>(uc.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MotoResponseDTO> actualizar(@PathVariable Integer id, @RequestBody MotoRequestDTO request) {
        return ResponseEntity.ok(uc.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
