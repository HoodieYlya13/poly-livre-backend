package com.poly.livre.backend.controllers;

import com.poly.livre.backend.exceptions.NotFoundException;

import com.poly.livre.backend.models.entities.Image;
import com.poly.livre.backend.repositories.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageRepository imageRepository;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable UUID id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        com.poly.livre.backend.exceptions.errors.ImageErrorCode.IMAGE_NOT_FOUND, id));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getType()))
                .body(image.getData());
    }
}
