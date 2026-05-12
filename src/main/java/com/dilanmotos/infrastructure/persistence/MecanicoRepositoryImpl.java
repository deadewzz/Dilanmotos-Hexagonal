package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.Mecanico;
import com.dilanmotos.domain.repository.MecanicoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MecanicoRepositoryImpl implements MecanicoRepository {

    private final MecanicoJpaRepository jpa;

    public MecanicoRepositoryImpl(MecanicoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Mecanico guardar(Mecanico mecanico) {
        MecanicoEntity entity = toEntity(mecanico);
        return toModel(jpa.save(entity));
    }

    @Override
    public List<Mecanico> obtenerTodos() {
        return jpa.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Mecanico> buscarPorId(Integer id) {
        return jpa.findById(id).map(this::toModel);
    }

    @Override
    public List<Mecanico> obtenerPorNombre(String nombre) {
        return jpa.findAll().stream()
                .map(this::toModel)
                .filter(m -> m.getNombre() != null && m.getNombre().equalsIgnoreCase(nombre))
                .collect(Collectors.toList());
    }

    @Override
    public Mecanico actualizar(Mecanico mecanico) {
        Integer id = mecanico.getIdMecanico();
        return jpa.findById(id).map(entity -> {
            entity.setNombre(mecanico.getNombre());
            entity.setEspecialidad(mecanico.getEspecialidad());
            return toModel(jpa.save(entity));
        }).orElseThrow(() -> new RuntimeException("Error al actualizar: Mecanico no encontrado"));
    }

    @Override
    public void eliminar(Integer id) {
        jpa.deleteById(id);
    }

    // MAPPERS INTERNOS
    private Mecanico toModel(MecanicoEntity e) {
        Mecanico m = new Mecanico();
        m.setIdMecanico(e.getIdMecanico());
        m.setNombre(e.getNombre());
        m.setEspecialidad(e.getEspecialidad());
        return m;
    }

    private MecanicoEntity toEntity(Mecanico m) {
        MecanicoEntity e = new MecanicoEntity();
        if (m.getIdMecanico() != null) {
            e.setIdMecanico(m.getIdMecanico());
        }
        e.setNombre(m.getNombre());
        e.setEspecialidad(m.getEspecialidad());
        return e;
    }
}
