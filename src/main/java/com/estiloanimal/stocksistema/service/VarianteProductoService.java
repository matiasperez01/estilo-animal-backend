package com.estiloanimal.stocksistema.service;

import com.estiloanimal.stocksistema.model.Producto;
import com.estiloanimal.stocksistema.model.VarianteProducto;
import com.estiloanimal.stocksistema.repository.VarianteProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VarianteProductoService {

    private final VarianteProductoRepository varianteRepository;
    private final ProductoService productoService;

    public List<VarianteProducto> listarPorProducto(Long productoId) {
        List<VarianteProducto> variantes = varianteRepository.findByProductoId(productoId);
        variantes.sort((a, b) -> VarianteProducto.compararTalles(a.getTalle(), b.getTalle()));
        return variantes;
    }

    public VarianteProducto guardar(Long productoId, VarianteProducto variante) {
        Producto producto = productoService.buscarPorId(productoId);
        variante.setProducto(producto);
        VarianteProducto guardada = varianteRepository.save(variante);
        productoService.recalcularStockDesdeVariantes(productoId, varianteRepository.findByProductoId(productoId));
        return guardada;
    }

    public VarianteProducto actualizar(Long id, VarianteProducto varianteActualizada) {
        VarianteProducto variante = varianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variante no encontrada"));
        variante.setTalle(varianteActualizada.getTalle());
        variante.setPrecio(varianteActualizada.getPrecio());
        variante.setStock(varianteActualizada.getStock());
        VarianteProducto actualizada = varianteRepository.save(variante);
        Long productoId = variante.getProducto().getId();
        productoService.recalcularStockDesdeVariantes(productoId, varianteRepository.findByProductoId(productoId));
        return actualizada;
    }

    public void eliminar(Long id) {
        VarianteProducto variante = varianteRepository.findById(id).orElse(null);
        varianteRepository.deleteById(id);
        if (variante != null && variante.getProducto() != null) {
            Long productoId = variante.getProducto().getId();
            productoService.recalcularStockDesdeVariantes(productoId, varianteRepository.findByProductoId(productoId));
        }
    }

    public void eliminarPorProducto(Long productoId) {
        varianteRepository.deleteByProductoId(productoId);
    }
}