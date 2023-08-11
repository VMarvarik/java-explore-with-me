package ru.practicum.mainservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.dto.category.NewCategoryDto;
import ru.practicum.mainservice.dto.compilation.CompilationDto;
import ru.practicum.mainservice.dto.compilation.NewCompilationDto;
import ru.practicum.mainservice.dto.compilation.UpdateCompilationDto;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.UpdateEventAdminRequestDto;
import ru.practicum.mainservice.dto.user.NewUserRequestDto;
import ru.practicum.mainservice.dto.user.UserDto;
import ru.practicum.mainservice.enums.EventState;
import ru.practicum.mainservice.service.CategoryService;
import ru.practicum.mainservice.service.CompilationService;
import ru.practicum.mainservice.service.EventService;
import ru.practicum.mainservice.service.UserService;
import ru.practicum.mainservice.util.PageParams;

import javax.validation.Valid;
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
    public UserDto createUser(@Valid @RequestBody NewUserRequestDto newUserRequestDto) {
        log.info("Creating user: {}", newUserRequestDto);
        return userService.createUser(newUserRequestDto);
    }

    @GetMapping("/users")
    public List<UserDto> getAllUsers(
            @RequestParam(required = false)
            final List<Long> ids,
            PageParams pageParams
    ) {
        log.info("Getting all users with ids in: {}, page params: {}", ids, pageParams);
        return userService.getAllUsers(ids, pageParams);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable final Long userId) {
        log.info("Deleting user: {}", userId);
        userService.deleteUser(userId);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody final NewCategoryDto newCategoryDto) {
        log.info("Creating category: {}", newCategoryDto);
        return categoryService.createCategory(newCategoryDto);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(
            @PathVariable
            final Long catId,
            @Valid @RequestBody
            final CategoryDto categoryDto
    ) {
        log.info("Updating category id: {}, to: {}", catId, categoryDto);
        return categoryService.updateCategory(catId, categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable final Long catId) {
        log.info("Deleting category with id: {}", catId);
        categoryService.deleteCategory(catId);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody final NewCompilationDto newCompilationDto) {
        log.info("Creating new compilation: {}", newCompilationDto);
        return compilationService.createCompilation(newCompilationDto);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(
            @PathVariable
            final Long compId,
            @Valid @RequestBody final UpdateCompilationDto updateCompilationDto
    ) {
        log.info("Updating compilation with id: {}, to: {}", compId, updateCompilationDto);
        return compilationService.updateCompilation(compId, updateCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable final Long compId) {
        log.info("Deleting compilation wirh id: {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(
            @PathVariable final Long eventId,
            @Valid @RequestBody final UpdateEventAdminRequestDto updater
    ) {
        log.info("Updating event by admin: {}, to: {}", eventId, updater);
        return eventService.updateEventByAdmin(eventId, updater);
    }

    @GetMapping("/events")
    public List<EventFullDto> getAllEvents(
            @RequestParam(required = false) final List<Long> users,
            @RequestParam(required = false) final List<Long> categories,
            @RequestParam(required = false) final List<EventState> states,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            final LocalDateTime rangeEnd,
            PageParams pageParams
    ) {
        log.info("Get events by admin with params:" +
                        "users: {},\ncategories: {},\nstates: {},\nrangeStart:{},\nrangeEnd:{},\npageParams:{}",
                users, categories, states, rangeStart, rangeEnd, pageParams);
        return eventService.getAllEventsByAdmin(users, categories, states, rangeStart, rangeEnd, pageParams);
    }
}
