package com.example.services;

import com.example.data.TODOItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Pavel Shatrov
 */
public interface TODORepository extends JpaRepository<TODOItem, Long> {
    
}
