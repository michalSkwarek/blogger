package com.skwarek.blogger.service.impl;

import com.skwarek.blogger.domain.Comment;
import com.skwarek.blogger.domain.Post;
import com.skwarek.blogger.domain.User;
import com.skwarek.blogger.dto.PostRequest;
import com.skwarek.blogger.exception.NotFoundPostException;
import com.skwarek.blogger.exception.NotFoundUserException;
import com.skwarek.blogger.repository.CommentRepository;
import com.skwarek.blogger.repository.PostRepository;
import com.skwarek.blogger.repository.UserRepository;
import com.skwarek.blogger.service.PostService;
import com.skwarek.blogger.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
//    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

//    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository) {
//        this.postRepository = postRepository;
//        this.userRepository = userRepository;
//        this.commentRepository = commentRepository;
//    }


    public PostServiceImpl(PostRepository postRepository, CommentRepository commentRepository, UserService userService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

    @Override
    public List<Post> findAllByUserId(Long userId) {
        User userDb = userService.findById(userId);
//        User userDb = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundUserException("Not found user with id: " + userId));

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
//        User userDb = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundUserException("Not found user with id: " + userId));

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
        comments.forEach(c -> postDb.removeComment(c));

        postRepository.deleteById(postDb.getId());
    }

}
