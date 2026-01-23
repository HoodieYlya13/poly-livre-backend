package com.poly.livre.backend.controllers;

import com.poly.livre.backend.models.dtos.BookDto;
import com.poly.livre.backend.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.livre.backend.models.dtos.BookRequestDto;
import com.poly.livre.backend.models.enums.DeliveryType;
import com.poly.livre.backend.models.enums.LocaleLanguage;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

        private MockMvc mockMvc;

        @Mock
        private BookService bookService;

        @InjectMocks
        private BookController bookController;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
        }

        @Test
        void shouldReturnBookById() throws Exception {
                UUID bookId = UUID.randomUUID();
                BookDto bookDto = BookDto.builder()
                                .id(bookId)
                                .title("Integration Test Book")
                                .author("Tester")
                                .build();

                given(bookService.getBookById(bookId)).willReturn(bookDto);

                mockMvc.perform(get("/books/{id}", bookId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(bookId.toString())))
                                .andExpect(jsonPath("$.title", is("Integration Test Book")))
                                .andExpect(jsonPath("$.author", is("Tester")));
        }

        @Test
        void shouldReturnTrendingBooks() throws Exception {
                BookDto book1 = BookDto.builder().title("Trending 1").build();
                BookDto book2 = BookDto.builder().title("Trending 2").build();
                List<BookDto> trendingBooks = List.of(book1, book2);

                given(bookService.getTrendingBooks()).willReturn(trendingBooks);

                mockMvc.perform(get("/books/trending")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].title", is("Trending 1")));
        }

        @Test
        void shouldReturnAllBooks() throws Exception {
                BookDto book1 = BookDto.builder().title("Book 1").build();
                List<BookDto> allBooks = List.of(book1);

                given(bookService.getAllBooks()).willReturn(allBooks);

                mockMvc.perform(get("/books/all")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].title", is("Book 1")));
        }

        @Test
        void shouldAddBook() throws Exception {
                UUID ownerId = UUID.randomUUID();
                BookRequestDto.InformationDto infoDto = new BookRequestDto.InformationDto(12, 2020, LocaleLanguage.FR,
                                DeliveryType.FREE);
                BookRequestDto requestDto = new BookRequestDto("New Book", "Author Name", "Description", 19.99, 30,
                                List.of("Science-Fiction"), infoDto, ownerId);

                BookDto bookDto = BookDto.builder()
                                .title("New Book")
                                .author("Author Name")
                                .price(19.99)
                                .build();

                given(bookService.addBook(any(BookRequestDto.class))).willReturn(bookDto);

                mockMvc.perform(post("/books/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(requestDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title", is("New Book")));
        }

        @Test
        void shouldReturnBooksByUserId() throws Exception {
                UUID userId = UUID.randomUUID();
                BookDto book = BookDto.builder().title("User Book").build();
                List<BookDto> userBooks = List.of(book);

                given(bookService.getBooksByUserId(userId)).willReturn(userBooks);

                mockMvc.perform(get("/books/user/{id}", userId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].title", is("User Book")));
        }
}
