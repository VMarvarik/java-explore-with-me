package ru.practicum.mainservice.util;

import lombok.*;
import org.springframework.data.domain.PageRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public final class PageParams {
    @PositiveOrZero(message = "АйДи запроса не может быть меньше 0")
    private Integer from = 0;
    @Positive(message = "Размер страницы должен быть больше 0")
    private Integer size = 10;

    public PageRequest makePageRequest() {
        return PageRequest.of(from / size, size);
    }
}
