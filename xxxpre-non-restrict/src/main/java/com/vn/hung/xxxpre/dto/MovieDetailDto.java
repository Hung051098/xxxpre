package com.vn.hung.xxxpre.dto;


import com.google.api.services.drive.model.File;
/**
 * A record to represent the video's technical metadata.
 * Using 'Long' (object) for durationMillis to allow null values if not present.
 */
record VideoMetadata(Long durationMillis, Integer width, Integer height) {
    public static VideoMetadata fromGoogleFile(File.VideoMediaMetadata metadata) {
        if (metadata == null) {
            return new VideoMetadata(null, null, null);
        }
        return new VideoMetadata(
                metadata.getDurationMillis(),
                metadata.getWidth(),
                metadata.getHeight()
        );
    }
}

/**
 * DTO for the detailed movie response.
 * This holds more information than the simple MovieDto used in lists.
 */
public record MovieDetailDto(
        String id,
        String name,
        String description,
        String thumbnailLink,
        String webViewLink, // Direct link to view in Google Drive
        String createdTime,
        VideoMetadata videoMetadata
) {
    /**
     * Factory method to create this DTO from a Google Drive File object.
     * @param file The File object from the Google Drive API.
     * @return A new MovieDetailDto instance.
     */
    public static MovieDetailDto fromGoogleFile(File file) {
        return new MovieDetailDto(
                file.getId(),
                file.getName(),
                file.getDescription(),
                file.getThumbnailLink(),
                file.getWebViewLink(),
                file.getCreatedTime() != null ? file.getCreatedTime().toStringRfc3339() : null,
                VideoMetadata.fromGoogleFile(file.getVideoMediaMetadata())
        );
    }
}
