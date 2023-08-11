package ru.practicum.mainservice.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.mainservice.dto.event.*;
import ru.practicum.mainservice.entity.Category;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.entity.User;
import ru.practicum.mainservice.enums.EventState;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "user")
    Event toEntity(NewEventDto newEventDto, User user, Category category);

    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "views", source = "views")
    EventFullDto toFullDto(Event event, Long confirmedRequests, Long views);

    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "views", source = "views")
    EventShortDto toShortDto(Event event, Long confirmedRequests, Long views);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "category", source = "newCategory")
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", source = "newState")
    Event partialUpdate(
            UpdateEventUserRequestDto updateEventDto,
            Category newCategory,
            EventState newState,
            @MappingTarget Event event
    );

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "category", source = "newCategory")
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", source = "newState")
    Event partialUpdate(
            UpdateEventAdminRequestDto updateEventDto,
            Category newCategory,
            EventState newState,
            @MappingTarget Event event
    );
}