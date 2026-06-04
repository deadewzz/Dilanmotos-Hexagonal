package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.ChatResponse;
import com.dilanmotos.domain.model.Moto;
import com.dilanmotos.domain.repository.MarcaRepository;
import com.dilanmotos.domain.repository.MotoRepository;
import com.dilanmotos.infrastructure.persistence.ChatExternalPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatUseCase {

    private final ChatExternalPort chatExternalPort;
    private final MotoRepository motoRepository;
    private final MarcaRepository marcaRepository;

    public ChatUseCase(ChatExternalPort chatExternalPort,
                       MotoRepository motoRepository,
                       MarcaRepository marcaRepository) {
        this.chatExternalPort = chatExternalPort;
        this.motoRepository = motoRepository;
        this.marcaRepository = marcaRepository;
    }

    public ChatResponse execute(String message, Integer idUsuario) {
        // Obtener la primera moto registrada del usuario
        List<Moto> motos = motoRepository.obtenerPorUsuario(idUsuario);

        if (motos.isEmpty()) {
            return new ChatResponse(
                "Parcero, no tienes ninguna moto registrada. " +
                "Registra tu moto primero para darte recomendaciones personalizadas."
            );
        }

        Moto moto = motos.get(0); // Primera moto del usuario

        // Obtener nombre de la marca
        String nombreMarca = marcaRepository.buscarPorId(moto.getIdMarca())
            .map(m -> m.getNombre())
            .orElse("Marca desconocida");

        return chatExternalPort.getAiAnswer(
            message,
            nombreMarca,
            moto.getModelo(),
            moto.getCilindraje()
        );
    }
}