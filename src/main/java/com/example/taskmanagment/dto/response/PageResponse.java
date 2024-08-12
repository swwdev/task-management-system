package com.example.taskmanagment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResponse<T> {

    @Schema(description = "collection of required data")
    private List<T> content;

    @Schema(description = "metadata of collection")
    private Metadata metadata;

    public static <T> PageResponse<T> of(Page<T> page) {
        Metadata metadata = new Metadata(page.getNumber(), page.getSize(), page.getTotalElements());
        return new PageResponse<>(page.getContent(), metadata);
    }

    @Data
    @AllArgsConstructor
    public static class Metadata {

        @Schema(description = "current page")
        private int page;

        @Schema(description = "size of collection")
        private int size;

        @Schema(description = "total elements amount")
        private long totalElements;
    }
}
