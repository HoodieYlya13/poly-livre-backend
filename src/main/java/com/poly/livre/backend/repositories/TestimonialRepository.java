package com.poly.livre.backend.repositories;

import com.poly.livre.backend.models.entities.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, UUID> {

    java.util.List<Testimonial> findByLanguage(com.poly.livre.backend.models.enums.LocaleLanguage language,
            org.springframework.data.domain.Pageable pageable);
}
