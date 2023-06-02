package com.skwarek.blogger.service.impl;

import com.skwarek.blogger.domain.Comment;
import com.skwarek.blogger.domain.Post;
import com.skwarek.blogger.domain.User;
import com.skwarek.blogger.dto.PostRequest;
import com.skwarek.blogger.exception.NotFoundPostException;
import com.skwarek.blogger.repository.PostRepository;
import com.skwarek.blogger.service.PostService;
import com.skwarek.blogger.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public PostServiceImpl(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Override
    public List<Post> findAllByUserId(Long userId) {
        User userDb = userService.findById(userId);

        return postRepository.findByUserId(userDb.getId());
    }

    @Override
    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundPostException("Not found post with id: " + postId));
    }

    @Override
    public Post create2User(Long userId, PostRequest postRequest) {
        User userDb = userService.findById(userId);

        Post newPost = Post.builder()
                .content(postRequest.getContent())
                .build();

        userDb.addPost(newPost);

        return postRepository.save(newPost);
    }

    @Override
    public Post update(Long postId, PostRequest postRequest) {
        Post oldPost = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundPostException("Not found post with id: " + postId));

        oldPost.setContent(postRequest.getContent());

        return postRepository.save(oldPost);
    }

    @Override
    public void deleteById(Long postId) {
        Post postDb = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundPostException("Not found post with id: " + postId));

        List<Comment> comments = new ArrayList<>(postDb.getComments());
        comments.forEach(postDb::removeComment);

        postRepository.deleteById(postDb.getId());
    }

}
