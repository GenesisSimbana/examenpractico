package com.examen.simbana.general.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.simbana.general.exception.CategoriaNotFoundException;
import com.examen.simbana.general.model.CategoriasProducto;
import com.examen.simbana.general.repository.CategoriasProductoRepository;

@Service
public class CategoriasProductoService {

    private final CategoriasProductoRepository repository;

    public CategoriasProductoService(CategoriasProductoRepository repository) {
        this.repository = repository;
    }

    // CREATE
    @Transactional
    public CategoriasProducto create(CategoriasProducto categoria) {
        if (categoria.getNombreCategoria() == null || categoria.getNombreCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        
        List<CategoriasProducto> categoriasExistentes = this.repository.findAll();
        for (CategoriasProducto cat : categoriasExistentes) {
            if (cat.getNombreCategoria().equalsIgnoreCase(categoria.getNombreCategoria())) {
                throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoria.getNombreCategoria());
            }
        }

        return this.repository.save(categoria);
    }

    // READ
    public CategoriasProducto findById(Integer id) {
        Optional<CategoriasProducto> categoriaOptional = this.repository.findById(id);
        if (categoriaOptional.isPresent()) {
            return categoriaOptional.get();
        } else {
            throw new CategoriaNotFoundException("La categoría con ID: " + id + " no existe");
        }
    }

    public List<CategoriasProducto> findAll() {
        List<CategoriasProducto> categorias = this.repository.findAll();
        if (categorias.isEmpty()) {
            throw new CategoriaNotFoundException("No existen categorías registradas");
        }
        return categorias;
    }

    // UPDATE
    @Transactional
    public CategoriasProducto update(Integer id, CategoriasProducto categoria) {
        CategoriasProducto categoriaExistente = this.findById(id);
        
        if (categoria.getNombreCategoria() != null && !categoria.getNombreCategoria().trim().isEmpty()) {
            List<CategoriasProducto> categoriasExistentes = this.repository.findAll();
            for (CategoriasProducto cat : categoriasExistentes) {
                if (!cat.getIdCategoria().equals(id) && 
                    cat.getNombreCategoria().equalsIgnoreCase(categoria.getNombreCategoria())) {
                    throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoria.getNombreCategoria());
                }
            }
            categoriaExistente.setNombreCategoria(categoria.getNombreCategoria());
        }
        
        if (categoria.getDescripcion() != null) {
            categoriaExistente.setDescripcion(categoria.getDescripcion());
        }

        return this.repository.save(categoriaExistente);
    }

    // DELETE
    @Transactional
    public void delete(Integer id) {
        CategoriasProducto categoria = this.findById(id);
        this.repository.delete(categoria);
    }
} 