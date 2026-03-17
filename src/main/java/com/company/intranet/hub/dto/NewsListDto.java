package com.company.intranet.hub.dto;

import java.util.List;

public record NewsListDto(
        List<NewsPostDto> content,
        int               page,
        int               size,
        long              total,
        int               totalPages
) {}
