package com.poly.livre.backend.models.converters;

import com.poly.livre.backend.models.dtos.BookDto;
import com.poly.livre.backend.models.dtos.BookReviewDto;
import com.poly.livre.backend.models.entities.Book;
import com.poly.livre.backend.models.entities.BookReview;
import com.poly.livre.backend.repositories.BookReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookConverter implements Converter<Book, BookDto> {

    private final UserConverter userConverter;
    private final BookReviewRepository bookReviewRepository;

    @Override
    public BookDto convert(@NonNull Book book) {
        List<BookReview> reviews = bookReviewRepository.findAll().stream()
                .filter(review -> review.getBook().getId().equals(book.getId()))
                .collect(Collectors.toList());

        List<BookReviewDto> reviewDtos = reviews.stream()
                .map(this::convertReview)
                .collect(Collectors.toList());

        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .author(book.getAuthor())
                .cover("/images/" + (book.getCoverImage() != null ? book.getCoverImage().getId() : "default"))
                .favorite(book.isFavorite())
                .styles(book.getStyles())
                .rating(book.getRating())
                .reviews(reviewDtos)
                .price(book.getPrice())
                .owner(userConverter.convert(book.getOwner()))
                .information(BookDto.InformationDto.builder()
                        .pages(book.getPages())
                        .year(book.getYear())
                        .language(book.getLanguage())
                        .delivery(book.getDelivery())
                        .build())
                .loanDuration(book.getLoanDuration())
                .loaned(book.isLoaned())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    private BookReviewDto convertReview(BookReview review) {
        return BookReviewDto.builder()
                .reviewId(review.getId())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .firstName(review.getUser().getFirstName())
                .lastName(review.getUser().getLastName())
                .status(review.getUser().getStatus())
                .bookId(review.getBook().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .language(review.getLanguage())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public Book convert(BookDto bookDto) {
        if (bookDto == null) {
            return null;
        }
        Book.BookBuilder builder = Book.builder();
                builder
                .id(bookDto.getId())
                .title(bookDto.getTitle())
                .description(bookDto.getDescription())
                .author(bookDto.getAuthor())
                .styles(bookDto.getStyles())
                .rating(bookDto.getRating())
                .price(bookDto.getPrice())
                .pages(bookDto.getInformation() != null ? bookDto.getInformation().getPages() : null)
                .year(bookDto.getInformation() != null ? bookDto.getInformation().getYear() : null)
                .language(bookDto.getInformation() != null ? bookDto.getInformation().getLanguage() : null)
                .delivery(bookDto.getInformation() != null ? bookDto.getInformation().getDelivery() : null)
                .loanDuration(bookDto.getLoanDuration())
                .loaned(bookDto.isLoaned())
                .favorite(bookDto.isFavorite());
        return builder.build();
    }   
}
