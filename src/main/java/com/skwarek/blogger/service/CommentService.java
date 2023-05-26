package com.skwarek.blogger.service;

import com.skwarek.blogger.domain.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> findAll();

    Comment findById(Long commentId);

    Comment create(Comment commentRequest);

    Comment update(Long commentId, Comment commentRequest);

    void deleteById(Long commentId);

}
