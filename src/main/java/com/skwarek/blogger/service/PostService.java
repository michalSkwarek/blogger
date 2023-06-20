package com.skwarek.blogger.service;

import com.skwarek.blogger.domain.Post;
import com.skwarek.blogger.dto.PostRequest;

import java.util.List;

public interface PostService {

    List<Post> findAllByAccountId(Long accountId);

    Post findById(Long postId);

    Post create2Account(Long accountId, PostRequest postRequest);

    Post update(Long postId, PostRequest postRequest);

    void deleteById(Long postId);

}
