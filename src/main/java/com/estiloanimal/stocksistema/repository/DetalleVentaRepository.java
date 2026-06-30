package com.estiloanimal.stocksistema.repository;

import com.estiloanimal.stocksistema.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByVentaId(Long ventaId);

    List<DetalleVenta> findByProductoId(Long productoId);

    // Producto más vendido
    @Query("SELECT d.producto.id, d.producto.nombre, SUM(d.cantidad) as total " +
            "FROM DetalleVenta d GROUP BY d.producto.id, d.producto.nombre " +
            "ORDER BY total DESC")
    List<Object[]> findProductosMasVendidos();

    // Cantidad total vendida de un producto
    @Query("SELECT SUM(d.cantidad) FROM DetalleVenta d WHERE d.producto.id = :productoId")
    Integer sumCantidadByProductoId(@Param("productoId") Long productoId);
}