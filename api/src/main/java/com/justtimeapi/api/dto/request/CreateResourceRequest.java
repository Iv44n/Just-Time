package com.justtimeapi.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateResourceRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String name,

        @NotBlank(message = "El tipo de recurso es obligatorio")
        String typeCode,

        @NotNull(message = "El usuario que crea el recurso es obligatorio")
        UUID createdBy,

        @Valid
        @NotNull(message = "Los detalles del recurso son obligatorios")
        Details details
) {
    public record Details(
        @NotBlank(message = "El tipo de motor de base de datos es obligatorio")
        String engine,

        // Formato desglosado
        String host,
        Double port,
        String database,
        String username,
        String password,
        String params,

        // Formato connectionUrl
        String connectionUrl
    ){}
}
