package com.example.demo.service;

import com.example.demo.dto.KinopoiskResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class KinopoiskProducerService {
    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "kinopoisk-films";

    public KinopoiskProducerService(RestTemplate restTemplate, KafkaTemplate<String, Object> kafkaTemplate) {
        this.restTemplate = restTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    // Запускается каждые 60 секунд (для теста). В реальности можно поставить cron = "0 0 2 * * *"
    @Scheduled(fixedRate = 60000)
    public void fetchAndSendToKafka() {
        String url = UriComponentsBuilder
                .fromUriString("https://kinopoiskapiunofficial.tech/api/v2.2/films")
                .queryParam("page", "3")
                .queryParam("order", "RATING")
                .toUriString();

        System.out.println("Продюсер: Запрашиваю данные у Кинопоиска...");
        KinopoiskResponse response = restTemplate.getForObject(url, KinopoiskResponse.class);

        if (response != null && response.getItems() != null) {
            // Отправляем весь список фильмов в Kafka
            kafkaTemplate.send(TOPIC, response.getItems());
            System.out.println("Продюсер: Отправил " + response.getItems().size() + " фильмов в Kafka");
        }
    }
}
