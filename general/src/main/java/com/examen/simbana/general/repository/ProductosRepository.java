package com.examen.simbana.general.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.examen.simbana.general.model.Productos;

public interface ProductosRepository extends JpaRepository<Productos, Integer> {
    
} 