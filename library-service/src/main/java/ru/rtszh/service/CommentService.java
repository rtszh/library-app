package ru.rtszh.service;

import ru.rtszh.dto.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(CommentDto commentDto);

    CommentDto updateComment(CommentDto updatedCommentDto);

    List<CommentDto> getBookComments(String bookId);

    CommentDto getBookComment(String bookId, int orderNumber);

    void deleteComment(String bookId, int commentOrderNumberToDelete);
}
