package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

import java.time.Duration;
import java.time.Instant;
@Data
public class Film {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @NonNull
    private Instant releaseDate;
    @NonNull
    private Duration duration;
}