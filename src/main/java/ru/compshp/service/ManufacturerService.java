package ru.compshp.service;

import org.springframework.stereotype.Service;
import ru.compshp.model.Manufacturer;
import ru.compshp.repository.ManufacturerRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ManufacturerService {
    private final ManufacturerRepository manufacturerRepository;

    public ManufacturerService(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    public List<Manufacturer> getAll() {
        return manufacturerRepository.findAll();
    }

    public Optional<Manufacturer> getById(Long id) {
        return manufacturerRepository.findById(id);
    }

    public Optional<Manufacturer> getByName(String name) {
        return manufacturerRepository.findByName(name);
    }

    public Manufacturer save(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }

    public void delete(Long id) {
        manufacturerRepository.deleteById(id);
    }

    // TODO: Добавить методы для получения статистики, рейтинга, популярных товаров и т.д.
} 