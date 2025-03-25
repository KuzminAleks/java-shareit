package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findByRequestor_Id(Integer userId);

    @Query("SELECT r.description, r.createTime, i.id, i.name, i.userOwner \n" +
            "FROM ItemRequest r \n" +
            "LEFT JOIN Item i ON i.request.id = r.id\n" +
            "WHERE r.requestor.id = :id")
    List<ItemRequest> findRequestsWithItems(@Param("requestorId") Integer requestorId);
}
