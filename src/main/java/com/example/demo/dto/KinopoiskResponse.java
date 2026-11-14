package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KinopoiskResponse {
    private List<FilmDto> items;
    public List<FilmDto> getItems() {return items;}
    public void setItems(List<FilmDto> items) { this.items = items; }
}
