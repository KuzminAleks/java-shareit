package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.status.BookingStatus;

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
    @JsonProperty("status_id")
    BookingStatus status;
    @NotNull
    @JsonProperty("booker_id")
    Integer bookerId;
    @NotNull
    @JsonProperty("item_id")
    Integer itemId;
    @NotBlank
    String itemName;
}
