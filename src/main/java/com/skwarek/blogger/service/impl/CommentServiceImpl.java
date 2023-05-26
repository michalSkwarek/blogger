package com.skwarek.blogger.service.impl;

import com.skwarek.blogger.domain.Comment;
import com.skwarek.blogger.exception.NotFoundCommentException;
import com.skwarek.blogger.repository.CommentRepository;
import com.skwarek.blogger.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundCommentException("Not found comment with id: " + commentId));
    }

    @Override
    public Comment create(Comment commentRequest) {
        Comment newComment = Comment.builder()
                .content(commentRequest.getContent())
                .build();

        return commentRepository.save(newComment);
    }

    @Override
    public Comment update(Long commentId, Comment commentRequest) {
        Comment oldComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundCommentException("Not found comment with id: " + commentId));

        oldComment.setContent(commentRequest.getContent());

        return commentRepository.save(oldComment);
    }

    @Override
    public void deleteById(Long commentId) {
        Comment commentDb = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundCommentException("Not found comment with id: " + commentId));

        commentRepository.deleteById(commentDb.getId());
    }

}
