package com.kpjunaid.dto;

import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportPostRequest {
    private String hatefulType;

    // Getter and Setter
    public String getHatefulType() {
        return hatefulType;
    }

    public void setHatefulType(String hatefulType) {
        this.hatefulType = hatefulType;
    }
}