package com.example.demo.controller;

import com.example.demo.entity.Film;
import com.example.demo.repository.FilmRepository;
import com.example.demo.service.FilmSyncService;
import com.example.demo.service.ReportService;
import jakarta.mail.MessagingException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/films")
public class FilmController {

    private final FilmSyncService syncService;
    private final FilmRepository filmRepository;
    private final ReportService reportService;

    public FilmController(FilmSyncService syncService, FilmRepository filmRepository, ReportService reportService) {
        this.syncService = syncService;
        this.filmRepository = filmRepository;
        this.reportService = reportService;
    }

    @PostMapping
    public String sync(@RequestParam Map<String, String> params) {
        syncService.syncFilms(params);
        return "Синхронизация завершена";
    }

    @GetMapping
    public Page<Film> search(
            @RequestParam(required = false) String filmName,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Double ratingFrom,
            @RequestParam(required = false) Double ratingTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Film> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filmName != null && !filmName.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("filmName")), "%" + filmName.toLowerCase() + "%"));
            }
            if (year != null) {
                predicates.add(cb.equal(root.get("year"), year));
            }
            if (ratingFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), ratingFrom));
            }
            if (ratingTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("rating"), ratingTo));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return filmRepository.findAll(spec, pageable);
    }

    @PostMapping("/report")
    public String sendReport(@RequestParam String email) throws MessagingException {
        reportService.sendCsvReport(email);
        return "Отчёт (CSV) отправлен на " + email;
    }
    @PostMapping("/report/xml")
    public String sendXmlReport(@RequestParam String email) throws MessagingException {
        reportService.sendXmlReport(email);
        return "Отчёт (XML) отправлен на " + email;
    }
}
