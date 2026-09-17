package com.estiloanimal.stocksistema.service;

import com.estiloanimal.stocksistema.model.PedidoProveedor;
import com.estiloanimal.stocksistema.model.Producto;
import com.estiloanimal.stocksistema.model.VarianteProducto;
import com.estiloanimal.stocksistema.repository.CategoriaRepository;
import com.estiloanimal.stocksistema.repository.DetallePedidoProveedorRepository;
import com.estiloanimal.stocksistema.repository.ProductoRepository;
import com.estiloanimal.stocksistema.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;
    private final DetallePedidoProveedorRepository detallePedidoProveedorRepository;

    // Orden natural de talles/opciones (numérico si corresponde) para que al
    // agregar un talle más chico que los existentes no quede al final de la lista.
    private static void ordenarVariantes(Producto producto) {
        if (producto.getVariantes() != null) {
            producto.getVariantes().sort((a, b) -> VarianteProducto.compararTalles(a.getTalle(), b.getTalle()));
        }
    }

    private static List<Producto> ordenarVariantes(List<Producto> productos) {
        productos.forEach(ProductoService::ordenarVariantes);
        return productos;
    }

    // Marca "proximamente" = true en los productos que tienen un pedido a
    // proveedor todavía PENDIENTE, para mostrarlos como "Próximamente" /
    // "Reservar" en la tienda en vez de "Sin stock".
    private List<Producto> marcarProximamente(List<Producto> productos) {
        Set<Long> idsProximamente = new HashSet<>(
                detallePedidoProveedorRepository.findProductoIdsPorEstadoPedido(PedidoProveedor.EstadoPedidoProveedor.PENDIENTE)
        );
        productos.forEach(p -> p.setProximamente(idsProximamente.contains(p.getId())));
        return productos;
    }

    private Producto marcarProximamente(Producto producto) {
        marcarProximamente(List.of(producto));
        return producto;
    }

    public List<Producto> listarTodos() {
        return marcarProximamente(ordenarVariantes(productoRepository.findAll()));
    }

    public Producto buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        ordenarVariantes(producto);
        marcarProximamente(producto);
        return producto;
    }

    public Producto buscarPorCodigoBarra(String codigoBarra) {
        return productoRepository.findByCodigoBarra(codigoBarra)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con código: " + codigoBarra));
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return marcarProximamente(ordenarVariantes(productoRepository.findByNombreContainingIgnoreCase(nombre)));
    }

    public List<Producto> listarPorCategoria(Long categoriaId) {
        return marcarProximamente(ordenarVariantes(productoRepository.findByCategoriaId(categoriaId)));
    }

    public List<Producto> listarPorProveedor(Long proveedorId) {
        return marcarProximamente(ordenarVariantes(productoRepository.findByProveedorId(proveedorId)));
    }

    public List<Producto> listarProductosBajoStock() {
        return ordenarVariantes(productoRepository.findProductosBajoStock());
    }

    public Producto guardar(Producto producto) {
        if (producto.getCodigoBarra() != null &&
                productoRepository.existsByCodigoBarra(producto.getCodigoBarra())) {
            throw new RuntimeException("Ya existe un producto con el código: " + producto.getCodigoBarra());
        }
        if (producto.getDestacado() == null) {
            producto.setDestacado(false);
        }
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto productoActualizado) {
        Producto producto = buscarPorId(id);
        producto.setNombre(productoActualizado.getNombre());
        producto.setDescripcion(productoActualizado.getDescripcion());
        producto.setPrecio(productoActualizado.getPrecio());
        producto.setPrecioDescuento(productoActualizado.getPrecioDescuento());
        producto.setStock(productoActualizado.getStock());
        producto.setStockMinimo(productoActualizado.getStockMinimo());
        producto.setCodigoBarra(productoActualizado.getCodigoBarra());
        producto.setImagenUrl(productoActualizado.getImagenUrl());
        producto.setEspecie(productoActualizado.getEspecie());
        producto.setDestacado(productoActualizado.getDestacado());
        producto.setTipoVariante(productoActualizado.getTipoVariante());
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

    public List<Producto> listarDestacados() {
        return marcarProximamente(ordenarVariantes(productoRepository.findByDestacadoTrue()));
    }

}