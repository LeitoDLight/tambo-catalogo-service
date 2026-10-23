package pe.edu.cibertec.tambo.catalogo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.tambo.catalogo.entity.Producto;
import pe.edu.cibertec.tambo.catalogo.repository.ProductoRepository;

@Service
@RequiredArgsConstructor
public class StockService {

    public record StockBajo(Long productoId, Integer stockActual, String nombre) {
    }

    private final ProductoRepository productoRepository;

    @Transactional
    public List<StockBajo> descontar(Long ventaId, Map<Long, Integer> cantidades) {
        List<StockBajo> alertas = new ArrayList<>();

        for (Map.Entry<Long, Integer> item : cantidades.entrySet()) {
            Long productoId = item.getKey();
            int cantidad = item.getValue();

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Venta " + ventaId + ": el producto " + productoId + " no existe"));

            if (!Boolean.TRUE.equals(producto.getActivo())) {
                throw new IllegalStateException(
                        "Venta " + ventaId + ": el producto " + productoId + " está dado de baja");
            }
            if (producto.getStock() < cantidad) {
                throw new IllegalStateException("Venta " + ventaId + ": stock insuficiente para el producto "
                        + productoId + " (hay " + producto.getStock() + ", se pidieron " + cantidad + ")");
            }

            producto.setStock(producto.getStock() - cantidad);
            productoRepository.save(producto);

            if (producto.getStock() <= producto.getStockMinimo()) {
                alertas.add(new StockBajo(producto.getId(), producto.getStock(), producto.getNombre()));
            }
        }
        return alertas;
    }
}
