package ru.practicum.mainservice.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.mainservice.dto.event.*;
import ru.practicum.mainservice.model.Category;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.model.enums.EventState;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "user")
    Event toModel(NewEventDto newEventDto, User user, Category category);

    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "views", source = "views")
    EventDto toDto(Event event, Long confirmedRequests, Long views);

    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "views", source = "views")
    EventShortDto toShortDto(Event event, Long confirmedRequests, Long views);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "category", source = "newCategory")
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", source = "newState")
    Event forUpdate(
            EventUpdateRequestDto updateEventDto,
            Category newCategory,
            EventState newState,
            @MappingTarget Event event
    );
}