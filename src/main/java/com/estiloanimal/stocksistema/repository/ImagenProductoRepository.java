package com.estiloanimal.stocksistema.repository;

import com.estiloanimal.stocksistema.model.ImagenProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Long> {
    List<ImagenProducto> findByProductoIdOrderByOrdenAsc(Long productoId);
    void deleteByProductoId(Long productoId);
}