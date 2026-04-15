package com.starterkit.springboot.mota;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MotaRepository extends JpaRepository<Mota, Long> {
     Optional<Mota> findByCodigoUnico(String codigoUnico);
}
