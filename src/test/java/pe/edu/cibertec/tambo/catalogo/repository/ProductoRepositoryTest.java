package pe.edu.cibertec.tambo.catalogo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import pe.edu.cibertec.tambo.catalogo.entity.Categoria;
import pe.edu.cibertec.tambo.catalogo.entity.Producto;


@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void insertarProducto() {
        Categoria bebidas = categoriaRepository.save(
                Categoria.builder().nombre("Bebidas").descripcion("Gaseosas y aguas").build());

        Producto guardado = productoRepository.save(Producto.builder()
                .nombre("Inca Kola 500ml")
                .precio(new BigDecimal("3.50"))
                .stock(20)
                .stockMinimo(5)
                .categoria(bebidas)
                .build());

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getActivo()).isTrue();
        assertThat(productoRepository.findById(guardado.getId()))
                .get()
                .extracting(Producto::getNombre)
                .isEqualTo("Inca Kola 500ml");
    }

    @Test
    void actualizarProducto() {
        Categoria snacks = categoriaRepository.save(Categoria.builder().nombre("Snacks").build());
        Producto producto = productoRepository.save(Producto.builder()
                .nombre("Papas Lays")
                .precio(new BigDecimal("2.00"))
                .stock(10)
                .stockMinimo(3)
                .categoria(snacks)
                .build());

        producto.setPrecio(new BigDecimal("2.50"));
        producto.setStock(8);
        productoRepository.saveAndFlush(producto);

        Producto actualizado = productoRepository.findById(producto.getId()).orElseThrow();
        assertThat(actualizado.getPrecio()).isEqualByComparingTo("2.50");
        assertThat(actualizado.getStock()).isEqualTo(8);
    }

    @Test
    void eliminarProducto() {
        Categoria lacteos = categoriaRepository.save(Categoria.builder().nombre("Lácteos").build());
        Producto producto = productoRepository.save(Producto.builder()
                .nombre("Yogurt Gloria")
                .precio(new BigDecimal("4.20"))
                .stock(15)
                .stockMinimo(4)
                .categoria(lacteos)
                .build());

        // Baja lógica, igual que en ProductoService: se marca inactivo, no se borra la fila
        producto.setActivo(false);
        productoRepository.saveAndFlush(producto);

        assertThat(productoRepository.findById(producto.getId()))
                .get()
                .extracting(Producto::getActivo)
                .isEqualTo(false);
        assertThat(productoRepository.findByActivoTrueOrderByNombreAsc())
                .extracting(Producto::getId)
                .doesNotContain(producto.getId());
    }

    @Test
    void listarProductos() {
        Categoria abarrotes = categoriaRepository.save(Categoria.builder().nombre("Abarrotes").build());
        productoRepository.save(Producto.builder().nombre("Arroz Costeño 1kg")
                .precio(new BigDecimal("5.90")).stock(30).stockMinimo(5).categoria(abarrotes).build());
        productoRepository.save(Producto.builder().nombre("Azúcar Rubia 1kg")
                .precio(new BigDecimal("4.50")).stock(25).stockMinimo(5).categoria(abarrotes).build());
        productoRepository.save(Producto.builder().nombre("Aceite Primor 1L")
                .precio(new BigDecimal("11.90")).stock(12).stockMinimo(3).categoria(abarrotes)
                .activo(false).build());

        List<Producto> activos = productoRepository.findByActivoTrueOrderByNombreAsc();
        List<Producto> conArroz = productoRepository
                .findByActivoTrueAndNombreContainingIgnoreCaseOrderByNombreAsc("arroz");

        assertThat(activos).extracting(Producto::getNombre)
                .containsExactly("Arroz Costeño 1kg", "Azúcar Rubia 1kg");
        assertThat(conArroz).hasSize(1);
        assertThat(conArroz.get(0).getNombre()).isEqualTo("Arroz Costeño 1kg");
    }
}
