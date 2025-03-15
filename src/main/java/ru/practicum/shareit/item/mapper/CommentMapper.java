package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentCreatedDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static CommentDto mapToItemWithComments(Comment comment) {
        return new CommentDto(comment.getId(), comment.getAuthor().getName(), comment.getItem().getDescription(),
                comment.getItem().isAvailable(), comment.getText());
    }

    public static CommentCreatedDto mapToCommentCreatedDto(Comment comment) {
        return new CommentCreatedDto(comment.getId(), comment.getText(), comment.getItem(),
                comment.getAuthor().getName(), comment.getCreated());
    }
}
