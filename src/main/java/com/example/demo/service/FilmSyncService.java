package com.example.demo.service;

import com.example.demo.dto.FilmDto;
import com.example.demo.dto.KinopoiskResponse;
import com.example.demo.entity.Film;
import com.example.demo.repository.FilmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class FilmSyncService {

    private static final Logger log = LoggerFactory.getLogger(FilmSyncService.class);
    private final RestTemplate restTemplate;
    private final FilmRepository filmRepository;

    public FilmSyncService(RestTemplate restTemplate, FilmRepository filmRepository) {
        this.restTemplate = restTemplate;
        this.filmRepository = filmRepository;
    }

    @Transactional
    public void syncFilms(Map<String, String> params) {
        String url = UriComponentsBuilder
                .fromUriString("https://kinopoiskapiunofficial.tech/api/v2.2/films")
                .queryParam("page", params.getOrDefault("page", "1"))
                .queryParam("order", params.getOrDefault("order", "RATING"))
                .queryParam("type", params.getOrDefault("type", "ALL"))
                .encode()
                .toUriString();

        KinopoiskResponse response = restTemplate.getForObject(url, KinopoiskResponse.class);

        if (response != null && response.getItems() != null) {
            for (FilmDto dto : response.getItems()) {
                log.info("Синхронизация: ID = {}, Name = {}", dto.getFilmId(), dto.getFilmName());
                    if (dto.getFilmName() == null || dto.getFilmName().isBlank()) {
                        log.warn("Пропущен фильм с ID {}, так как нет названия", dto.getFilmId());
                        continue;
                    }


                if (dto.getFilmId() != null && !filmRepository.existsByFilmId(dto.getFilmId())) {
                    Film film = new Film();
                    film.setFilmId(dto.getFilmId());
                    film.setFilmName(dto.getFilmName());
                    film.setYear(dto.getYear());
                    film.setRating(dto.getRating());
                    film.setDescription(dto.getDescription());
                    filmRepository.save(film);
                }
            }
        }
    }
}

