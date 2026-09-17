package com.estiloanimal.stocksistema.repository;

import com.estiloanimal.stocksistema.model.DetallePedidoProveedor;
import com.estiloanimal.stocksistema.model.PedidoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetallePedidoProveedorRepository extends JpaRepository<DetallePedidoProveedor, Long> {

    // IDs de productos que tienen al menos un pedido a proveedor en el estado dado
    // (usado para marcar productos como "Próximamente" mientras el pedido está PENDIENTE).
    @Query("SELECT DISTINCT d.producto.id FROM DetallePedidoProveedor d " +
           "WHERE d.pedido.estado = :estado AND d.producto IS NOT NULL")
    List<Long> findProductoIdsPorEstadoPedido(@Param("estado") PedidoProveedor.EstadoPedidoProveedor estado);
}
