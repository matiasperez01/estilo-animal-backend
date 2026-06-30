package com.estiloanimal.stocksistema.service;

import com.estiloanimal.stocksistema.model.Proveedor;
import com.estiloanimal.stocksistema.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }

    public Proveedor buscarPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));
    }

    public List<Proveedor> buscarPorNombre(String nombre) {
        return proveedorRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Proveedor guardar(Proveedor proveedor) {
        if (proveedor.getEmail() != null && proveedorRepository.existsByEmail(proveedor.getEmail())) {
            throw new RuntimeException("Ya existe un proveedor con el email: " + proveedor.getEmail());
        }
        return proveedorRepository.save(proveedor);
    }

    public Proveedor actualizar(Long id, Proveedor proveedorActualizado) {
        Proveedor proveedor = buscarPorId(id);
        proveedor.setNombre(proveedorActualizado.getNombre());
        proveedor.setTelefono(proveedorActualizado.getTelefono());
        proveedor.setEmail(proveedorActualizado.getEmail());
        proveedor.setDireccion(proveedorActualizado.getDireccion());
        return proveedorRepository.save(proveedor);
    }

    public void eliminar(Long id) {
        Proveedor proveedor = buscarPorId(id);
        proveedorRepository.delete(proveedor);
    }
}