package com.example.demo.repository;

import com.example.demo.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FilmRepository extends JpaRepository<Film,Long>, JpaSpecificationExecutor<Film> {
    boolean existsByFilmId(Long filmId);
}
