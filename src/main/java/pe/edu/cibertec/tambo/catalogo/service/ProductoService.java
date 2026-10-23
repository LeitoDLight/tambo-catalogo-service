package pe.edu.cibertec.tambo.catalogo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.tambo.catalogo.dto.ProductoRequest;
import pe.edu.cibertec.tambo.catalogo.dto.ProductoResponse;
import pe.edu.cibertec.tambo.catalogo.entity.Categoria;
import pe.edu.cibertec.tambo.catalogo.entity.Producto;
import pe.edu.cibertec.tambo.catalogo.repository.ProductoRepository;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(String nombre) {
        List<Producto> productos = (nombre == null || nombre.isBlank())
                ? productoRepository.findByActivoTrueOrderByNombreAsc()
                : productoRepository.findByActivoTrueAndNombreContainingIgnoreCaseOrderByNombreAsc(nombre.trim());
        return productos.stream().map(this::aResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return aResponse(buscar(id));
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Categoria categoria = categoriaService.buscar(request.categoriaId());
        Producto producto = Producto.builder()
                .nombre(request.nombre().trim())
                .descripcion(request.descripcion())
                .precio(request.precio())
                .stock(request.stock())
                .stockMinimo(request.stockMinimo())
                .categoria(categoria)
                .build();
        return aResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscar(id);
        Categoria categoria = categoriaService.buscar(request.categoriaId());
        producto.setNombre(request.nombre().trim());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setStockMinimo(request.stockMinimo());
        producto.setCategoria(categoria);
        return aResponse(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscar(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private Producto buscar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    private ProductoResponse aResponse(Producto p) {
        return new ProductoResponse(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getStock(),
                p.getStockMinimo(),
                p.getActivo(),
                p.getCategoria().getId(),
                p.getCategoria().getNombre());
    }
}
