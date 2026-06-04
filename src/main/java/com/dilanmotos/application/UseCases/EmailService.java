package com.dilanmotos.application.UseCases;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarCotizacion(
            String destino,
            String nombreCliente,
            String detalleCotizacion,
            Double total
    ) {

        SimpleMailMessage mensaje = new SimpleMailMessage();

        mensaje.setTo(destino);
        mensaje.setSubject("Cotización - Dilan Motos");

        mensaje.setText(
                "Hola " + nombreCliente + "\n\n" +
                "Gracias por cotizar con nosotros.\n\n" +
                "Detalle:\n" +
                detalleCotizacion +
                "\n\nTotal: $" + total +
                "\n\nDilan Motos"
        );

        mailSender.send(mensaje);
    }

    public void enviarCorreoRecuperacion(String destinatario, String token) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Recuperación de contraseña - Dilan Motos");
        mensaje.setText(
            "Hola parcero,\n\n" +
            "Recibimos una solicitud para restablecer tu contraseña.\n\n" +
            "Tu código de recuperación es:\n\n" +
            ">>> " + token + " <<<\n\n" +
            "Este código expira en 15 minutos.\n" +
            "Si no solicitaste esto, ignora este correo.\n\n" +
            "Dilan Motos"
        );
        mailSender.send(mensaje);
    }
}
