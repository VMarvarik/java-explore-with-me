CREATE TABLE IF NOT EXISTS user_ (
     id bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL,
     email varchar(254)  UNIQUE NOT NULL,
     name varchar(250)   NOT NULL,
     CONSTRAINT pk_user PRIMARY KEY (
        id
     )
);

CREATE TABLE IF NOT EXISTS location (
    id bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat float NOT NULL,
    lon float NOT NULL,
    CONSTRAINT pk_location PRIMARY KEY (
        id
    )
);

CREATE TABLE IF NOT EXISTS category (
    id bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name varchar(50) UNIQUE NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (
        id
    )
);

CREATE TABLE IF NOT EXISTS event (
    id bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation varchar(2000) NOT NULL,
    category_id bigint NOT NULL,
    created_on timestamp WITHOUT TIME ZONE NOT NULL,
    description varchar(7000) NOT NULL,
    event_date timestamp WITHOUT TIME ZONE NOT NULL,
    initiator_id bigint NOT NULL,
    location_id bigint NOT NULL,
    paid boolean NOT NULL,
    participant_limit int NOT NULL,
    published_on timestamp WITHOUT TIME ZONE NULL,
    request_moderation boolean NOT NULL,
    state varchar(9) NOT NULL,
    title varchar(120) NOT NULL,
    CONSTRAINT pk_event PRIMARY KEY (
        id
    ),
    CONSTRAINT fk_category_id FOREIGN KEY (
        category_id
    ) REFERENCES category (id),
    CONSTRAINT fk_initiator_id FOREIGN KEY (
        initiator_id
    ) REFERENCES user_ (id),
    CONSTRAINT fk_location_id FOREIGN KEY (
        location_id
    ) REFERENCES location (id)
);

CREATE TABLE IF NOT EXISTS compilation (
    id bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned boolean NOT NULL,
    title varchar(50) UNIQUE NOT NULL,
    CONSTRAINT pk_compilation PRIMARY KEY (
        id
    )
);

CREATE TABLE IF NOT EXISTS event_compilation (
    event_id bigint NOT NULL,
    compilation_id bigint NOT NULL,
    CONSTRAINT pk_event_compilation PRIMARY KEY (
        event_id,
        compilation_id
    ),
    CONSTRAINT fk_event_id FOREIGN KEY (
        event_id
    ) REFERENCES event (id),
    CONSTRAINT fk_compilation_id FOREIGN KEY (
        compilation_id
    ) REFERENCES compilation (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS request (
    id bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created timestamp WITHOUT TIME ZONE NOT NULL,
    event_id bigint NOT NULL,
    requester_id bigint NOT NULL,
    status varchar(10) NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (
        id
    ),
    CONSTRAINT fk_event_id FOREIGN KEY (
        event_id
    ) REFERENCES event (id),
    CONSTRAINT fk_requester_id FOREIGN KEY (
        requester_id
    ) REFERENCES user_ (id)
);

CREATE TABLE IF NOT EXISTS comment
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created_on   TIMESTAMP WITHOUT TIME ZONE,
    event_id     BIGINT,
    author_id    BIGINT,
    comment_text VARCHAR(2000) NOT NULL,
    CONSTRAINT fk_event_comment FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_comment FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);