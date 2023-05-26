package com.skwarek.blogger.repository;

import com.skwarek.blogger.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
