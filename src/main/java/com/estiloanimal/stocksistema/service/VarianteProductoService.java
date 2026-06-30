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
        return varianteRepository.findByProductoId(productoId);
    }

    public VarianteProducto guardar(Long productoId, VarianteProducto variante) {
        Producto producto = productoService.buscarPorId(productoId);
        variante.setProducto(producto);
        return varianteRepository.save(variante);
    }

    public VarianteProducto actualizar(Long id, VarianteProducto varianteActualizada) {
        VarianteProducto variante = varianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variante no encontrada"));
        variante.setTalle(varianteActualizada.getTalle());
        variante.setPrecio(varianteActualizada.getPrecio());
        variante.setStock(varianteActualizada.getStock());
        return varianteRepository.save(variante);
    }

    public void eliminar(Long id) {
        varianteRepository.deleteById(id);
    }

    public void eliminarPorProducto(Long productoId) {
        varianteRepository.deleteByProductoId(productoId);
    }
}