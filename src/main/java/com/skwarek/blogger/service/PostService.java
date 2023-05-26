package com.skwarek.blogger.service;

import com.skwarek.blogger.domain.Post;

import java.util.List;

public interface PostService {

    List<Post> findAll();

    Post findById(Long postId);

    Post create(Post postRequest);

    Post update(Long postId, Post postRequest);

    void deleteById(Long postId);

}
