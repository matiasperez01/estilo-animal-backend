package com.estiloanimal.stocksistema.repository;

import com.estiloanimal.stocksistema.model.PedidoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoProveedorRepository extends JpaRepository<PedidoProveedor, Long> {
}
