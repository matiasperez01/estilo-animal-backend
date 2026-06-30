package com.estiloanimal.stocksistema.service;

import com.estiloanimal.stocksistema.model.Producto;
import com.estiloanimal.stocksistema.repository.CategoriaRepository;
import com.estiloanimal.stocksistema.repository.ProductoRepository;
import com.estiloanimal.stocksistema.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    public Producto buscarPorCodigoBarra(String codigoBarra) {
        return productoRepository.findByCodigoBarra(codigoBarra)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con código: " + codigoBarra));
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> listarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    public List<Producto> listarPorProveedor(Long proveedorId) {
        return productoRepository.findByProveedorId(proveedorId);
    }

    public List<Producto> listarProductosBajoStock() {
        return productoRepository.findProductosBajoStock();
    }

    public Producto guardar(Producto producto) {
        if (producto.getCodigoBarra() != null &&
                productoRepository.existsByCodigoBarra(producto.getCodigoBarra())) {
            throw new RuntimeException("Ya existe un producto con el código: " + producto.getCodigoBarra());
        }
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto productoActualizado) {
        Producto producto = buscarPorId(id);
        producto.setNombre(productoActualizado.getNombre());
        producto.setDescripcion(productoActualizado.getDescripcion());
        producto.setPrecio(productoActualizado.getPrecio());
        producto.setStock(productoActualizado.getStock());
        producto.setStockMinimo(productoActualizado.getStockMinimo());
        producto.setCodigoBarra(productoActualizado.getCodigoBarra());
        producto.setImagenUrl(productoActualizado.getImagenUrl());
        producto.setEspecie(productoActualizado.getEspecie());

        if (productoActualizado.getCategoria() != null && productoActualizado.getCategoria().getId() != null) {
            producto.setCategoria(categoriaRepository.findById(productoActualizado.getCategoria().getId()).orElse(null));
        } else {
            producto.setCategoria(null);
        }

        if (productoActualizado.getProveedor() != null && productoActualizado.getProveedor().getId() != null) {
            producto.setProveedor(proveedorRepository.findById(productoActualizado.getProveedor().getId()).orElse(null));
        } else {
            producto.setProveedor(null);
        }

        return productoRepository.save(producto);
    }

    public void actualizarStock(Long id, Integer cantidad) {
        Producto producto = buscarPorId(id);
        int nuevoStock = producto.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        producto.setStock(nuevoStock);
        productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        Producto producto = buscarPorId(id);
        productoRepository.delete(producto);
    }
}