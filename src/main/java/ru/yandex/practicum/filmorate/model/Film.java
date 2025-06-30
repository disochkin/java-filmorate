package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private LocalDate releaseDate;
    @NonNull
    private int duration;
}