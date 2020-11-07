package com.lorenzovigo.api.endpoints;

import com.lorenzovigo.api.database.CocheRepository;
import com.lorenzovigo.api.database.LocalRepository;
import com.lorenzovigo.api.exceptions.*;
import com.lorenzovigo.api.model.Coche;
import com.lorenzovigo.api.model.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;

@RestController
public class CocheController {
    private final CocheRepository repository;
    private final LocalRepository localRepository;
    String dateFormat = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

    CocheController(CocheRepository repository, LocalRepository localRepository) {
        this.repository = repository;
        this.localRepository = localRepository;
    }

    @GetMapping("/coches")
    List<Coche> all(@RequestParam String sort) {
        List<Coche> list = repository.findAll();
        return sortList(list, sort);
    }

    @GetMapping("/concesionarios/{id}/coches")
    List<Coche> allByLocal(@PathVariable Long id, @RequestParam String sort) {
        Local local = localRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("concesionario", "id", id.toString()));
        List<Coche> list = repository.findAll().stream().filter(car -> car.getLocalId() == id).collect(Collectors.toList());
        return sortList(list, sort);
    }

    @GetMapping("/coches/{id}")
    Coche one(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("coche", "id", id.toString()));
    }

    @DeleteMapping("/coches/{id}/force")
    ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new NotFoundException("coche", "id", id.toString());
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/coches/{id}/vender")
    Coche sell(@PathVariable Long id, @RequestParam double precio){
        Coche car = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("coche", "id", id.toString()));
        if (car.isVendido() || !car.isDisponible()) {
            throw new InvalidActionException(" con un coche ya vendido o no disponible.");
        } else if (precio < 0) {
            throw new InvalidValueException("precio", valueOf(precio));
        } else {
            car.setVendido(true);
            car.setPrecio(precio);
            LocalDateTime date = LocalDateTime.now();
            car.setFechaVenta(date.format(formatter));
        }
        return car;
    }

    @PutMapping("/coches/{id}/matricular")
    Coche setMatricula(@PathVariable Long id, @RequestParam String matricula){
        Coche car = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("coche", "id", id.toString()));
        if (car.isVendido()) {
            throw new InvalidActionException(" con un coche ya vendido.");
        } else if (!car.isDisponible()) {
            throw new InvalidActionException(" con un coche no disponible.");
        } else {
            try {
                car.setMatricula(matricula);
            } catch (Exception e) {
                throw new NonUniqueValueException("matricula", matricula);
            }
        }
        return car;
    }

    @DeleteMapping("/coches/{id}")
    Coche setUnavailable(@PathVariable Long id){
        Coche car = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("coche", "id", id.toString()));
        if (car.isVendido()) {
            throw new InvalidActionException(" con un coche ya vendido.");
        } else if (!car.isDisponible()) {
            throw new InvalidActionException(" con un coche no disponible.");
        } else {
            car.setDisponible(false);
        }
        return car;
    }

    @PostMapping("/coches")
    @ResponseStatus( HttpStatus.CREATED )
    Coche newCoche(@RequestBody Coche newCoche) {

        Local local = localRepository.findById(newCoche.getLocalId())
                .orElseThrow(() -> new NotFoundException("concesionario", "id", String.valueOf(newCoche.getLocalId())));

        try{
            checkDateFormat(newCoche.getFechaIngreso());
        } catch (ParseException e) {
            throw new InvalidDateFormatException(newCoche.getFechaIngreso());
        }

        try{
            checkDateFormat(newCoche.getFechaVenta());
        } catch (ParseException e) {
            throw new InvalidDateFormatException(newCoche.getFechaVenta());
        }

        if (newCoche.getPrecio() < 0) throw new InvalidValueException("precio", valueOf(newCoche.getPrecio()));
        if (newCoche.getCoste() < 0) throw new InvalidValueException("coste", valueOf(newCoche.getCoste()));

        if (newCoche.getFechaIngreso() == null) {
            LocalDateTime date = LocalDateTime.now();
            newCoche.setFechaIngreso(date.format(formatter));
        }

        if (newCoche.isVendido() && newCoche.getFechaVenta() == null) {
            throw new InvalidActionException("ya que no podemos añadir un coche vendido sin su fecha de venta.");
        }
        if (!newCoche.isVendido() && newCoche.getFechaVenta() != null) {
            throw new InvalidActionException("ya que no podemos añadir fecha de venta a un coche no vendido.");

        }

        return repository.save(newCoche);
    }

    @GetMapping("/beneficios")
    @ResponseStatus( HttpStatus.CREATED )
    private String getReport() {
        String report = "CONCESIONARIO \t GASTOS \t INGRESOS \t TOTAL \t DIRECCIÓN \n";
        List<Local> locals = localRepository.findAll();
        double gastos_total = 0; double ingresos_total = 0; double total_total;

        for (Local l: locals) {
            List<Coche> coches = allByLocal(l.getId(), "");

            double gastos = 0; double ingresos = 0; double total;
            for (Coche c: coches) {
                gastos -= c.getCoste();
                if (c.isVendido()) ingresos += c.getPrecio();
            }
            total = ingresos + gastos;

            report += l.getId() + " \t\t " + gastos + " \t " + ingresos + " \t " + total + " \t " + l.getDireccion() + "\n";

            gastos_total += gastos;
            ingresos_total += ingresos;
        }

        total_total = gastos_total + ingresos_total;
        report += "TOTAL" + " \t\t " + gastos_total + " \t " + ingresos_total + " \t " + total_total + "\n";
        return report;
    }

    private List<Coche> sortList(List<Coche> list, String sort) throws InvalidSortValueException {
        if (sort.equals("IA")) {
            return sortByFechaIngresoAscendent(list);
        } else if (sort.equals("ID")) {
            return sortByFechaIngresoDescendent(list);
        } else if (sort.equals("VA")) {
            return sortByFechaVentaAscendent(list);
        } else if (sort.equals("VD")) {
            return sortByFechaVentaDescendent(list);
        } else if (sort.isBlank()) {
            return list;
        } else {
            throw new InvalidSortValueException(sort);
        }
    }

    private List<Coche> sortByFechaIngresoAscendent(List<Coche> list){
        list.stream().sorted(Comparator.comparing(Coche::getFechaIngreso));
        return list;
    }

    private List<Coche> sortByFechaIngresoDescendent(List<Coche> list){
        List<Coche> sorted_list = sortByFechaIngresoAscendent(list);
        Collections.reverse(sorted_list);
        return list;
    }

    private List<Coche> sortByFechaVentaDescendent(List<Coche> list){
        list.stream().sorted(Comparator.comparing(Coche::getFechaVenta));
        return list;
    }

    private List<Coche> sortByFechaVentaAscendent(List<Coche> list){
        List<Coche> sorted_list = sortByFechaVentaDescendent(list);
        Collections.reverse(sorted_list);
        return list;
    }

    private void checkDateFormat(String date) throws ParseException {
        if (date != null) {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setLenient(false);
            formatter.parse(date);
        }
    }
}
