package com.poly.livre.backend.configurations;

import com.poly.livre.backend.models.entities.Book;
import com.poly.livre.backend.models.entities.BookReview;
import com.poly.livre.backend.models.entities.Testimonial;
import com.poly.livre.backend.models.entities.User;
import com.poly.livre.backend.models.enums.DeliveryType;
import com.poly.livre.backend.models.enums.LocaleLanguage;
import com.poly.livre.backend.models.enums.UserStatus;
import com.poly.livre.backend.repositories.BookRepository;
import com.poly.livre.backend.repositories.BookReviewRepository;
import com.poly.livre.backend.repositories.TestimonialRepository;
import com.poly.livre.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookReviewRepository bookReviewRepository;
    private final TestimonialRepository testimonialRepository;
    private final com.poly.livre.backend.repositories.ImageRepository imageRepository;

    private static final String COMMON_DESCRIPTION = "Le roman suit les destins croisés de plusieurs personnages, mais se concentre principalement sur Jean Valjean, un ancien forçat condamné pour vol de pain qui devient un homme intègre, et sur l'inspecteur Javert, déterminé à le ramener à la justice.\nL'histoire se déroule dans la France du 19ème siècle, couvrant des événements historiques comme la révolution de 1832. À travers ces personnages et événements, Hugo explore des thèmes universels tels que la rédemption, l'amour, la misère et l'injustice sociale.";

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping data seeding.");
            return;
        }

        log.info("Seeding database with mock data...");

        User owner = User.builder()
                .username("Mocked Owner")
                .email("owner@example.com")
                .firstName("Mocked Owner")
                .lastName("Mocked Owner")
                .status(UserStatus.WORKER)
                .build();

        User user1 = User.builder()
                .username("PDG")
                .email("user1@example.com")
                .firstName("Naheem")
                .lastName("Akaby")
                .status(UserStatus.STUDENT)
                .build();

        User user2 = User.builder()
                .username("RacistePrime")
                .email("user2@example.com")
                .firstName("Imed")
                .lastName("Zarour")
                .status(UserStatus.STUDENT)
                .build();

        User user3 = User.builder()
                .username("example")
                .email("user3@example.com")
                .firstName("John")
                .lastName("Doe")
                .status(UserStatus.TEACHER)
                .build();

        userRepository.saveAll(List.of(owner, user1, user2, user3));
        log.info("Users seeded.");

        byte[] imageBytes = getClass().getResourceAsStream("/static/img/mock_image.png").readAllBytes();
        com.poly.livre.backend.models.entities.Image mockImage = com.poly.livre.backend.models.entities.Image.builder()
                .name("mock_image.png")
                .type("image/png")
                .data(imageBytes)
                .build();
        imageRepository.save(mockImage);
        log.info("Mock image seeded.");

        List<String> styles = List.of("Mocked Style", "Mocked Style 2", "Mocked Style 3");

        Book book1 = createBook("Mocked Book 1", owner, styles, mockImage);
        Book book2 = createBook("Mocked Book 2", owner, styles, mockImage);
        Book book3 = createBook("Mocked Book 3", owner, styles, mockImage);
        Book book4 = createBook("Mocked Book 4", owner, styles, mockImage);

        bookRepository.saveAll(List.of(book1, book2, book3, book4));
        log.info("Books seeded.");

        BookReview review1 = BookReview.builder()
                .book(book1)
                .user(user1)
                .rating(5)
                .comment("Mocked Comment")
                .language(LocaleLanguage.FR)
                .build();

        BookReview review2 = BookReview.builder()
                .book(book2)
                .user(user2)
                .rating(4)
                .comment("Mocked Comment")
                .language(LocaleLanguage.FR)
                .build();

        BookReview review3 = BookReview.builder()
                .book(book3)
                .user(user3)
                .rating(4)
                .comment("Mocked Comment")
                .language(LocaleLanguage.FR)
                .build();

        bookReviewRepository.saveAll(List.of(review1, review2, review3));
        log.info("Reviews seeded.");

        createTestimonials(user1, user2, user3);
        log.info("Testimonials seeded.");

        log.info("Database seeding completed.");
    }

    private Book createBook(String title, User owner, List<String> styles,
            com.poly.livre.backend.models.entities.Image coverImage) {
        return Book.builder()
                .title(title)
                .description(COMMON_DESCRIPTION)
                .author("Mocked Author")
                .coverImage(coverImage)
                .favorite(false)
                .styles(styles)
                .rating(4.6)
                .price(3.0)
                .owner(owner)
                .pages(100)
                .year(2022)
                .language(LocaleLanguage.FR)
                .delivery(DeliveryType.FREE)
                .loanDuration(14)
                .loaned(false)
                .build();
    }

    private void createTestimonials(User user1, User user2, User user3) {
        Testimonial t1 = createTestimonial(user1, 5, LocaleLanguage.FR,
                "Liprêrie a transformé ma façon de découvrir de nouveaux livres. Le choix est incroyable et la livraison est toujours ponctuelle. J'ai lu plus de livres cette année que jamais auparavant !");
        Testimonial t2 = createTestimonial(user2, 4, LocaleLanguage.FR,
                "Enfin un service de prêt de livres qui comprend les lecteurs ! Pas de désordre, pas de tracas. J’adore pouvoir rendre les livres sans stress et avoir toujours quelque chose de nouveau à lire.");
        Testimonial t3 = createTestimonial(user3, 4, LocaleLanguage.FR,
                "Notre club de lecture est entièrement passé à Liprêrie. Tout le monde reçoit ses exemplaires à temps et nous économisons énormément d'argent !");

        Testimonial t4 = createTestimonial(user1, 5, LocaleLanguage.EN,
                "Liprêrie has transformed the way I discover new books. The selection is incredible and the delivery is always on time. I've read more books this year than ever before!");
        Testimonial t5 = createTestimonial(user2, 4, LocaleLanguage.EN,
                "Finally, a book lending service that understands readers! No mess, no hassle. I love being able to return books stress-free and always have something new to read.");
        Testimonial t6 = createTestimonial(user3, 4, LocaleLanguage.EN,
                "Our book club has completely switched to Liprêrie. Everyone receives their copies on time and we're saving a lot of money!");

        testimonialRepository.saveAll(List.of(t1, t2, t3, t4, t5, t6));
    }

    private Testimonial createTestimonial(User user, int rating, LocaleLanguage language, String comment) {
        return Testimonial.builder()
                .user(user)
                .rating(rating)
                .language(language)
                .comment(comment)
                .build();
    }
}
