package ru.yandex.practicum.catsgram.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "id" })
public class Image {
    Long id;
    Long postId;
    String originalFileName;
    String filePath;
}
