package com.examen.simbana.general.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.simbana.general.exception.CategoriaNotFoundException;
import com.examen.simbana.general.exception.ProductoNotFoundException;
import com.examen.simbana.general.model.EstadoProducto;
import com.examen.simbana.general.model.Productos;
import com.examen.simbana.general.repository.ProductosRepository;

@Service
public class ProductosService {

    private final ProductosRepository repository;
    private final CategoriasProductoService categoriaService;
    private static final BigDecimal MARGEN_VENTA = new BigDecimal("1.25"); // 25% de margen

    public ProductosService(ProductosRepository repository, CategoriasProductoService categoriaService) {
        this.repository = repository;
        this.categoriaService = categoriaService;
    }

    public Productos findById(Integer id) {
        Optional<Productos> productoOptional = this.repository.findById(id);
        if (productoOptional.isPresent()) {
            return productoOptional.get();
        } else {
            throw new ProductoNotFoundException("El producto con ID: " + id + " no existe");
        }
    }

    public List<Productos> findAll() {
        List<Productos> productos = this.repository.findAll();
        if (productos.isEmpty()) {
            throw new ProductoNotFoundException("No existen productos registrados");
        }
        return productos;
    }

    @Transactional
    public Productos create(Productos producto) {
        // Validar campos obligatorios
        if (producto.getNombreProducto() == null || producto.getNombreProducto().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        if (producto.getPrecioVenta() == null || producto.getPrecioVenta().doubleValue() <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor a 0");
        }
        if (producto.getStockActual() == null || producto.getStockActual() < 0) {
            throw new IllegalArgumentException("El stock actual no puede ser negativo");
        }
        if (producto.getEstadoProducto() == null) {
            throw new IllegalArgumentException("El estado del producto no puede estar vacío");
        }

        // Validar que la categoría exista
        if (producto.getIdCategoria() != null) {
            try {
                categoriaService.findById(producto.getIdCategoria());
            } catch (CategoriaNotFoundException e) {
                throw new IllegalArgumentException("La categoría especificada no existe");
            }
        }

        // Validar que el costo de compra y precio de venta cumplan con el margen del 25%
        if (producto.getCostoCompra() != null) {
            BigDecimal precioVentaCalculado = producto.getCostoCompra().multiply(MARGEN_VENTA).setScale(2, RoundingMode.HALF_UP);
            if (producto.getPrecioVenta().compareTo(precioVentaCalculado) != 0) {
                throw new IllegalArgumentException(
                    String.format("El precio de venta debe ser exactamente el 25%% más que el costo de compra. " +
                                "Costo de compra: %s, Precio de venta esperado: %s, Precio de venta proporcionado: %s",
                                producto.getCostoCompra(), precioVentaCalculado, producto.getPrecioVenta()));
            }
        }

        return this.repository.save(producto);
    }

    @Transactional
    public Productos update(Integer id, Productos producto) {
        Productos productoExistente = this.findById(id);

        if (producto.getNombreProducto() != null && !producto.getNombreProducto().trim().isEmpty()) {
            productoExistente.setNombreProducto(producto.getNombreProducto());
        }
        if (producto.getDescripcion() != null) {
            productoExistente.setDescripcion(producto.getDescripcion());
        }
        if (producto.getPrecioVenta() != null && producto.getPrecioVenta().doubleValue() > 0) {
            productoExistente.setPrecioVenta(producto.getPrecioVenta());
        }
        if (producto.getCostoCompra() != null) {
            BigDecimal precioVentaCalculado = producto.getCostoCompra().multiply(MARGEN_VENTA).setScale(2, RoundingMode.HALF_UP);
            if (productoExistente.getPrecioVenta().compareTo(precioVentaCalculado) != 0) {
                throw new IllegalArgumentException(
                    String.format("El precio de venta debe ser exactamente el 25%% más que el costo de compra. " +
                                "Costo de compra: %s, Precio de venta esperado: %s, Precio de venta actual: %s",
                                producto.getCostoCompra(), precioVentaCalculado, productoExistente.getPrecioVenta()));
            }
            productoExistente.setCostoCompra(producto.getCostoCompra());
        }
        if (producto.getStockActual() != null && producto.getStockActual() >= 0) {
            productoExistente.setStockActual(producto.getStockActual());
        }
        if (producto.getEstadoProducto() != null) {
            productoExistente.setEstadoProducto(producto.getEstadoProducto());
        }
        if (producto.getIdCategoria() != null) {
            try {
                categoriaService.findById(producto.getIdCategoria());
                productoExistente.setIdCategoria(producto.getIdCategoria());
            } catch (CategoriaNotFoundException e) {
                throw new IllegalArgumentException("La categoría especificada no existe");
            }
        }

        return this.repository.save(productoExistente);
    }

    @Transactional
    public void delete(Integer id) {
        Productos producto = this.findById(id);
        this.repository.delete(producto);
    }

    @Transactional
    public Productos cambiarEstado(Integer id, EstadoProducto nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede estar vacío");
        }

        Productos producto = this.findById(id);
        producto.setEstadoProducto(nuevoEstado);
        return this.repository.save(producto);
    }

    @Transactional
    public Productos aumentarStock(Integer id, Integer cantidad, BigDecimal precioCompra) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a incrementar debe ser mayor a 0");
        }
        if (precioCompra == null || precioCompra.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de compra debe ser mayor a 0");
        }

        Productos producto = this.findById(id);
        
        // Calcular nuevo precio de venta (precio de compra + 25%)
        BigDecimal nuevoPrecioVenta = precioCompra.multiply(MARGEN_VENTA).setScale(2, RoundingMode.HALF_UP);
        
        // Actualizar producto
        producto.setStockActual(producto.getStockActual() + cantidad);
        producto.setCostoCompra(precioCompra);
        producto.setPrecioVenta(nuevoPrecioVenta);
        producto.setEstadoProducto(EstadoProducto.ACTIVO);
        
        return this.repository.save(producto);
    }

    @Transactional
    public Productos disminuirStock(Integer id, Integer cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a disminuir debe ser mayor a 0");
        }

        Productos producto = this.findById(id);
        
        if (producto.getStockActual() < cantidad) {
            throw new IllegalArgumentException("No hay suficiente stock disponible. Stock actual: " + producto.getStockActual());
        }
        
        producto.setStockActual(producto.getStockActual() - cantidad);
        
        // Si el stock llega a 0, cambiar estado a AGOTADO
        if (producto.getStockActual() == 0) {
            producto.setEstadoProducto(EstadoProducto.AGOTADO);
        }
        
        return this.repository.save(producto);
    }
} 