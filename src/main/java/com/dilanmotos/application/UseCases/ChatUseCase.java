package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.ChatResponse;
import com.dilanmotos.domain.model.Moto;
import com.dilanmotos.domain.repository.MarcaRepository;
import com.dilanmotos.domain.repository.MotoRepository;
import com.dilanmotos.infrastructure.persistence.DeepSeekAdapter; // ← Cambio aquí
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatUseCase {

    private final DeepSeekAdapter deepSeekAdapter; // ← Cambio aquí
    private final MotoRepository motoRepository;
    private final MarcaRepository marcaRepository;

    public ChatUseCase(DeepSeekAdapter deepSeekAdapter, // ← Cambio aquí
                       MotoRepository motoRepository,
                       MarcaRepository marcaRepository) {
        this.deepSeekAdapter = deepSeekAdapter;
        this.motoRepository = motoRepository;
        this.marcaRepository = marcaRepository;
    }

    public ChatResponse execute(String message, Integer idUsuario) {
        List<Moto> motos = motoRepository.obtenerPorUsuario(idUsuario);

        if (motos.isEmpty()) {
            return new ChatResponse(
                "Parcero, no tienes ninguna moto registrada. " +
                "Registra tu moto primero para darte recomendaciones personalizadas."
            );
        }

        Moto moto = motos.get(0);

        String nombreMarca = marcaRepository.buscarPorId(moto.getIdMarca())
            .map(m -> m.getNombre())
            .orElse("Marca desconocida");

        return deepSeekAdapter.getAiAnswer( // ← Cambio aquí
            message,
            nombreMarca,
            moto.getModelo(),
            moto.getCilindraje()
        );
    }

    public ChatResponse executeRecomendaciones(Integer idUsuario) {
        List<Moto> motos = motoRepository.obtenerPorUsuario(idUsuario);

        if (motos.isEmpty()) {
            return new ChatResponse("{\"recomendaciones\": []}");
        }

        Moto moto = motos.get(0);

        String nombreMarca = marcaRepository.buscarPorId(moto.getIdMarca())
            .map(m -> m.getNombre())
            .orElse("Marca desconocida");

        return deepSeekAdapter.getRecomendaciones( // ← Cambio aquí
            nombreMarca,
            moto.getModelo(),
            moto.getCilindraje()
        );
    }
}