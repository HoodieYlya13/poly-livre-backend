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
                var favoriteBookIds = getFavoriteBookIds();
                return bookRepository.findById(id)
                                .map(book -> bookConverter.convert(book, favoriteBookIds))
                                .orElseThrow(() -> new NotFoundException(
                                                com.poly.livre.backend.exceptions.errors.BookErrorCode.BOOK_NOT_FOUND,
                                                id));
        }

        @Transactional(readOnly = true)
        public List<BookDto> getTrendingBooks() {
                var favoriteBookIds = getFavoriteBookIds();
                return bookRepository.findAll(org.springframework.data.domain.PageRequest.of(0, 4))
                                .stream()
                                .map(book -> bookConverter.convert(book, favoriteBookIds))
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<BookDto> getAllBooks() {
                var favoriteBookIds = getFavoriteBookIds();
                return bookRepository.findAll()
                                .stream()
                                .map(book -> bookConverter.convert(book, favoriteBookIds))
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
                var favoriteBookIds = getFavoriteBookIds();
                return bookRepository.findAllByOwnerId(userId)
                                .stream()
                                .map(book -> bookConverter.convert(book, favoriteBookIds))
                                .toList();
        }

        @Transactional
        public void deleteBook(UUID bookId) {
                var currentUser = getCurrentUser()
                                .orElseThrow(() -> new ForbiddenException(UserErrorCode.NOT_FOUND));

                var book = bookRepository.findById(bookId)
                                .orElseThrow(() -> new NotFoundException(
                                                com.poly.livre.backend.exceptions.errors.BookErrorCode.BOOK_NOT_FOUND,
                                                bookId));

                if (!book.getOwner().getId().equals(currentUser.getId())) {
                        throw new ForbiddenException(UserErrorCode.ACCESS_DENIED);
                }

                bookRepository.delete(book);
        }

        @Transactional
        public BookDto toggleFavorite(UUID bookId) {
                var currentUser = getCurrentUser()
                                .orElseThrow(() -> new ForbiddenException(UserErrorCode.NOT_FOUND));

                var user = userRepository.findById(currentUser.getId())
                                .orElseThrow(() -> new NotFoundException(UserErrorCode.NOT_FOUND,
                                                currentUser.getId().toString()));

                var book = bookRepository.findById(bookId)
                                .orElseThrow(() -> new NotFoundException(
                                                com.poly.livre.backend.exceptions.errors.BookErrorCode.BOOK_NOT_FOUND,
                                                bookId));

                if (user.getFavoriteBooks().contains(book)) {
                        user.getFavoriteBooks().remove(book);
                } else {
                        user.getFavoriteBooks().add(book);
                }

                userRepository.save(user);

                return bookConverter.convert(book, java.util.Collections.singleton(book.getId()));
        }

        private java.util.Set<UUID> getFavoriteBookIds() {
                return getCurrentUser()
                                .flatMap(principal -> userRepository.findById(principal.getId()))
                                .map(user -> user.getFavoriteBooks().stream()
                                                .map(Book::getId)
                                                .collect(java.util.stream.Collectors.toSet()))
                                .orElse(java.util.Collections.emptySet());
        }
}