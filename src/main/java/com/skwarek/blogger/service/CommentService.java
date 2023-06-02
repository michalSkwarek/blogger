package com.skwarek.blogger.service;

import com.skwarek.blogger.domain.Comment;
import com.skwarek.blogger.dto.CommentRequest;

import java.util.List;

public interface CommentService {

    List<Comment> findAllByPostId(Long postId);

    Comment findById(Long commentId);

    Comment create2Post(Long postId, CommentRequest commentRequest);

    Comment update(Long commentId, CommentRequest commentRequest);

    void deleteById(Long commentId);

}
