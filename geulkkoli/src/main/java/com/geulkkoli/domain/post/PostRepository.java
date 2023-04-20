package com.geulkkoli.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository {
    Post save(Post post);

    void update(Long postId, Post updateParam);

    void delete(Long postId);

    Optional<Post> findById(Long postId);

    List<Post> findAll();

}
