package com.estiloanimal.stocksistema.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVenta estado;

    @Column(name = "medio_pago", length = 50)
    private String medioPago;

    @Column(length = 255)
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles;

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoVenta.COMPLETADA;
        }
    }

    public enum EstadoVenta {
        PENDIENTE, EN_PREPARACION, ENTREGADO, COMPLETADA, CANCELADA
    }

    @Column(length = 100)
    private String nombreCliente;

    @Column(length = 100)
    private String apellidoCliente;

    @Column(length = 50)
    private String telefono;

    @Column(length = 255)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 20)
    private String tipoEntrega; // "retirar" o "envio"

    @Column(length = 255)
    private String nota;
}
