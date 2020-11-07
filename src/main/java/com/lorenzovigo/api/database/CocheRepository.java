package com.lorenzovigo.api.database;

import com.lorenzovigo.api.model.Coche;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CocheRepository extends JpaRepository<Coche, Long> {

}
