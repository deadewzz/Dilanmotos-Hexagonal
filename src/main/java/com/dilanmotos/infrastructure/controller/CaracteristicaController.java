package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.CaracteristicaUC;
import com.dilanmotos.infrastructure.dto.CaracteristicaRequestDTO;
import com.dilanmotos.infrastructure.dto.CaracteristicaResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/caracteristicas")
public class CaracteristicaController {

    private final CaracteristicaUC uc;

    public CaracteristicaController(CaracteristicaUC uc) {
        this.uc = uc;
    }

    // GET para listar TODO
    @GetMapping
    public ResponseEntity<List<CaracteristicaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(uc.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaracteristicaResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @GetMapping("/moto/{idMoto}")
    public ResponseEntity<List<CaracteristicaResponseDTO>> listarPorMoto(@PathVariable Integer idMoto) {
        return ResponseEntity.ok(uc.listarPorMoto(idMoto));
    }

    @PostMapping
    public ResponseEntity<CaracteristicaResponseDTO> crear(@RequestBody CaracteristicaRequestDTO request) {
        return ResponseEntity.ok(uc.agregar(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}