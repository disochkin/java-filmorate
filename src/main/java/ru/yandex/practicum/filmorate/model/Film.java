package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.MinReleaseDate;

import java.time.LocalDate;

@Data
public class Film {
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

    public Film() {

    }
}