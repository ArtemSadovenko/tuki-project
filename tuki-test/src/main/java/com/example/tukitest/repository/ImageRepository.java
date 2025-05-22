package com.example.tukitest.repository;

import com.example.tukitest.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    @Query(
            value = """
        SELECT DISTINCT * FROM images
        WHERE id IN (
            SELECT image_id FROM tags
            WHERE tag_name IN (:tagNames)
        )
    """,
            nativeQuery = true
    )
    List<ImageEntity> findImageByTags(@Param("tagNames") List<String> tags);
}
