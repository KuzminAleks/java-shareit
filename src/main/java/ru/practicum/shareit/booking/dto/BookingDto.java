package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    Integer id;
    @NotNull
    Integer itemId;
    @NotNull
    @JsonProperty("start")
    LocalDateTime startTime;
    @NotNull
    @JsonProperty("end")
    LocalDateTime endTime;
}
