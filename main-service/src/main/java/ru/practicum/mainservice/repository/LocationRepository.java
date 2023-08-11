package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}