package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.Caracteristica;
import com.dilanmotos.domain.repository.CaracteristicaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CaracteristicaRepositoryImpl implements CaracteristicaRepository {

    private final CaracteristicaJpaRepository jpa;

    public CaracteristicaRepositoryImpl(CaracteristicaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Caracteristica guardar(Caracteristica c) {
        CaracteristicaEntity entity = new CaracteristicaEntity();
        entity.setIdMoto(c.getIdMoto());
        entity.setDescripcion(c.getDescripcion());
        CaracteristicaEntity saved = jpa.save(entity);
        c.setIdCaracteristica(saved.getIdCaracteristica());
        return c;
    }

    @Override
    public List<Caracteristica> obtenerPorMoto(Integer idMoto) {
        return jpa.findByIdMoto(idMoto).stream().map(e -> {
            Caracteristica c = new Caracteristica();
            c.setIdCaracteristica(e.getIdCaracteristica());
            c.setIdMoto(e.getIdMoto());
            c.setDescripcion(e.getDescripcion());
            return c;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Caracteristica> obtenerTodas() {
        return jpa.findAll().stream().map(e -> {
            Caracteristica c = new Caracteristica();
            c.setIdCaracteristica(e.getIdCaracteristica());
            c.setIdMoto(e.getIdMoto());
            c.setDescripcion(e.getDescripcion());
            return c;
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<Caracteristica> buscarPorId(Integer id) {
        return jpa.findById(id).map(e -> {
            Caracteristica c = new Caracteristica();
            c.setIdCaracteristica(e.getIdCaracteristica());
            c.setIdMoto(e.getIdMoto());
            c.setDescripcion(e.getDescripcion());
            return c;
        });
    }

    @Override
    public void eliminar(Integer id) {
        jpa.deleteById(id);
    }
}