package com.estiloanimal.stocksistema.repository;

import com.estiloanimal.stocksistema.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoBarra(String codigoBarra);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByCategoriaId(Long categoriaId);

    List<Producto> findByProveedorId(Long proveedorId);

    // Productos con stock menor o igual al stock mínimo (para alertas)
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo")
    List<Producto> findProductosBajoStock();

    // Productos sin stock
    List<Producto> findByStockEquals(Integer stock);

    boolean existsByCodigoBarra(String codigoBarra);

    List<Producto> findByDestacadoTrue(Boolean destacado);

    List<Producto> findByDestacadoTrue();
}