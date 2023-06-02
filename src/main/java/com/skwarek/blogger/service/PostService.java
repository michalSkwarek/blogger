package com.skwarek.blogger.service;

import com.skwarek.blogger.domain.Post;
import com.skwarek.blogger.dto.PostRequest;

import java.util.List;

public interface PostService {

    List<Post> findAllByUserId(Long userId);

    Post findById(Long postId);

    Post create2User(Long userId, PostRequest postRequest);

    Post update(Long postId, PostRequest postRequest);

    void deleteById(Long postId);

}
