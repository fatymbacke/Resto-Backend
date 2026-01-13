package com.app.manage_restaurant.utilities;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class FileRouterConfig {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Bean
    public RouterFunction<ServerResponse> fileRouter() {
        return RouterFunctions.route(GET("/files/{*filepath}"), request -> {
            // Récupère tout le chemin après /files/
            String filepath = request.pathVariable("filepath");

            Path filePathObj = Paths.get(uploadDir, filepath);

            if (!Files.exists(filePathObj) || Files.isDirectory(filePathObj)) {
                return ServerResponse.notFound().build();
            }

            FileSystemResource resource = new FileSystemResource(filePathObj.toFile());

            // Détection automatique du type MIME
            String mimeType;
            try {
                mimeType = Files.probeContentType(filePathObj);
            } catch (IOException e) {
                mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .bodyValue(resource);
        });
    }
}

