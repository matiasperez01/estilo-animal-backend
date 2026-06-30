package com.estiloanimal.stocksistema.repository;

import com.estiloanimal.stocksistema.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByEmail(String email);

    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByEmail(String email);
}