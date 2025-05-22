package com.example.tukitest.controller;

import com.example.tukitest.external.ExternalService;
import com.example.tukitest.external.ImageDTO;
import com.example.tukitest.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/v1/image")
public class ImageController {
    @Autowired
    private ImageService imageService;

    @Autowired
    private ExternalService externalService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createImage(@RequestPart(value = "file") MultipartFile file) {
        imageService.createImage(file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<Set<ImageDTO>> search(@RequestBody List<String> tags) {
        return new ResponseEntity<>(imageService.search(tags),HttpStatus.OK);
    }


}
