package com.examen.simbana.general.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.examen.simbana.general.model.CategoriasProducto;
import com.examen.simbana.general.service.CategoriasProductoService;

@RestController
@RequestMapping("/api/categorias")
public class CategoriasProductoController {

    private final CategoriasProductoService service;

    public CategoriasProductoController(CategoriasProductoService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriasProducto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<CategoriasProducto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<CategoriasProducto> create(@RequestBody CategoriasProducto categoria) {
        return ResponseEntity.ok(service.create(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriasProducto> update(@PathVariable Integer id, @RequestBody CategoriasProducto categoria) {
        return ResponseEntity.ok(service.update(id, categoria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
} 