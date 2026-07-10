package com.skysurvey.sky_survey_api.response;

import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "uploaded_files")
public class UploadedFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id", nullable = false)
    private AnswerEntity answer;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "storage_path", nullable = false, length = 255)
    private String storagePath;

    @Column(name = "file_size", nullable = false)
    private Integer fileSize;


    @Column(name = "file_type", nullable = false, length = 100)
    private String fileType;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    protected UploadedFileEntity() {
    }

    public UploadedFileEntity(AnswerEntity answer, String originalFilename,
                              String storagePath, Integer fileSize, String fileType) {
        this.answer = answer;
        this.originalFilename = originalFilename;
        this.storagePath = storagePath;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }

    @PrePersist
    void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public AnswerEntity getAnswer() { return answer; }
    public String getOriginalFilename() { return originalFilename; }
    public String getStoragePath() { return storagePath; }
    public Integer getFileSize() { return fileSize; }
    public String getFileType() { return fileType; }
}
