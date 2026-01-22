package com.poly.livre.backend.models.entities;

import com.poly.livre.backend.models.enums.LocaleLanguage;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "BOOK_REVIEWS")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BookReview extends AuditDateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_ID", nullable = false)
    private Book book;

    @Column(name = "RATING", nullable = false)
    private Integer rating;

    @Column(name = "COMMENT", columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "LANGUAGE")
    private LocaleLanguage language;
}
