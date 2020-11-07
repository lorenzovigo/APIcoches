package com.lorenzovigo.api.endpoints;

import com.lorenzovigo.api.database.LocalRepository;
import com.lorenzovigo.api.exceptions.NonUniqueValueException;
import com.lorenzovigo.api.exceptions.NotFoundException;
import com.lorenzovigo.api.exceptions.NullValueException;
import com.lorenzovigo.api.model.Local;
import org.springframework.http.HttpStatus;
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
        return repository.findAll();
    }

    @PostMapping("/concesionarios")
    @ResponseStatus( HttpStatus.CREATED )
    Local newLocal(@RequestBody Local newLocal) {
        try {
            return repository.save(newLocal);
        } catch (Exception e) {
            if (newLocal.getDireccion() == null) { throw new NullValueException("direccion"); }
            throw new NonUniqueValueException("direccion", newLocal.getDireccion());
        }
    }

    @GetMapping("/concesionarios/{id}")
    Local one(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("concesionario", "id", id.toString()));
    }
}
