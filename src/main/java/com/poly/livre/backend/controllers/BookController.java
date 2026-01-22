package com.poly.livre.backend.controllers;

import com.poly.livre.backend.models.dtos.BookDto;
import com.poly.livre.backend.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/trending")
    public ResponseEntity<List<BookDto>> getTrendingBooks() {
        return ResponseEntity.ok(bookService.getTrendingBooks());
    }

    @PostMapping("/add")
    public ResponseEntity<BookDto> addBook() {
        return ResponseEntity.ok(bookService.addBook());
    }
}
