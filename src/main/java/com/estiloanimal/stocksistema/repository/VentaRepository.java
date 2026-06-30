package com.estiloanimal.stocksistema.repository;

import com.estiloanimal.stocksistema.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByClienteId(Long clienteId);

    List<Venta> findByEstado(Venta.EstadoVenta estado);

    List<Venta> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);

    // Total de ventas en un rango de fechas
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fecha BETWEEN :desde AND :hasta AND v.estado = 'COMPLETADA'")
    BigDecimal sumTotalByFechaBetween(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    // Ventas del día
    @Query("SELECT v FROM Venta v WHERE DATE(v.fecha) = CURRENT_DATE")
    List<Venta> findVentasDeHoy();

    // Cantidad de ventas por estado
    Long countByEstado(Venta.EstadoVenta estado);
}