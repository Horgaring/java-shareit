package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.item.id = ?1 ORDER BY c.created DESC")
    List<Comment> findAllByItemId(Long itemId);

    @Query("SELECT c FROM Comment c WHERE c.item.id = ?1 AND c.author.id = ?2")
    List<Comment> findByItemIdAndAuthorId(Long itemId, Long authorId);
}
