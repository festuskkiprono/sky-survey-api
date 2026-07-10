package com.skysurvey.sky_survey_api.response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
@Component
public class LocalDiskFileStorage implements FileStorage{
    private final Path root;

    /**
     * app.uploads.dir comes from application.properties (default: ./uploads).
     * The directory is created at boot -- a submission should never be the
     * first thing to discover it's missing.
     */
    public LocalDiskFileStorage(@Value("${app.uploads.dir:uploads}") String uploadsDir) throws IOException {
        this.root = Path.of(uploadsDir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    @Override
    public String store(MultipartFile file) {
        // Server-generated name = UUID + original extension.
        // This single line defeats both upload attacks from the design review:
        //   1. Collision: two users upload "certificate.pdf" -> distinct UUIDs, no overwrite.
        //   2. Path traversal: a client naming its file "../../etc/passwd" has zero
        //      influence on where bytes land, because we never use its name for location.
        // original_filename is stored in the DB for DISPLAY; storage_path is for LOCATION.
        String extension = "";
        String original = file.getOriginalFilename();
        if (original != null && original.lastIndexOf('.') > -1) {
            extension = original.substring(original.lastIndexOf('.')).toLowerCase();
        }
        String storageName = UUID.randomUUID() + extension;

        try {
            file.transferTo(root.resolve(storageName));
        } catch (IOException e) {
            // Unchecked wrapper so the @Transactional service sees a RuntimeException
            // and rolls the DB back -- a half-persisted submission must not survive.
            throw new UncheckedIOException("Failed to store uploaded file", e);
        }
        return storageName;
    }

    @Override
    public Path resolve(String storagePath) {
        return root.resolve(storagePath).normalize();
    }

    @Override
    public void deleteQuietly(String storagePath) {
        try {
            Files.deleteIfExists(root.resolve(storagePath));
        } catch (IOException ignored) {
            // best effort by contract
        }
    }
}
