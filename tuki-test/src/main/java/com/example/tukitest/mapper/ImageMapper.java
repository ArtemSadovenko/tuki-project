package com.example.tukitest.mapper;

import com.example.tukitest.entity.ImageEntity;
import com.example.tukitest.entity.Tag;
import com.example.tukitest.external.ImageDTO;
import com.example.tukitest.external.dto.ResponseDTO;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ImageMapper {
    private double CONFIDENCE_THRESHOLD = 40.0;

    public ImageEntity toImageEntity(MultipartFile file) throws IOException {
        ImageEntity image = ImageEntity.builder()
                .contentType(file.getContentType())
                .fileName(file.getOriginalFilename())
                .data(file.getBytes())
                .build();
        return image;
    }

    public MultipartFile toMultipartFile(ImageEntity entity) {
        return new MockMultipartFile(
                entity.getFileName(),
                entity.getFileName(),
                entity.getContentType(),
                entity.getData()
        );
    }

    public ImageEntity createImageEntity(MultipartFile file, ResponseDTO responseDTO) {
        try {
            ImageEntity imageEntity = ImageEntity.builder()
                    .contentType(file.getContentType())
                    .fileName(file.getOriginalFilename())
                    .data(file.getBytes())
                    .build();

            imageEntity.setTags(
                    responseDTO.getResult().getTags().stream()
                            .filter(e -> e.getConfidence() >= CONFIDENCE_THRESHOLD)
                            .map(e -> {
                                        Tag tag = new Tag();
                                        tag.setTagName(e.getTag().getEn());
                                        tag.setImage(imageEntity);
                                        return tag;
                                    }
                            )
                            .collect(Collectors.toList())
            );
            return imageEntity;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Tag> externalToTag(ResponseDTO responseDTO, ImageEntity imageEntity) {
        return responseDTO.getResult().getTags().stream()
                .filter(e -> e.getConfidence() >= CONFIDENCE_THRESHOLD)
                .map(e -> {
                            Tag tag = new Tag();
                            tag.setTagName(e.getTag().getEn());
                            tag.setImage(imageEntity);
                            return tag;
                        }
                )
                .collect(Collectors.toList());
    }

    public ImageDTO entityToDTO(ImageEntity imageEntity) {
        String base64Data = Base64.getEncoder().encodeToString(imageEntity.getData());

        return  ImageDTO.builder()
                .id(imageEntity.getId())
                .tags(imageEntity.getTags().stream().map(Tag::getTagName).collect(Collectors.toList()))
                .base64Data(base64Data)
                .contentType(imageEntity.getContentType())
                .fileName(imageEntity.getFileName())
                .build();
    }
}
