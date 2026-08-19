package com.dilanmotos.infrastructure.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ApkDownloadController {

    @GetMapping("/app-release.apk")
    public ResponseEntity<Resource> descargarApk() {
        Resource resource = new ClassPathResource("static/app-release.apk");

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        try {
            return ResponseEntity.ok()
                    // Tipo MIME oficial para archivos instalables .apk
                    .contentType(MediaType.parseMediaType("application/vnd.android.package-archive"))
                    // Obliga a guardar el archivo como descarga de adjunto
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"app-release.apk\"")
                    // Permite que DownloadManager en Android calcule la barra de progreso
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}