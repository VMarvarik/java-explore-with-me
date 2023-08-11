package ru.practicum.mainservice.controller.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.dto.category.NewCategoryDto;
import ru.practicum.mainservice.dto.compilation.CompilationDto;
import ru.practicum.mainservice.dto.compilation.CompilationUpdateDto;
import ru.practicum.mainservice.dto.compilation.NewCompilationDto;
import ru.practicum.mainservice.dto.event.EventDto;
import ru.practicum.mainservice.dto.event.EventUpdateRequestDto;
import ru.practicum.mainservice.dto.user.NewUserDto;
import ru.practicum.mainservice.dto.user.UserDto;
import ru.practicum.mainservice.model.enums.EventState;
import ru.practicum.mainservice.service.CategoryService;
import ru.practicum.mainservice.service.CompilationService;
import ru.practicum.mainservice.service.EventService;
import ru.practicum.mainservice.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final CompilationService compilationService;
    private final EventService eventService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody NewUserDto userDto) {
        log.info("Создание пользователя: {}", userDto);
        return userService.addUser(userDto);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers(
            @RequestParam(required = false) final List<Long> ids,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size
    ) {
        log.info("Вызов всех пользователей");
        return userService.getAllUsers(ids, from, size);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable final Long userId) {
        log.info("Удаление пользователя: {}", userId);
        userService.deleteUser(userId);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody final NewCategoryDto newCategoryDto) {
        log.info("Создание категории: {}", newCategoryDto);
        return categoryService.addCategory(newCategoryDto);
    }

    @PatchMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(
            @PathVariable
            final Long catId,
            @Valid @RequestBody
            final CategoryDto categoryDto
    ) {
        log.info("Обновление категории: {}, to: {}", catId, categoryDto);
        return categoryService.updateCategory(catId, categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable final Long catId) {
        log.info("Удаление категории: {}", catId);
        categoryService.deleteCategory(catId);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody final NewCompilationDto newCompilationDto) {
        log.info("Создание компиляции: {}", newCompilationDto);
        return compilationService.createCompilation(newCompilationDto);
    }

    @PatchMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(
            @PathVariable final Long compId,
            @Valid @RequestBody final CompilationUpdateDto updateCompilationDto
    ) {
        log.info("Обновление компиляции с: {}, на: {}", compId, updateCompilationDto);
        return compilationService.updateCompilation(compId, updateCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable final Long compId) {
        log.info("Удаление компиляции: {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto updateEvent(
            @PathVariable final Long eventId,
            @Valid @RequestBody final EventUpdateRequestDto requestDto
    ) {
        log.info("Обновление события с: {}, на: {}", eventId, requestDto);
        return eventService.updateEventByAdmin(eventId, requestDto);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> getAllEvents(
            @RequestParam(required = false) final List<Long> users,
            @RequestParam(required = false) final List<Long> categories,
            @RequestParam(required = false) final List<EventState> states,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final LocalDateTime rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size
    ) {
        log.info("Вызов всех событий");
        return eventService.getAllEventsByAdmin(users, categories, states, rangeStart, rangeEnd, from, size);
    }
}
