package com.estiloanimal.stocksistema.repository;

import com.estiloanimal.stocksistema.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByDni(String dni);

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);
}