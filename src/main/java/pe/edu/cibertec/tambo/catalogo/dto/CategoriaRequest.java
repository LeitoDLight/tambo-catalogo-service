package pe.edu.cibertec.tambo.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre no debe superar 80 caracteres") String nombre,
        @Size(max = 255, message = "La descripción no debe superar 255 caracteres") String descripcion) {
}
