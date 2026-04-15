package com.starterkit.springboot.mota;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/motas")
public class MotaPageController {

    private final MotaService motaService;

    @Value("${security.api-key.admin:}")
    private String adminKey;

    public MotaPageController(MotaService motaService) {
        this.motaService = motaService;
    }

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("titulo", "Gestao de Motas");
        model.addAttribute("motas", motaService.listAll());
        model.addAttribute("pageScript", "/js/motas.js");
        return "motas/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("titulo", "Novo mota");
        model.addAttribute("motas", null);
        model.addAttribute("motaForm", new MotaForm());
        model.addAttribute("modo", "novo");
        model.addAttribute("pageScript", "/js/motas.js");
        return "motas/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Mota mota = motaService.getById(id);
        MotaForm form = new MotaForm();
        copyMotaToForm(mota, form);
        model.addAttribute("titulo", "Editar mota");
        model.addAttribute("mota", mota);
        model.addAttribute("motaForm", form);
        model.addAttribute("modo", "editar");
        model.addAttribute("pageScript", "/js/motas.js");
        return "motas/form";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        Mota mota = motaService.getById(id);
        model.addAttribute("titulo", "Ficha do Mota");
        model.addAttribute("mota", mota);
        model.addAttribute("pageScript", "/js/motas.js");
        return "motas/detalhe";
    }

    @GetMapping("/codigo/{codigo}")
    public String detalhePorCodigo(@PathVariable String codigo, Model model) {
        Mota mota = motaService.getByCodigo(codigo);
        model.addAttribute("titulo", "Ficha do Mota");
        model.addAttribute("mota", mota);
        model.addAttribute("pageScript", "/js/motas.js");
        return "motas/detalhe";
    }

    @GetMapping("/scan")
    public String scan(Model model) {
        model.addAttribute("titulo", "Ler QR Code");
        model.addAttribute("pageScript", "/js/motas.js");
        return "motas/scan";
    }

    @PostMapping
    public String criar(@Valid MotaForm motaForm, BindingResult bindingResult, Model model) {
        if (!isValidAdminKey(motaForm.getAdminApiKey())) {
            bindingResult.rejectValue("adminApiKey", "adminApiKey.invalid", "Chave de administrador invalida.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("titulo", "Novo Mota");
            model.addAttribute("mota", null);
            model.addAttribute("modo", "novo");
            model.addAttribute("pageScript", "/js/motas.js");
            return "motas/form";
        }

        motaService.create(motaForm);
        return "redirect:/motas?status=created";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid MotaForm motaForm, BindingResult bindingResult,
            Model model) {
        Mota mota = motaService.getById(id);
        if (!isValidAdminKey(motaForm.getAdminApiKey())) {
            bindingResult.rejectValue("adminApiKey", "adminApiKey.invalid", "Chave de administrador invalida.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("titulo", "Editar Mota");
            model.addAttribute("modo", "editar");
            model.addAttribute("mota", mota);
            model.addAttribute("pageScript", "/js/motas.js");
            return "motas/form";
        }

        motaService.update(id, motaForm);
        return "redirect:/motas?status=updated";
    }

    private boolean isValidAdminKey(String providedKey) {
        return providedKey != null && !providedKey.trim().isEmpty() && providedKey.equals(adminKey);
    }

    private void copyMotaToForm(Mota mota, MotaForm form) {
        form.setMarca(mota.getMarca());
        form.setModelo(mota.getModelo());
        form.setKilometragem(mota.getKilometragem());
        form.setCilindrada(mota.getCilindrada());
        form.setAno(mota.getAno());
        form.setCor(mota.getCor());
        form.setGarantia(mota.getGarantia());
        form.setDescription(mota.getDescription());
        form.setCost(mota.getCost());
        form.setDone(mota.getDone());
    }
}
