package com.poly.livre.backend.repositories;

import com.poly.livre.backend.models.entities.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, UUID> {
}
