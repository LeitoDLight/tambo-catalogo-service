package pe.edu.cibertec.tambo.catalogo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.tambo.catalogo.dto.CategoriaRequest;
import pe.edu.cibertec.tambo.catalogo.dto.CategoriaResponse;
import pe.edu.cibertec.tambo.catalogo.entity.Categoria;
import pe.edu.cibertec.tambo.catalogo.repository.CategoriaRepository;
import pe.edu.cibertec.tambo.catalogo.repository.ProductoRepository;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream().map(this::aResponse).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Long id) {
        return aResponse(buscar(id));
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        String nombre = request.nombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre");
        }
        Categoria categoria = Categoria.builder()
                .nombre(nombre)
                .descripcion(request.descripcion())
                .build();
        return aResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscar(id);
        String nombre = request.nombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre");
        }
        categoria.setNombre(nombre);
        categoria.setDescripcion(request.descripcion());
        return aResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscar(id);
        // Solo cuentan los productos activos: los dados de baja ya no se ven en el sistema
        if (productoRepository.existsByCategoriaIdAndActivoTrue(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: la categoría tiene productos activos asociados");
        }
        // Se limpian los productos dados de baja para que no bloqueen la FK
        productoRepository.deleteByCategoriaIdAndActivoFalse(id);
        categoriaRepository.delete(categoria);
    }

    public Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
    }

    private CategoriaResponse aResponse(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion());
    }
}
