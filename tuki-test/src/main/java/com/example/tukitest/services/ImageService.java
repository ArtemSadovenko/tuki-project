package com.example.tukitest.services;

import com.example.tukitest.entity.ImageEntity;
import com.example.tukitest.external.ExternalService;
import com.example.tukitest.external.ImageDTO;
import com.example.tukitest.mapper.ImageMapper;
import com.example.tukitest.repository.ImageRepository;
import com.example.tukitest.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ImageMapper imageMapper;
    @Autowired
    private ExternalService externalService;
    @Autowired
    private TagRepository tagRepository;

    public void createImage(MultipartFile file) {
        try {
            ImageEntity save = imageMapper.toImageEntity(file);
            save.setTags(
                    imageMapper.externalToTag(
                            externalService.getTags(file),
                            save
                    )
            );
            imageRepository.save(save);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MultipartFile> getAll() {
        return imageRepository.findAll().stream().map(imageMapper::toMultipartFile).collect(Collectors.toList());
    }

    @Transactional
    public Set<ImageDTO> search(List<String> tags) {
        return imageRepository.findImageByTags(tags).stream().map(e -> imageMapper.entityToDTO(e)).collect(Collectors.toSet());
    }
}
