package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Entity
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column
    String name;
    @Column
    String description;
    @Column(name = "is_available")
    boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    User userOwner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    ItemRequest request;
}
