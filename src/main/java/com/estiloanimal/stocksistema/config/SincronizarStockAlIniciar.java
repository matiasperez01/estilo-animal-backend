package com.estiloanimal.stocksistema.config;

import com.estiloanimal.stocksistema.model.Producto;
import com.estiloanimal.stocksistema.model.VarianteProducto;
import com.estiloanimal.stocksistema.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Al arrancar, recalcula el stock de cada producto con talles/variantes
// como la suma de sus variantes, por si quedó desincronizado de antes de
// que el stock de estos productos se empezara a calcular automáticamente.
// Es idempotente (no cambia nada si ya está correcto), así que no molesta
// que se ejecute en cada reinicio.
@Component
@RequiredArgsConstructor
@Slf4j
public class SincronizarStockAlIniciar implements CommandLineRunner {

    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public void run(String... args) {
        List<Producto> productos = productoRepository.findAll();
        int corregidos = 0;

        for (Producto producto : productos) {
            List<VarianteProducto> variantes = producto.getVariantes();
            if (variantes == null || variantes.isEmpty()) continue;

            int total = variantes.stream().mapToInt(VarianteProducto::getStock).sum();
            if (producto.getStock() == null || total != producto.getStock()) {
                producto.setStock(total);
                productoRepository.save(producto);
                corregidos++;
            }
        }

        if (corregidos > 0) {
            log.info("Stock recalculado para {} producto(s) con talles al iniciar", corregidos);
        }
    }
}
