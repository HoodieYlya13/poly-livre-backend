package com.poly.livre.backend.models.entities;

import com.poly.livre.backend.models.enums.DeliveryType;
import com.poly.livre.backend.models.enums.LocaleLanguage;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity(name = "BOOKS")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Book extends AuditDateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "TITLE", columnDefinition = "VARCHAR(255)", nullable = false)
    private String title;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Column(name = "AUTHOR", columnDefinition = "VARCHAR(255)")
    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COVER_IMAGE_ID")
    private Image coverImage;

    @Column(name = "IS_FAVORITE")
    private boolean favorite;

    @ElementCollection
    @CollectionTable(name = "BOOK_STYLES", joinColumns = @JoinColumn(name = "BOOK_ID"))
    @Column(name = "STYLE")
    private List<String> styles;

    @Column(name = "RATING")
    private Double rating;

    @Column(name = "PRICE")
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID")
    private User owner;

    @Column(name = "PAGES")
    private Integer pages;

    @Column(name = "YEAR")
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(name = "LANGUAGE")
    private LocaleLanguage language;

    @Enumerated(EnumType.STRING)
    @Column(name = "DELIVERY_TYPE")
    private DeliveryType delivery;

    @Column(name = "LOAN_DURATION")
    private Integer loanDuration;

    @Column(name = "IS_LOANED")
    private boolean loaned;
}
