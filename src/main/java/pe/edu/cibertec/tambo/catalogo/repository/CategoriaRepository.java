package pe.edu.cibertec.tambo.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.tambo.catalogo.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
