package pe.edu.cibertec.tambo.catalogo.dto;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer stock,
        Integer stockMinimo,
        Boolean activo,
        Long categoriaId,
        String categoriaNombre) {
}
