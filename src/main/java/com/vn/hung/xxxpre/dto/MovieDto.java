package com.vn.hung.xxxpre.dto;

public class MovieDto {
    private String id;
    private String name;

    private String thumbnailLink;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public MovieDto(String id, String name, String thumbnailLink) {
        this.id = id;
        this.name = name;
        this.thumbnailLink = thumbnailLink;
    }

    // Note: The Google API returns a parent object. We need records to map the nesting.
    record GoogleDriveFileResponse(java.util.List<MovieDto> files) {
    }
}
