package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotation.MinReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor
@Data
@Builder
public class Film {
    @Positive(message = "ID фильма не может быть пустым")
    private Long id;
    @NotBlank(message = "Название фильма не должно быть пустым")
    private String name;
    @Size(max = 200, message = "Описание фильма не должно превышать 200 символов")
    private String description;
    @NotNull
    @PastOrPresent
    @MinReleaseDate
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private int duration;
    @Builder.Default
    private Set<Long> likes = new HashSet<>();
}