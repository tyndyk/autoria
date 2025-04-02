package com.example.auto_ria.dto.responces;

import java.util.List;

public record PaginatedResponse<T>(
    List<T> content,
    int totalPages,
    long totalElements,
    int pageNumber,
    int pageSize
) {}
