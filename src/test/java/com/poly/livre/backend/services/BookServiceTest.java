package com.poly.livre.backend.services;

import com.poly.livre.backend.exceptions.NotFoundException;
import com.poly.livre.backend.exceptions.errors.BookErrorCode;
import com.poly.livre.backend.models.converters.BookConverter;
import com.poly.livre.backend.models.dtos.BookDto;
import com.poly.livre.backend.models.entities.Book;
import com.poly.livre.backend.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.poly.livre.backend.repositories.ImageRepository;
import com.poly.livre.backend.repositories.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookConverter bookConverter;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldReturnBookById_WhenBookExists() {
        UUID bookId = UUID.randomUUID();
        Book book = Book.builder().id(bookId).title("Test Book").build();
        BookDto bookDto = BookDto.builder().id(bookId).title("Test Book").build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookConverter.convert(book)).thenReturn(bookDto);

        BookDto result = bookService.getBookById(bookId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(bookId);
    }

    @Test
    void shouldThrowNotFoundException_WhenBookDoesNotExist() {
        UUID bookId = UUID.randomUUID();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(bookId))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", BookErrorCode.BOOK_NOT_FOUND);
    }

    @Test
    void shouldReturnTrendingBooks() {
        Book book = Book.builder().title("Trending Book").build();
        BookDto bookDto = BookDto.builder().title("Trending Book").build();
        Page<Book> page = new PageImpl<>(List.of(book));

        when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(bookConverter.convert(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.getTrendingBooks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Trending Book");

        verify(bookRepository).findAll(PageRequest.of(0, 4));
    }

    @Test
    void shouldReturnAllBooks() {
        Book book = Book.builder().title("Some Book").build();
        BookDto bookDto = BookDto.builder().title("Some Book").build();

        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookConverter.convert(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.getAllBooks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Some Book");
    }

    @Test
    void shouldReturnBooksByUserId() {
        UUID userId = UUID.randomUUID();
        Book book = Book.builder().title("User Book").build();
        BookDto bookDto = BookDto.builder().title("User Book").build();

        when(bookRepository.findAllByOwnerId(userId)).thenReturn(List.of(book));
        when(bookConverter.convert(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.getBooksByUserId(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("User Book");
    }
}
