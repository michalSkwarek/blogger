package com.skwarek.blogger.repository;

import com.skwarek.blogger.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
