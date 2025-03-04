package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    Integer id;
    String name;
    String description;
    @JsonProperty(value = "available", required = true)
    boolean available;
    User userOwner;
    ItemRequest request;
}
