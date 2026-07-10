package com.skysurvey.sky_survey_api.response;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {
    private final UploadedFileRepository uploadedFileRepository;
    private final FileStorage fileStorage;

    public CertificateController(UploadedFileRepository uploadedFileRepository,
                                 FileStorage fileStorage) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.fileStorage = fileStorage;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileSystemResource> download(@PathVariable Integer id) {
        UploadedFileEntity file = uploadedFileRepository.findById(id)
                .orElseThrow(() -> new CertificateNotFoundException(id));

        Path path = fileStorage.resolve(file.getStoragePath());
        FileSystemResource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            // Metadata row exists but bytes are gone (manually deleted from uploads/):
            // same 404 as no-row -- the client can't tell and shouldn't.
            throw new CertificateNotFoundException(id);
        }

        return ResponseEntity.ok()
                .contentType(safeMediaType(file.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + sanitize(file.getOriginalFilename()) + "\"")
                .contentLength(file.getFileSize())
                .body(resource);
    }

    /** file_type came from the client's browser at upload; trust but verify. */
    private MediaType safeMediaType(String stored) {
        try {
            return MediaType.parseMediaType(stored);
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    /** Strip characters that would break the Content-Disposition header. */
    private String sanitize(String filename) {
        return filename == null ? "download"
                : filename.replace("\"", "").replace("\r", "").replace("\n", "");
    }
}
