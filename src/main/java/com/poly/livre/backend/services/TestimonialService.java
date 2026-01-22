package com.poly.livre.backend.services;

import com.poly.livre.backend.models.converters.TestimonialConverter;
import com.poly.livre.backend.models.dtos.TestimonialDto;
import com.poly.livre.backend.models.enums.LocaleLanguage;
import com.poly.livre.backend.repositories.TestimonialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestimonialService {

    private final TestimonialRepository testimonialRepository;
    private final TestimonialConverter testimonialConverter;

    @Transactional(readOnly = true)
    public List<TestimonialDto> getTestimonialsByLocale(String locale) {
        LocaleLanguage language;
        try {
            language = LocaleLanguage.valueOf(locale.toUpperCase());
        } catch (IllegalArgumentException e) {
            return List.of();
        }

        return testimonialRepository.findByLanguage(language, PageRequest.of(0, 3))
                .stream()
                .map(testimonialConverter::convert)
                .toList();
    }
}
