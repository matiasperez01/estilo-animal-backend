package com.estiloanimal.stocksistema.repository;

import com.estiloanimal.stocksistema.model.VarianteProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VarianteProductoRepository extends JpaRepository<VarianteProducto, Long> {
    List<VarianteProducto> findByProductoId(Long productoId);
    void deleteByProductoId(Long productoId);
}