package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "courts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String location;
    private String description;
    private String imageUrl;

    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}