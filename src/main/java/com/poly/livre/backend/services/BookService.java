package com.poly.livre.backend.services;

import com.poly.livre.backend.exceptions.NotFoundException;

import com.poly.livre.backend.models.converters.BookConverter;
import com.poly.livre.backend.models.dtos.BookDto;
import com.poly.livre.backend.repositories.BookRepository;
import com.poly.livre.backend.exceptions.ForbiddenException;
import com.poly.livre.backend.exceptions.errors.UserErrorCode;
import com.poly.livre.backend.models.dtos.BookRequestDto;
import com.poly.livre.backend.models.entities.Book;
import com.poly.livre.backend.repositories.ImageRepository;
import com.poly.livre.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService implements BaseService {

        private final BookRepository bookRepository;
        private final BookConverter bookConverter;
        private final UserRepository userRepository;
        private final ImageRepository imageRepository;

        @Transactional(readOnly = true)
        public BookDto getBookById(UUID id) {
                return bookRepository.findById(id)
                                .map(bookConverter::convert)
                                .orElseThrow(() -> new NotFoundException(
                                                com.poly.livre.backend.exceptions.errors.BookErrorCode.BOOK_NOT_FOUND,
                                                id));
        }

        @Transactional(readOnly = true)
        public List<BookDto> getTrendingBooks() {
                return bookRepository.findAll(org.springframework.data.domain.PageRequest.of(0, 4))
                                .stream()
                                .map(bookConverter::convert)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<BookDto> getAllBooks() {
                return bookRepository.findAll()
                                .stream()
                                .map(bookConverter::convert)
                                .toList();
        }

        @Transactional
        public BookDto addBook(BookRequestDto request) {
                var currentUser = getCurrentUser()
                                .orElseThrow(() -> new ForbiddenException(UserErrorCode.NOT_FOUND));

                if (!currentUser.getId().equals(request.ownerId())) {
                        throw new ForbiddenException(UserErrorCode.ACCESS_DENIED);
                }

                var owner = userRepository.findById(request.ownerId())
                                .orElseThrow(() -> new NotFoundException(UserErrorCode.NOT_FOUND,
                                                request.ownerId().toString()));

                // Mock image for the moment
                var coverImage = imageRepository.findAll().stream().findFirst().orElse(null);

                var book = Book.builder()
                                .title(request.title())
                                .author(request.author())
                                .description(request.description())
                                .price(request.price())
                                .loanDuration(request.loanDuration())
                                .styles(request.styles())
                                .pages(request.information().pages())
                                .year(request.information().year())
                                .language(request.information().language())
                                .delivery(request.information().delivery())
                                .owner(owner)
                                .coverImage(coverImage)
                                .build();

                return bookConverter.convert(bookRepository.save(book));
        }

        @Transactional(readOnly = true)
        public List<BookDto> getBooksByUserId(UUID userId) {
                return bookRepository.findAllByOwnerId(userId)
                                .stream()
                                .map(bookConverter::convert)
                                .toList();
        }
}