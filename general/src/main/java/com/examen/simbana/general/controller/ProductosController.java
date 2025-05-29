package com.examen.simbana.general.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.examen.simbana.general.model.EstadoProducto;
import com.examen.simbana.general.model.Productos;
import com.examen.simbana.general.service.ProductosService;

@RestController
@RequestMapping("/api/productos")
public class ProductosController {

    private final ProductosService service;

    public ProductosController(ProductosService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Productos> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<Productos>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<Productos> create(@RequestBody Productos producto) {
        return ResponseEntity.ok(service.create(producto));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Productos> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam EstadoProducto nuevoEstado) {
        return ResponseEntity.ok(service.cambiarEstado(id, nuevoEstado));
    }

    @PutMapping("/{id}/aumentar-stock")
    public ResponseEntity<Productos> aumentarStock(
            @PathVariable Integer id,
            @RequestParam Integer cantidad,
            @RequestParam BigDecimal precioCompra) {
        return ResponseEntity.ok(service.aumentarStock(id, cantidad, precioCompra));
    }

    @PutMapping("/{id}/disminuir-stock")
    public ResponseEntity<Productos> disminuirStock(
            @PathVariable Integer id,
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(service.disminuirStock(id, cantidad));
    }
} 