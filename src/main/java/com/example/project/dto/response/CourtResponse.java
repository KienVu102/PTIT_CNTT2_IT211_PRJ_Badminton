package com.example.project.dto.response;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourtResponse {
    private Long id;
    private String name;
    private String location;
    private String description;
    private String imageUrl;
    private boolean active;
}