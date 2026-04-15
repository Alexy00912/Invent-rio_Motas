package com.starterkit.springboot.mota;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/motas")
class MotaController {

    private final MotaRepository repo;

    public MotaController(MotaRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mota create(@RequestBody Mota t) {
        if (t.getDone() == null) t.setDone(false);
        return repo.save(t);
    }

    @GetMapping
    public List<Mota> list() {
        return repo.findAll();
    }

    @PostMapping("/teste")
    public List<Mota> seed(
            @RequestParam(defaultValue = "5") int count,
            @RequestParam(defaultValue = "false") boolean clear
    ) {
        if (count < 1 || count > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "count deve estar entre 1 e 100");
        }
        if (clear) {
            repo.deleteAll();
        }
        List<Mota> created = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Mota t = new Mota();
            t.setMarca("Marca: " + i);
            t.setModelo("Modelo: " + i);
            t.setKilometragem("Kilometragem: " + i);
            t.setCilindrada("Cilindrada: " + i);
            t.setAno("Ano: " + i);
            t.setCor("Cor: " + i);
            t.setGarantia(false);
            t.setSeguro(false);
            t.setDescription("Seed " + i);
            t.setCost("0");
            t.setDone(false);
            repo.save(t);
            created.add(t);
        }
        return created;
    }

    @GetMapping("/{id}")
    public Mota get(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mota nao encontrado"));
    }

    @PutMapping("/{id}")
    public Mota update(@PathVariable Long id, @RequestBody Mota tUpdate) {
        Mota t = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mota nao encontrado"));

        t.setMarca(tUpdate.getMarca());
        t.setModelo(tUpdate.getModelo());
        t.setKilometragem(tUpdate.getKilometragem());
        t.setCilindrada(tUpdate.getCilindrada ());
        t.setCor(tUpdate.getCor());
        t.setAno(tUpdate.getAno());
        t.setDescription(tUpdate.getDescription());
        if(tUpdate.getGarantia() != null) t.setGarantia(tUpdate.getGarantia());
        if (tUpdate.getSeguro() != null) t.setSeguro(tUpdate.getSeguro());
        if (tUpdate.getDone() != null) t.setDone(tUpdate.getDone());
        if (tUpdate.getCost() != null) t.setCost(tUpdate.getCost());

        return repo.save(t);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mota nao encontrado");
        }
        repo.deleteById(id);
    }
}
