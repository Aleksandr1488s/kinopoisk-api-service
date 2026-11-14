package com.example.demo.service;

import com.example.demo.entity.Film;
import com.example.demo.repository.FilmRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ReportService {

    private final JavaMailSender mailSender;
    private final FilmRepository filmRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public ReportService(JavaMailSender mailSender, FilmRepository filmRepository) {
        this.mailSender = mailSender;
        this.filmRepository = filmRepository;
    }

    public void sendCsvReport(String toEmail) throws MessagingException {
        List<Film> films = filmRepository.findAll();

        StringBuilder csv = new StringBuilder("ID,Name,Year,Rating,Description\n");

        for (Film f : films) {
            String desc = f.getDescription() != null ? f.getDescription().replace(",", " ") : "";
            csv.append(f.getFilmId()).append(",")
                    .append(f.getFilmName()).append(",")
                    .append(f.getYear()).append(",")
                    .append(f.getRating()).append(",")
                    .append(desc).append("\n");
        }
        sendEmail(toEmail, "Отчёт по фильмам (CSV)", "films.csv", csv.toString().getBytes(StandardCharsets.UTF_8));
    }


    public void sendXmlReport(String toEmail) throws MessagingException {
        List<Film> films = filmRepository.findAll();
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<films>\n");
        for (Film f : films) {
            xml.append("  <film>\n");
            xml.append("    <id>").append(f.getFilmId()).append("</id>\n");
            xml.append("    <name>").append(f.getFilmName()).append("</name>\n");
            xml.append("    <year>").append(f.getYear()).append("</year>\n");
            xml.append("    <rating>").append(f.getRating()).append("</rating>\n");
            xml.append("    <description>").append(f.getDescription() != null ? f.getDescription() : "").append("</description>\n");
            xml.append("  </film>\n");
        }
        xml.append("</films>");

        sendEmail(toEmail, "Отчёт по фильмам (XML)", "films.xml", xml.toString().getBytes(StandardCharsets.UTF_8));
    }

    // Вспомогательный метод для отправки
    private void sendEmail(String toEmail, String subject, String filename, byte[] attachment) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail); // Используем переменную из properties
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText("Во вложении файл с фильмами.");
        helper.addAttachment(filename, new ByteArrayResource(attachment));

        mailSender.send(message);
    }
}

