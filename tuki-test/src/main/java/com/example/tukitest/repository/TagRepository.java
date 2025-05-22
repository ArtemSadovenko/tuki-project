package com.example.tukitest.repository;

import com.example.tukitest.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

        @Query("SELECT t FROM Tag t JOIN FETCH t.image WHERE t.tagName IN :tagNames")
//    @EntityGraph(attributePaths = "image")
    List<Tag> findByTagNameIn(@Param("tagNames") List<String> tagNames);

}
