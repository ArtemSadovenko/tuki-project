package com.example.tukitest.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageDTO {
    private Long id;
    private String fileName;
    private String contentType;
    private String base64Data;
    private List<String> tags;
}
