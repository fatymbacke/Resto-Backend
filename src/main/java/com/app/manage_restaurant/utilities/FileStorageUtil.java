package com.app.manage_restaurant.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
@Component
public class FileStorageUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageUtil.class);

    @Value("${file.upload-dir}")
    private String storageRoot;

    private Path getRootPath() {
        return Paths.get(storageRoot);
    }

    private void ensureDirectory(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            logger.info("Dossier créé : {}", path.toAbsolutePath());
        }
    }

    /**
     * Stocke un fichier directement dans le dossier final (logos/ ou covers/)
     */
    public Mono<String> storeFile(FilePart filePart, String folderName) {
        if (filePart == null) return Mono.empty();

        return Mono.fromCallable(() -> {
            Path finalDir = getRootPath().resolve(folderName);
            ensureDirectory(finalDir);

            String originalFilename = filePart.filename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            Path destination = finalDir.resolve(uniqueFilename);
            return destination;
        }).flatMap(path ->
                filePart.transferTo(path.toFile())
                        .then(Mono.just(folderName + "/" + path.getFileName().toString()))
        );
    }

    /**
     * Supprime un fichier
     */
    public Mono<Boolean> deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) return Mono.just(false);

        String cleanPath = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;

        return Mono.fromCallable(() -> {
            Path path = Paths.get(storageRoot, cleanPath);
            if (Files.exists(path)) {
                boolean deleted = Files.deleteIfExists(path);
                logger.info("Suppression du fichier {} : {}", path, deleted);
                return deleted;
            }
            return false;
        });
    }

    /**
     * Génère l’URL publique
     */
    public Mono<String> getFileUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) return Mono.just("");
        String cleanPath = relativePath.startsWith("/") ? relativePath : "/" + relativePath;
        return Mono.just("/files" + cleanPath);
    }
}
