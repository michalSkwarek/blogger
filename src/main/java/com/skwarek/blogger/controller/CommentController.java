package com.skwarek.blogger.controller;

import com.skwarek.blogger.domain.Comment;
import com.skwarek.blogger.dto.CommentRequest;
import com.skwarek.blogger.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping(value = "/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping(value = "/comments")
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentService.findAll();

        if (!comments.isEmpty()) {
            return ResponseEntity.ok(comments);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping(value = "/comments/{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable("commentId") Long commentId) {
        Comment comment = commentService.findById(commentId);

        return ResponseEntity.ok(comment);
    }

    @PostMapping(value = "/comments/create")
    public ResponseEntity<Comment> createComment(@RequestBody CommentRequest commentRequest) {
        Comment createdComment = commentService.create(commentRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/api/comments")
                .path("/{commentId}").buildAndExpand(createdComment.getId()).toUri();

        return ResponseEntity.created(location).body(createdComment);
    }

    @PutMapping(value = "/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable("commentId") Long commentId,
                                                 @RequestBody CommentRequest commentRequest) {
        Comment updatedComment = commentService.update(commentId, commentRequest);

        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping(value = "/comments/{commentId}")
    public ResponseEntity<HttpStatus> deleteCommentById(@PathVariable("commentId") Long commentId) {
        commentService.deleteById(commentId);

        return ResponseEntity.noContent().build();
    }

}
