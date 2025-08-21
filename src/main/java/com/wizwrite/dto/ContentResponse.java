package com.wizwrite.dto;

import lombok.Data;

@Data
public class ContentResponse {
    private String generatedText;
    private int remainingCredits;
}
