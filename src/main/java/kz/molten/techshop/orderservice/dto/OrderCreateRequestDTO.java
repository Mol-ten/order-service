package kz.molten.techshop.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kz.molten.techshop.orderservice.enumeration.Provider;

import java.util.List;

public record OrderCreateRequestDTO(
        @Valid @NotEmpty List<@NotNull OrderProductDTO> products,
        @NotNull Provider provider
        ) {
}
