package com.poly.livre.backend.models.converters;

import com.poly.livre.backend.models.dtos.TestimonialDto;
import com.poly.livre.backend.models.entities.Testimonial;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

@Component
@RequiredArgsConstructor
public class TestimonialConverter implements Converter<Testimonial, TestimonialDto> {

    private final UserConverter userConverter;

    @Override
    @NonNull
    public TestimonialDto convert(@NonNull Testimonial testimonial) {
        return TestimonialDto.builder()
                .id(testimonial.getId())
                .user(userConverter.convert(testimonial.getUser()))
                .rating(testimonial.getRating())
                .comment(testimonial.getComment())
                .build();
    }
}
