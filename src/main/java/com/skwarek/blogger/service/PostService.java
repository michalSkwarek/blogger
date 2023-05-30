package com.skwarek.blogger.service;

import com.skwarek.blogger.domain.Post;
import com.skwarek.blogger.dto.PostRequest;

import java.util.List;

public interface PostService {

    List<Post> findAll();

    Post findById(Long postId);

    Post create(PostRequest postRequest);

    Post update(Long postId, PostRequest postRequest);

    void deleteById(Long postId);

}
