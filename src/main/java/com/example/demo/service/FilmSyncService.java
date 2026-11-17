package com.example.demo.service;

import com.example.demo.dto.FilmDto;
import com.example.demo.dto.KinopoiskResponse;
import com.example.demo.entity.Film;
import com.example.demo.repository.FilmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
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

    @KafkaListener(topics = "kinopoisk-films", groupId = "kinopoisk-group", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void consumeFilms(List<FilmDto> films) {
        log.info("Консьюмер: Получено {} фильмов из Kafka", films.size());

        for (FilmDto dto : films) {
            if (dto.getFilmName() == null || dto.getFilmName().isBlank()) {
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
                log.info("Сохранен фильм: {}", film.getFilmName());
            }
        }
    }
}

