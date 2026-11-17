package com.example.demo.controller;

import com.example.demo.entity.Film;
import com.example.demo.repository.FilmRepository;

import com.example.demo.service.ReportService;
import com.example.demo.specification.FilmSpecification;
import jakarta.mail.MessagingException;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v2/films")
public class FilmController {


    private final FilmRepository filmRepository;
    private final ReportService reportService;

    public FilmController(FilmRepository filmRepository, ReportService reportService) {
        this.filmRepository = filmRepository;
        this.reportService = reportService;
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

        Specification<Film> spec = FilmSpecification.withFilters(filmName, year, ratingFrom, ratingTo);

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
