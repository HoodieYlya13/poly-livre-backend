package com.poly.livre.backend.repositories;

import com.poly.livre.backend.models.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    List<Book> findAllByOwnerId(UUID ownerId);

    List<Book> findAllByStylesContaining(String style);
}
