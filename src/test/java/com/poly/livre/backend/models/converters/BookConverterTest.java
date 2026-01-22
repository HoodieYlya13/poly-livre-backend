package com.poly.livre.backend.models.converters;

import com.poly.livre.backend.models.dtos.BookDto;
import com.poly.livre.backend.models.dtos.UserDto;
import com.poly.livre.backend.models.entities.Book;
import com.poly.livre.backend.models.entities.BookReview;
import com.poly.livre.backend.models.entities.User;
import com.poly.livre.backend.models.enums.DeliveryType;
import com.poly.livre.backend.models.enums.LocaleLanguage;
import com.poly.livre.backend.repositories.BookReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookConverterTest {

    @Mock
    private UserConverter userConverter;

    @Mock
    private BookReviewRepository bookReviewRepository;

    @InjectMocks
    private BookConverter bookConverter;

    @Test
    void shouldConvertBookToBookDto() {
        UUID bookId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        Instant now = Instant.now();

        User owner = User.builder()
                .id(ownerId)
                .username("owner")
                .firstName("John")
                .lastName("Doe")
                .build();

        UserDto ownerDto = UserDto.builder()
                .id(ownerId)
                .username("owner")
                .firstName("John")
                .lastName("Doe")
                .build();

        Book book = Book.builder()
                .id(bookId)
                .title("The Great Gatsby")
                .description("A novel.")
                .author("F. Scott Fitzgerald")
                .owner(owner)
                .favorite(true)
                .styles(Collections.singletonList("Classic"))
                .rating(4.5)
                .price(10.0)
                .pages(200)
                .year(1925)
                .language(LocaleLanguage.EN)
                .delivery(DeliveryType.FREE)
                .loanDuration(14)
                .loaned(false)
                .build();
        book.setCreatedAt(now);
        book.setUpdatedAt(now);

        when(userConverter.convert(any(User.class))).thenReturn(ownerDto);

        User reviewer = User.builder().id(UUID.randomUUID()).username("reviewer").build();
        BookReview review = BookReview.builder()
                .id(reviewId)
                .book(book)
                .user(reviewer)
                .rating(5)
                .comment("Great!")
                .language(LocaleLanguage.EN)
                .build();
        review.setCreatedAt(now);

        when(bookReviewRepository.findAll()).thenReturn(List.of(review));

        BookDto bookDto = bookConverter.convert(book);

        assertThat(bookDto).isNotNull();
        assertThat(bookDto.getId()).isEqualTo(bookId);
        assertThat(bookDto.getTitle()).isEqualTo("The Great Gatsby");
        assertThat(bookDto.getOwner()).isEqualTo(ownerDto);
        assertThat(bookDto.getReviews()).hasSize(1);
        assertThat(bookDto.getReviews().get(0).getReviewId()).isEqualTo(reviewId);
        assertThat(bookDto.getReviews().get(0).getComment()).isEqualTo("Great!");
        assertThat(bookDto.getInformation().getPages()).isEqualTo(200);
        assertThat(bookDto.getInformation().getLanguage()).isEqualTo(LocaleLanguage.EN);
        assertThat(bookDto.getInformation().getDelivery()).isEqualTo(DeliveryType.FREE);
        assertThat(bookDto.getCreatedAt()).isEqualTo(now);
        assertThat(bookDto.getCover()).isEqualTo("/images/default");
    }
}
