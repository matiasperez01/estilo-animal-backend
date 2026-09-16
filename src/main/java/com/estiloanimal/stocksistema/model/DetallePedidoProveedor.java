package com.estiloanimal.stocksistema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_pedidos_proveedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedidoProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(length = 150)
    private String nombreProducto;

    @Column(length = 50)
    private String talle;

    @JsonIgnoreProperties("detalles")
    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoProveedor pedido;

    @JsonIgnoreProperties({"detallesVenta", "variantes"})
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = true)
    private Producto producto;

    // Alternativa a mandar "producto": {"id": N} desde el frontend: un id
    // plano que el service resuelve con getReferenceById en vez de depender
    // de que Jackson arme el objeto Producto anidado.
    @Transient
    private Long productoId;

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (this.cantidad != null && this.precioUnitario != null) {
            this.subtotal = this.precioUnitario.multiply(BigDecimal.valueOf(this.cantidad));
        }
    }
}
