package pe.edu.cibertec.tambo.catalogo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.tambo.catalogo.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrueOrderByNombreAsc();

    List<Producto> findByActivoTrueAndNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);

    boolean existsByCategoriaIdAndActivoTrue(Long categoriaId);

    void deleteByCategoriaIdAndActivoFalse(Long categoriaId);
}
