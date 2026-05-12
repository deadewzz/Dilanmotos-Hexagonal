package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.Moto;
import com.dilanmotos.domain.repository.MotoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MotoRepositoryImpl implements MotoRepository {

    private final MotoJpaRepository jpa;

    public MotoRepositoryImpl(MotoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Moto guardar(Moto moto) {
        MotoEntity entity = toEntity(moto);
        return toModel(jpa.save(entity));
    }

    @Override
    public List<Moto> obtenerTodas() {
        return jpa.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Moto> buscarPorId(Integer id) {
        return jpa.findById(id).map(this::toModel);
    }

    @Override
    public Moto actualizar(Moto moto) {
        Integer id = moto.getIdMoto();
        return jpa.findById(id).map(entity -> {
            entity.setIdUsuario(moto.getIdUsuario());
            entity.setIdMarca(moto.getIdMarca());
            entity.setModelo(moto.getModelo());
            entity.setCilindraje((int) moto.getCilindraje());
            return toModel(jpa.save(entity));
        }).orElseThrow(() -> new RuntimeException("Error al actualizar: Moto no encontrada"));
    }

    @Override
    public void eliminar(Integer id) {
        jpa.deleteById(id);
    }

    // MAPPERS INTERNOS
    private Moto toModel(MotoEntity e) {
        Moto m = new Moto();
        m.setIdMoto(e.getIdMoto());
        m.setIdUsuario(e.getIdUsuario());
        m.setIdMarca(e.getIdMarca());
        m.setModelo(e.getModelo());
        m.setCilindraje(e.getCilindraje());
        return m;
    }

    private MotoEntity toEntity(Moto m) {
        MotoEntity e = new MotoEntity();
        if (m.getIdMoto() != null)
            e.setIdMoto(m.getIdMoto());
        e.setIdUsuario(m.getIdUsuario());
        e.setIdMarca(m.getIdMarca());
        e.setModelo(m.getModelo());
        e.setCilindraje((int) m.getCilindraje());
        return e;
    }
}