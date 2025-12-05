package com.justtimeapi.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ResourceType {
    private Integer id;
    private String code;
    private String description;
}
