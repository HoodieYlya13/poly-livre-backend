package com.poly.livre.backend.services;

import com.poly.livre.backend.exceptions.NotFoundException;

import com.poly.livre.backend.models.converters.BookConverter;
import com.poly.livre.backend.models.dtos.BookDto;
import com.poly.livre.backend.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookConverter bookConverter;

    @Transactional(readOnly = true)
    public BookDto getBookById(UUID id) {
        return bookRepository.findById(id)
                .map(bookConverter::convert)
                .orElseThrow(() -> new NotFoundException(
                        com.poly.livre.backend.exceptions.errors.BookErrorCode.BOOK_NOT_FOUND, id));
    }

    @Transactional(readOnly = true)
    public List<BookDto> getTrendingBooks() {
        return bookRepository.findAll(org.springframework.data.domain.PageRequest.of(0, 4))
                .stream()
                .map(bookConverter::convert)
                .toList();
    }

    @Transactional
    public BookDto addBook() {
        BookDto newBook = BookDto.builder()
                .title("New Book")
                .description("Description of the new book")
                .author("Author Name")
                .price(19.99)
                .build();

        return newBook;
    }
}