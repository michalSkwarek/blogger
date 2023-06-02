package com.skwarek.blogger.controller;

import com.skwarek.blogger.domain.Post;
import com.skwarek.blogger.dto.PostRequest;
import com.skwarek.blogger.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping(value = "/api")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping(value = "/users/{userId}/posts")
    public ResponseEntity<List<Post>> getAllPostsByUserId(@PathVariable("userId") Long userId) {
        List<Post> posts = postService.findAllByUserId(userId);

        if (!posts.isEmpty()) {
            return ResponseEntity.ok(posts);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping(value = "/posts/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable("postId") Long postId) {
        Post post = postService.findById(postId);

        return ResponseEntity.ok(post);
    }

    @PostMapping(value = "/users/{userId}/posts/create")
    public ResponseEntity<Post> createPost2User(@PathVariable("userId") Long userId,
                                                @RequestBody PostRequest postRequest) {
        Post createdPost = postService.create2User(userId, postRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/api/posts")
                .path("/{postId}")
                .buildAndExpand(createdPost.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdPost);
    }

    @PutMapping(value = "/posts/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable("postId") Long postId,
                                           @RequestBody PostRequest postRequest) {
        Post updatedPost = postService.update(postId, postRequest);

        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping(value = "/posts/{postId}")
    public ResponseEntity<HttpStatus> deletePostById(@PathVariable("postId") Long postId) {
        postService.deleteById(postId);

        return ResponseEntity.noContent().build();
    }

}
