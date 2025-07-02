package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotation.ValidLogin;

import java.time.LocalDate;

@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
@Data
public class User {
    private Long id;
    @NotNull(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @ValidLogin
    private String login;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    @NotNull(message = "Дата рождения не может быть пустой")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}