package com.estiloanimal.stocksistema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "variantes_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VarianteProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String talle;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    @JsonIgnoreProperties("variantes")
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // Orden natural para mostrar las variantes: si ambos talles son numéricos
    // (talles de indumentaria: 1, 2, 3...) se comparan como números para que
    // 2 < 10; si no, se comparan alfabéticamente (Colores, Sabores, etc.).
    public static int compararTalles(String a, String b) {
        Double na = parseNumero(a);
        Double nb = parseNumero(b);
        if (na != null && nb != null) return Double.compare(na, nb);
        if (a == null) return b == null ? 0 : -1;
        if (b == null) return 1;
        return a.compareToIgnoreCase(b);
    }

    private static Double parseNumero(String s) {
        if (s == null) return null;
        try {
            return Double.parseDouble(s.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}