package com.starterkit.springboot.mota;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MotaService {

    private final MotaRepository repo;
    private final Path motasUploadDir;

    public MotaService(MotaRepository repo,
                       @Value("${app.upload-dir:./uploads}") String uploadDir) {
        this.repo = repo;
        this.motasUploadDir = Paths.get(uploadDir)
                                   .toAbsolutePath()
                                   .normalize()
                                   .resolve("motas");
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(motasUploadDir);
        } catch (IOException ex) {
            throw new IllegalStateException("Nao foi possivel criar a pasta de uploads", ex);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void fillMissingCodigoUnico() {
        List<Mota> motas = repo.findAll();
        boolean changed = false;

        for (Mota mota : motas) {
            if (!StringUtils.hasText(mota.getCodigoUnico())) {
                mota.setCodigoUnico(UUID.randomUUID().toString());
                changed = true;
            }
        }

        if (changed) {
            repo.saveAll(motas);
        }
    }

    public List<Mota> listAll() {
        return repo.findAll();
    }

    public Mota getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Mota nao encontrada"));
    }

    public Mota getByCodigo(String codigo) {
        return repo.findByCodigoUnico(codigo)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Mota nao encontrada"));
    }

    public Mota create(MotaForm form) {
        Mota mota = new Mota();
        applyForm(mota, form);
        mota.setImagemPath(storeImage(form.getImagem(), null));
        return repo.save(mota);
    }

    public Mota update(Long id, MotaForm form) {
        Mota mota = getById(id);
        applyForm(mota, form);
        mota.setImagemPath(storeImage(form.getImagem(), mota.getImagemPath()));
        return repo.save(mota);
    }

    public void delete(Long id) {
        Mota mota = getById(id);
        deleteStoredImage(mota.getImagemPath());
        repo.delete(mota);
    }

    private void applyForm(Mota mota, MotaForm form) {
        mota.setMarca(form.getMarca());
        mota.setModelo(form.getModelo());
        mota.setKilometragem(form.getKilometragem());
        mota.setCilindrada(form.getCilindrada());
        mota.setAno(form.getAno());
        mota.setCor(form.getCor());
        mota.setGarantia(Boolean.TRUE.equals(form.getGarantia()));
        mota.setSeguro(Boolean.TRUE.equals(form.getSeguro()));
        mota.setDescription(form.getDescription());
        mota.setCost(form.getCost());
        mota.setDone(Boolean.TRUE.equals(form.getDone()));
    }

    private String storeImage(MultipartFile imagem, String currentImagePath) {
        if (imagem == null || imagem.isEmpty()) {
            return currentImagePath;
        }

        String originalName = StringUtils.cleanPath(imagem.getOriginalFilename());
        String extension = getExtension(originalName).toLowerCase(Locale.ROOT);

        if (!isAllowedImageExtension(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Formato de imagem nao suportado");
        }

        String generatedName = UUID.randomUUID().toString() + extension;
        Path destination = motasUploadDir.resolve(generatedName).normalize();

        if (!destination.startsWith(motasUploadDir)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nome de ficheiro invalido");
        }

        try (InputStream inputStream = imagem.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Falha ao guardar a imagem");
        }

        deleteStoredImage(currentImagePath);

        return "motas/" + generatedName;
    }

    private void deleteStoredImage(String imagePath) {
        if (!StringUtils.hasText(imagePath)) {
            return;
        }

        Path filePath = Paths.get("uploads")
                .resolve(imagePath)
                .toAbsolutePath()
                .normalize();

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Falha ao remover a imagem");
        }
    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index < 0) {
            return "";
        }
        return fileName.substring(index);
    }

    private boolean isAllowedImageExtension(String extension) {
        return ".png".equals(extension)
                || ".jpg".equals(extension)
                || ".jpeg".equals(extension)
                || ".webp".equals(extension)
                || ".gif".equals(extension);
    }
}