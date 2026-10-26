CREATE TABLE pieces (
                        id                    BIGSERIAL PRIMARY KEY,
                        title                 VARCHAR(255) NOT NULL,
                        slug                  VARCHAR(255) NOT NULL UNIQUE,
                        language              VARCHAR(2)   NOT NULL CHECK (language IN ('HI', 'EN')),
    type                  VARCHAR(20)  NOT NULL CHECK (type IN ('STORY', 'JOURNAL', 'GHAZAL', 'QUOTE')),
    body                  TEXT         NOT NULL,
    author_name           VARCHAR(150),
    reading_time_minutes  INTEGER,
    published_on          DATE,
    status                VARCHAR(10)  NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED')),
    featured              BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pieces_browse ON pieces (status, language, type);

CREATE TABLE moods (
                       id          BIGSERIAL PRIMARY KEY,
                       name        VARCHAR(50) NOT NULL UNIQUE,
                       slug        VARCHAR(50) NOT NULL UNIQUE,
                       sort_order  INTEGER     NOT NULL DEFAULT 0
);

CREATE TABLE themes (
                        id    BIGSERIAL PRIMARY KEY,
                        name  VARCHAR(80) NOT NULL UNIQUE,
                        slug  VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE collections (
                             id               BIGSERIAL PRIMARY KEY,
                             name             VARCHAR(150) NOT NULL,
                             slug             VARCHAR(150) NOT NULL UNIQUE,
                             description      TEXT,
                             cover_image_url  VARCHAR(500),
                             sort_order       INTEGER NOT NULL DEFAULT 0,
                             published        BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE piece_moods (
                             piece_id  BIGINT NOT NULL REFERENCES pieces (id) ON DELETE CASCADE,
                             mood_id   BIGINT NOT NULL REFERENCES moods (id)  ON DELETE CASCADE,
                             PRIMARY KEY (piece_id, mood_id)
);

CREATE TABLE piece_themes (
                              piece_id  BIGINT NOT NULL REFERENCES pieces (id) ON DELETE CASCADE,
                              theme_id  BIGINT NOT NULL REFERENCES themes (id) ON DELETE CASCADE,
                              PRIMARY KEY (piece_id, theme_id)
);

CREATE TABLE collection_pieces (
                                   collection_id  BIGINT  NOT NULL REFERENCES collections (id) ON DELETE CASCADE,
                                   piece_id       BIGINT  NOT NULL REFERENCES pieces (id)      ON DELETE CASCADE,
                                   position       INTEGER NOT NULL DEFAULT 0,
                                   PRIMARY KEY (collection_id, piece_id)
);

INSERT INTO moods (name, slug, sort_order) VALUES
                                               ('Peaceful',    'peaceful',    1),
                                               ('Hopeful',     'hopeful',     2),
                                               ('Nostalgic',   'nostalgic',   3),
                                               ('Lonely',      'lonely',      4),
                                               ('Romantic',    'romantic',    5),
                                               ('Reflective',  'reflective',  6),
                                               ('Joyful',      'joyful',      7),
                                               ('Melancholic', 'melancholic', 8),
                                               ('Restless',    'restless',    9),
                                               ('Comforted',   'comforted',  10);