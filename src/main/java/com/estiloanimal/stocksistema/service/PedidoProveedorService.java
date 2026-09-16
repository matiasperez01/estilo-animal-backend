package com.estiloanimal.stocksistema.service;

import com.estiloanimal.stocksistema.model.DetallePedidoProveedor;
import com.estiloanimal.stocksistema.model.PedidoProveedor;
import com.estiloanimal.stocksistema.model.VarianteProducto;
import com.estiloanimal.stocksistema.repository.PedidoProveedorRepository;
import com.estiloanimal.stocksistema.repository.ProductoRepository;
import com.estiloanimal.stocksistema.repository.VarianteProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoProveedorService {

    private final PedidoProveedorRepository pedidoProveedorRepository;
    private final ProductoService productoService;
    private final VarianteProductoRepository varianteRepository;
    private final ProductoRepository productoRepository;

    // Si el detalle vino con "productoId" (id plano) en vez de un objeto
    // "producto" anidado, lo resuelve a una referencia gestionada por Hibernate.
    private void resolverProducto(DetallePedidoProveedor detalle) {
        if (detalle.getProducto() == null && detalle.getProductoId() != null) {
            detalle.setProducto(productoRepository.getReferenceById(detalle.getProductoId()));
        }
    }

    public List<PedidoProveedor> listarTodos() {
        return pedidoProveedorRepository.findAll();
    }

    public PedidoProveedor buscarPorId(Long id) {
        return pedidoProveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido a proveedor no encontrado con ID: " + id));
    }

    public PedidoProveedor crear(PedidoProveedor pedido) {
        BigDecimal total = BigDecimal.ZERO;

        if (pedido.getDetalles() != null) {
            for (DetallePedidoProveedor detalle : pedido.getDetalles()) {
                detalle.setPedido(pedido);
                resolverProducto(detalle);
                if (detalle.getSubtotal() == null && detalle.getPrecioUnitario() != null && detalle.getCantidad() != null) {
                    detalle.setSubtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())));
                }
                if (detalle.getSubtotal() != null) {
                    total = total.add(detalle.getSubtotal());
                }
            }
        }

        pedido.setTotal(total);
        return pedidoProveedorRepository.save(pedido);
    }

    public PedidoProveedor actualizarEstado(Long id, PedidoProveedor.EstadoPedidoProveedor estado) {
        PedidoProveedor pedido = buscarPorId(id);
        PedidoProveedor.EstadoPedidoProveedor estadoAnterior = pedido.getEstado();
        pedido.setEstado(estado);

        if (estado == PedidoProveedor.EstadoPedidoProveedor.RECIBIDO && estadoAnterior != PedidoProveedor.EstadoPedidoProveedor.RECIBIDO) {
            for (DetallePedidoProveedor detalle : pedido.getDetalles()) {
                ajustarStockDetalle(detalle, 1);
            }
        }

        if (estado != PedidoProveedor.EstadoPedidoProveedor.RECIBIDO && estadoAnterior == PedidoProveedor.EstadoPedidoProveedor.RECIBIDO) {
            for (DetallePedidoProveedor detalle : pedido.getDetalles()) {
                ajustarStockDetalle(detalle, -1);
            }
        }

        return pedidoProveedorRepository.save(pedido);
    }

    // Suma (signo +1) o resta (signo -1) stock para un detalle de pedido a
    // proveedor, respetando el talle/variante puntual cuando corresponde.
    private void ajustarStockDetalle(DetallePedidoProveedor detalle, int signo) {
        if (detalle.getProducto() == null) return;
        Long productoId = detalle.getProducto().getId();

        List<VarianteProducto> variantes = varianteRepository.findByProductoId(productoId);

        if (variantes.isEmpty()) {
            productoService.actualizarStock(productoId, signo * detalle.getCantidad());
        } else if (detalle.getTalle() != null) {
            variantes.stream()
                    .filter(v -> v.getTalle().equalsIgnoreCase(detalle.getTalle()))
                    .findFirst()
                    .ifPresent(v -> {
                        v.setStock(v.getStock() + signo * detalle.getCantidad());
                        varianteRepository.save(v);
                    });
        }
    }

    public void eliminar(Long id) {
        pedidoProveedorRepository.deleteById(id);
    }
}
