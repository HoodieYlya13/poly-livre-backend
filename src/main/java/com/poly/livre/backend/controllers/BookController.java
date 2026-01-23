package com.poly.livre.backend.controllers;

import com.poly.livre.backend.models.dtos.BookDto;
import com.poly.livre.backend.models.dtos.BookRequestDto;
import com.poly.livre.backend.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Books", description = "Operations related to book management")
public class BookController {

    private final BookService bookService;

    @io.swagger.v3.oas.annotations.Operation(summary = "Get book by ID", description = "Retrieves detailed information about a specific book.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book found")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found")
    @GetMapping("/{id:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
    public ResponseEntity<BookDto> getBookById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get trending books", description = "Retrieves a list of trending books.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of trending books")
    @GetMapping("/trending")
    public ResponseEntity<List<BookDto>> getTrendingBooks() {
        return ResponseEntity.ok(bookService.getTrendingBooks());
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get all books", description = "Retrieves a list of all available books.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of all books")
    @GetMapping("/all")
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Add a new book", description = "Adds a new book to the platform.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book added successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden if user doesn't own the action")
    @PostMapping("/add")
    public ResponseEntity<BookDto> addBook(@RequestBody @Valid BookRequestDto request) {
        return ResponseEntity.ok(bookService.addBook(request));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get books by user ID", description = "Retrieves all books owned by a specific user.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of user's books")
    @GetMapping("/user/{id}")
    public ResponseEntity<List<BookDto>> getBooksByUserId(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getBooksByUserId(id));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Delete a book", description = "Deletes a book by its ID.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Book deleted successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden if user is not the owner")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found")
    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Toggle favorite status", description = "Adds or removes a book from the user's favorites.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Favorite status toggled")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book or User not found")
    @PostMapping("/{id}/favorite")
    public ResponseEntity<BookDto> toggleFavorite(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.toggleFavorite(id));
    }
}
