package com.lorenzovigo.api.endpoints;

import com.lorenzovigo.api.database.LocalRepository;
import com.lorenzovigo.api.model.Local;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LocalController {

    private final LocalRepository repository;

    LocalController(LocalRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/concesionarios")
    List<Local> all() {
        // TODO HTTP Status, database empty
        return repository.findAll();
    }

    @PostMapping("/concesionarios")
    Local newLocal(@RequestBody Local newLocal) {
        // TODO HTTP Status, address not unique, null address (Check why it doesn't work)
        return repository.save(newLocal);
    }

    @GetMapping("/concesionarios/{id}")
    Local one(@PathVariable Long id) {
        // TODO HTTP Status, not found
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Local con id " + id + "no encontrado"));
    }
}
