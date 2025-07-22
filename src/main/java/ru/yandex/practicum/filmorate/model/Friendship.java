package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor
@Data
@Builder
public class Friendship {
    @NotNull
    private Long userId;
    @NotNull
    private Long friendId;
    @NotNull
    private FriendshipStatus friendshipStatus;
}
