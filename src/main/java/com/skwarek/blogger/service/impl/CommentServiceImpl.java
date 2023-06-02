package com.skwarek.blogger.service.impl;

import com.skwarek.blogger.domain.Comment;
import com.skwarek.blogger.domain.Post;
import com.skwarek.blogger.dto.CommentRequest;
import com.skwarek.blogger.exception.NotFoundCommentException;
import com.skwarek.blogger.repository.CommentRepository;
import com.skwarek.blogger.service.CommentService;
import com.skwarek.blogger.service.PostService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    public CommentServiceImpl(CommentRepository commentRepository, PostService postService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
    }

    @Override
    public List<Comment> findAllByPostId(Long postId) {
        Post postDb = postService.findById(postId);

        return commentRepository.findByPostId(postDb.getId());
    }

    @Override
    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundCommentException("Not found comment with id: " + commentId));
    }

    @Override
    public Comment create2Post(Long postId, CommentRequest commentRequest) {
        Post postDb = postService.findById(postId);

        Comment newComment = Comment.builder()
                .content(commentRequest.getContent())
                .build();

        postDb.addComment(newComment);

        return commentRepository.save(newComment);
    }

    @Override
    public Comment update(Long commentId, CommentRequest commentRequest) {
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
