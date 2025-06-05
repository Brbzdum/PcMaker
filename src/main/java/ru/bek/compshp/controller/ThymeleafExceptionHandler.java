package ru.bek.compshp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для контроллеров использующих Thymeleaf шаблоны
 */
@ControllerAdvice
public class ThymeleafExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ThymeleafExceptionHandler.class);

    /**
     * Обработка ошибок валидации
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleValidationExceptions(ConstraintViolationException ex) {
        logger.error("Ошибка валидации: {}", ex.getMessage());
        
        ModelAndView modelAndView = new ModelAndView();
        Map<String, String> errors = new HashMap<>();
        
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        
        modelAndView.addObject("validationErrors", errors);
        modelAndView.addObject("error", "Ошибка валидации данных");
        
        // Если ошибка в админ-панели, возвращаем соответствующее представление
        if (ex.getMessage().contains("/admin")) {
            modelAndView.setViewName("admin/error");
        } else {
            modelAndView.setViewName("error");
        }
        
        return modelAndView;
    }

    /**
     * Обработка общих исключений для админ-панели
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralExceptions(Exception ex, Model model) {
        logger.error("Ошибка: {}", ex.getMessage(), ex);
        
        model.addAttribute("error", "Произошла ошибка: " + ex.getMessage());
        model.addAttribute("stackTrace", ex.getStackTrace());
        
        // Если ошибка в админ-панели, возвращаем соответствующее представление
        if (ex.getMessage() != null && ex.getMessage().contains("/admin")) {
            return "admin/error";
        } else {
            return "error";
        }
    }
} 