package com.marriott.aura.tapquery.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class ToolResponse<T> {

    private String tool;
    private boolean success;
    private T data;
    private long count;
    private long totalCount;
    private int page;
    private int size;
    private Map<String, Object> query;
    private String message;

    @Builder.Default
    private String timestamp = Instant.now().toString();

    public static <T> ToolResponse<T> of(String tool, T data, long count,
                                          long totalCount, int page, int size,
                                          Map<String, Object> query) {
        return ToolResponse.<T>builder()
                .tool(tool)
                .success(true)
                .data(data)
                .count(count)
                .totalCount(totalCount)
                .page(page)
                .size(size)
                .query(query)
                .build();
    }

    public static <T> ToolResponse<T> single(String tool, T data, Map<String, Object> query) {
        return ToolResponse.<T>builder()
                .tool(tool)
                .success(true)
                .data(data)
                .count(data != null ? 1 : 0)
                .totalCount(data != null ? 1 : 0)
                .page(0)
                .size(1)
                .query(query)
                .build();
    }

    public static <T> ToolResponse<T> notFound(String tool, Map<String, Object> query) {
        return ToolResponse.<T>builder()
                .tool(tool)
                .success(false)
                .count(0)
                .totalCount(0)
                .page(0)
                .size(0)
                .query(query)
                .message("No records found")
                .build();
    }
}
