package ru.practicum.shareit.item.dal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    Integer id;
    String name;
    String description;
    @JsonProperty(value = "available", required = true)
    Boolean available;
    ItemRequest request;
}
