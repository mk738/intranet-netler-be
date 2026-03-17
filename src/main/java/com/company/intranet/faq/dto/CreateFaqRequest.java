package com.company.intranet.faq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFaqRequest(
        @NotBlank @Size(max = 1000) String question,
        @NotBlank                   String answer,
                                    String category
) {}
