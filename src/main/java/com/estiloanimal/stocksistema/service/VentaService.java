package com.estiloanimal.stocksistema.service;

import com.estiloanimal.stocksistema.model.DetalleVenta;
import com.estiloanimal.stocksistema.model.Producto;
import com.estiloanimal.stocksistema.model.VarianteProducto;
import com.estiloanimal.stocksistema.model.Venta;
import com.estiloanimal.stocksistema.repository.VarianteProductoRepository;
import com.estiloanimal.stocksistema.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoService productoService;
    private final VarianteProductoRepository varianteRepository;

    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));
    }

    public List<Venta> listarPorCliente(Long clienteId) {
        return ventaRepository.findByClienteId(clienteId);
    }

    public List<Venta> listarPorEstado(Venta.EstadoVenta estado) {
        return ventaRepository.findByEstado(estado);
    }

    public List<Venta> listarPorFecha(LocalDateTime desde, LocalDateTime hasta) {
        return ventaRepository.findByFechaBetween(desde, hasta);
    }

    public List<Venta> listarVentasDeHoy() {
        return ventaRepository.findVentasDeHoy();
    }

    public BigDecimal totalVentasPorPeriodo(LocalDateTime desde, LocalDateTime hasta) {
        BigDecimal total = ventaRepository.sumTotalByFechaBetween(desde, hasta);
        return total != null ? total : BigDecimal.ZERO;
    }

    public Venta actualizarEstado(Long id, Venta.EstadoVenta estado) {
        Venta venta = buscarPorId(id);
        Venta.EstadoVenta estadoAnterior = venta.getEstado();
        venta.setEstado(estado);

        if (estado == Venta.EstadoVenta.ENTREGADO && estadoAnterior != Venta.EstadoVenta.ENTREGADO) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                if (detalle.getProducto() != null) {
                    productoService.actualizarStock(detalle.getProducto().getId(), -detalle.getCantidad());
                } else if (detalle.getNombreProducto() != null) {
                    List<Producto> productos = productoService.buscarPorNombre(detalle.getNombreProducto());
                    if (!productos.isEmpty()) {
                        Producto producto = productos.get(0);
                        List<VarianteProducto> variantes = varianteRepository.findByProductoId(producto.getId());

                        if (variantes.isEmpty()) {
                            // Sin variantes — descontar stock del producto directamente
                            productoService.actualizarStock(producto.getId(), -detalle.getCantidad());
                        } else if (detalle.getTalle() != null) {
                            // Con variantes — buscar la variante por talle
                            variantes.stream()
                                    .filter(v -> v.getTalle().equalsIgnoreCase(detalle.getTalle()))
                                    .findFirst()
                                    .ifPresent(v -> {
                                        v.setStock(v.getStock() - detalle.getCantidad());
                                        varianteRepository.save(v);
                                    });
                        }
                    }
                }
            }
        }

        if (estado == Venta.EstadoVenta.CANCELADA && estadoAnterior == Venta.EstadoVenta.ENTREGADO) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                if (detalle.getProducto() != null) {
                    productoService.actualizarStock(detalle.getProducto().getId(), detalle.getCantidad());
                } else if (detalle.getNombreProducto() != null) {
                    List<Producto> productos = productoService.buscarPorNombre(detalle.getNombreProducto());
                    if (!productos.isEmpty()) {
                        Producto producto = productos.get(0);
                        List<VarianteProducto> variantes = varianteRepository.findByProductoId(producto.getId());

                        if (variantes.isEmpty()) {
                            productoService.actualizarStock(producto.getId(), detalle.getCantidad());
                        } else if (detalle.getTalle() != null) {
                            variantes.stream()
                                    .filter(v -> v.getTalle().equalsIgnoreCase(detalle.getTalle()))
                                    .findFirst()
                                    .ifPresent(v -> {
                                        v.setStock(v.getStock() + detalle.getCantidad());
                                        varianteRepository.save(v);
                                    });
                        }
                    }
                }
            }
        }

        return ventaRepository.save(venta);
    }

    // Método principal: crea la venta y descuenta el stock automáticamente
    public Venta crearVenta(Venta venta) {
        BigDecimal total = BigDecimal.ZERO;

        for (DetalleVenta detalle : venta.getDetalles()) {
            // Verificar y descontar stock
            Producto producto = productoService.buscarPorId(detalle.getProducto().getId());
            productoService.actualizarStock(producto.getId(), -detalle.getCantidad());

            // Establecer precio actual del producto
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setVenta(venta);

            // Acumular total
            BigDecimal subtotal = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()));
            detalle.setSubtotal(subtotal);
            total = total.add(subtotal);
        }

        venta.setTotal(total);
        return ventaRepository.save(venta);
    }

    // Cancelar venta: devuelve el stock
    public Venta cancelarVenta(Long id) {
        Venta venta = buscarPorId(id);

        if (venta.getEstado() == Venta.EstadoVenta.CANCELADA) {
            throw new RuntimeException("La venta ya está cancelada");
        }

        // Devolver stock de cada producto
        for (DetalleVenta detalle : venta.getDetalles()) {
            productoService.actualizarStock(detalle.getProducto().getId(), detalle.getCantidad());
        }

        venta.setEstado(Venta.EstadoVenta.CANCELADA);
        return ventaRepository.save(venta);
    }

    public Venta registrar(Venta venta) {
        BigDecimal total = BigDecimal.ZERO;

        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                detalle.setVenta(venta);
                if (detalle.getSubtotal() == null && detalle.getPrecioUnitario() != null && detalle.getCantidad() != null) {
                    detalle.setSubtotal(
                            detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()))
                    );
                }
                if (detalle.getSubtotal() != null) {
                    total = total.add(detalle.getSubtotal());
                }
            }
        }

        if (venta.getTotal() == null) {
            venta.setTotal(total);
        }

        return ventaRepository.save(venta);
    }

    public void eliminar(Long id) {
        ventaRepository.deleteById(id);
    }

}