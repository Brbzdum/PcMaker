package ru.bek.compshp.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Утилитный класс для работы с датами
 */
public class DateUtils {
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    
    /**
     * Форматирует дату в строку по умолчанию
     */
    public static String formatDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return DEFAULT_FORMATTER.format(date);
    }
    
    /**
     * Форматирует дату в ISO формат для JSON
     */
    public static String formatDateISO(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return ISO_FORMATTER.format(date);
    }
    
    /**
     * Парсит строку в дату
     */
    public static LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateStr, DEFAULT_FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(dateStr, ISO_FORMATTER);
            } catch (DateTimeParseException ex) {
                return null;
            }
        }
    }
} 