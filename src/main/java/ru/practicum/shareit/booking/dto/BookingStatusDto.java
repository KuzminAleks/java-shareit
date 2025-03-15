package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingStatusDto {
    Integer id;
    @NotNull
    @JsonProperty("start")
    LocalDateTime startTime;
    @NotNull
    @JsonProperty("end")
    LocalDateTime endTime;
    @NotNull
    BookingStatus status;
    @NotNull
    @JsonProperty("booker")
    User bookerId;
    @NotNull
    @JsonProperty("item")
    Item itemId;
}
