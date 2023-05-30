package com.skwarek.blogger.service.impl;

import com.skwarek.blogger.domain.Comment;
import com.skwarek.blogger.domain.Post;
import com.skwarek.blogger.dto.PostRequest;
import com.skwarek.blogger.exception.NotFoundPostException;
import com.skwarek.blogger.repository.CommentRepository;
import com.skwarek.blogger.repository.PostRepository;
import com.skwarek.blogger.service.PostService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostServiceImpl(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundPostException("Not found post with id: " + postId));
    }

    @Override
    public Post create(PostRequest postRequest) {
        Post newPost = Post.builder()
                .content(postRequest.getContent())
                .build();

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

        List<Comment> commentsDb = commentRepository.findByPostId(postId);
        commentsDb.forEach(c -> commentRepository.deleteById(c.getId()));

        postRepository.deleteById(postDb.getId());
    }

}
