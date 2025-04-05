package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findByRequestorIdOrderByCreateTimeDesc(Integer userId);

    List<ItemRequest> findOrderByCreateTimeDesc();

    @Query("SELECT r.description, r.createTime, i \n" +
            "FROM ItemRequest r \n" +
            "LEFT JOIN Item i ON i.request.id = r.id\n" +
            "WHERE r.requestor.id = :id")
    List<ItemRequest> findRequestsWithItems(@Param("id") Integer requestorId);

    @Query("SELECT r FROM ItemRequest r " +
            "LEFT JOIN Item i ON i.request.id = r.id " +
            "WHERE r.requestor.id = :id")
    List<ItemRequest> findItems(@Param("id") Integer id);


}
