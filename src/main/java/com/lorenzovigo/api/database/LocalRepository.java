package com.lorenzovigo.api.database;

import com.lorenzovigo.api.model.Local;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalRepository extends JpaRepository<Local, Long> {

}
