package com.example.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CourtRequest {

    @NotBlank(message = "Court name is required")
    private String name;

    private String location;
    private String description;
}