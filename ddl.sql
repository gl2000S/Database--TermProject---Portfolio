-- =============================================================
--  CineTrack — Movie Watchlist & Social Tracker
--  DDL Script
--  Database: cinetrack
-- =============================================================

CREATE DATABASE IF NOT EXISTS cinetrack
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE cinetrack;

-- =============================================================
--  TABLE: USER
--  Stores registered user accounts.
-- =============================================================
CREATE TABLE IF NOT EXISTS USER (
    user_id       INT             NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)     NOT NULL,
    email         VARCHAR(255)    NOT NULL,
    password_hash VARCHAR(255)    NOT NULL,   -- bcrypt hash
    bio           VARCHAR(500),
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_user       PRIMARY KEY (user_id),
    CONSTRAINT uq_username   UNIQUE (username),
    CONSTRAINT uq_email      UNIQUE (email)
);

-- =============================================================
--  TABLE: GENRE
--  Lookup table for movie genres (e.g. Action, Drama, Comedy).
-- =============================================================
CREATE TABLE IF NOT EXISTS GENRE (
    genre_id   INT          NOT NULL AUTO_INCREMENT,
    name       VARCHAR(50)  NOT NULL,

    CONSTRAINT pk_genre    PRIMARY KEY (genre_id),
    CONSTRAINT uq_genre    UNIQUE (name)
);

-- =============================================================
--  TABLE: DIRECTOR
--  Stores director profiles.
-- =============================================================
CREATE TABLE IF NOT EXISTS DIRECTOR (
    director_id  INT           NOT NULL AUTO_INCREMENT,
    name         VARCHAR(150)  NOT NULL,
    bio          TEXT,
    birthdate    DATE,
    photo_url    VARCHAR(500),

    CONSTRAINT pk_director PRIMARY KEY (director_id)
);

-- =============================================================
--  TABLE: ACTOR
--  Stores actor profiles.
-- =============================================================
CREATE TABLE IF NOT EXISTS ACTOR (
    actor_id   INT           NOT NULL AUTO_INCREMENT,
    name       VARCHAR(150)  NOT NULL,
    bio        TEXT,
    birthdate  DATE,
    photo_url  VARCHAR(500),

    CONSTRAINT pk_actor PRIMARY KEY (actor_id)
);

-- =============================================================
--  TABLE: MOVIE
--  Core movie catalog. avg_rating is a cached value updated
--  whenever a review is added, edited, or deleted.
-- =============================================================
CREATE TABLE IF NOT EXISTS MOVIE (
    movie_id     INT            NOT NULL AUTO_INCREMENT,
    title        VARCHAR(255)   NOT NULL,
    release_year YEAR,
    runtime_min  INT,
    synopsis     TEXT,
    poster_url   VARCHAR(500),
    avg_rating   DECIMAL(3, 1)  DEFAULT NULL,

    CONSTRAINT pk_movie         PRIMARY KEY (movie_id),
    CONSTRAINT chk_runtime      CHECK (runtime_min > 0),
    CONSTRAINT chk_avg_rating   CHECK (avg_rating BETWEEN 1.0 AND 10.0 OR avg_rating IS NULL)
);

-- =============================================================
--  TABLE: MOVIE_GENRE
--  Many-to-many: movies ↔ genres.
-- =============================================================
CREATE TABLE IF NOT EXISTS MOVIE_GENRE (
    movie_id   INT  NOT NULL,
    genre_id   INT  NOT NULL,

    CONSTRAINT pk_movie_genre   PRIMARY KEY (movie_id, genre_id),
    CONSTRAINT fk_mg_movie      FOREIGN KEY (movie_id)  REFERENCES MOVIE (movie_id)  ON DELETE CASCADE,
    CONSTRAINT fk_mg_genre      FOREIGN KEY (genre_id)  REFERENCES GENRE (genre_id)  ON DELETE CASCADE
);

-- =============================================================
--  TABLE: MOVIE_DIRECTOR
--  Many-to-many: movies ↔ directors.
--  A movie can have multiple directors (co-directors),
--  and a director can direct many movies.
-- =============================================================
CREATE TABLE IF NOT EXISTS MOVIE_DIRECTOR (
    movie_id     INT  NOT NULL,
    director_id  INT  NOT NULL,

    CONSTRAINT pk_movie_director  PRIMARY KEY (movie_id, director_id),
    CONSTRAINT fk_md_movie        FOREIGN KEY (movie_id)    REFERENCES MOVIE    (movie_id)    ON DELETE CASCADE,
    CONSTRAINT fk_md_director     FOREIGN KEY (director_id) REFERENCES DIRECTOR (director_id) ON DELETE CASCADE
);

-- =============================================================
--  TABLE: MOVIE_ACTOR
--  Many-to-many: movies ↔ actors.
--  Carries character_name as a payload attribute.
-- =============================================================
CREATE TABLE IF NOT EXISTS MOVIE_ACTOR (
    movie_id        INT           NOT NULL,
    actor_id        INT           NOT NULL,
    character_name  VARCHAR(150),

    CONSTRAINT pk_movie_actor  PRIMARY KEY (movie_id, actor_id),
    CONSTRAINT fk_ma_movie     FOREIGN KEY (movie_id)  REFERENCES MOVIE  (movie_id)  ON DELETE CASCADE,
    CONSTRAINT fk_ma_actor     FOREIGN KEY (actor_id)  REFERENCES ACTOR  (actor_id)  ON DELETE CASCADE
);

-- =============================================================
--  TABLE: REVIEW
--  User reviews for movies. One review per user per movie
--  enforced via unique constraint on (user_id, movie_id).
--  Rating is 1–10.
-- =============================================================
CREATE TABLE IF NOT EXISTS REVIEW (
    review_id   INT      NOT NULL AUTO_INCREMENT,
    user_id     INT      NOT NULL,
    movie_id    INT      NOT NULL,
    rating      TINYINT  NOT NULL,
    body        TEXT,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_review            PRIMARY KEY (review_id),
    CONSTRAINT uq_user_movie_review UNIQUE (user_id, movie_id),
    CONSTRAINT chk_rating           CHECK (rating BETWEEN 1 AND 10),
    CONSTRAINT fk_review_user       FOREIGN KEY (user_id)  REFERENCES USER  (user_id)  ON DELETE CASCADE,
    CONSTRAINT fk_review_movie      FOREIGN KEY (movie_id) REFERENCES MOVIE (movie_id) ON DELETE CASCADE
);

-- =============================================================
--  TABLE: WATCHLIST_ENTRY
--  Tracks a user's relationship with a movie.
--  status: 'WATCHED' | 'WANT_TO_WATCH' | 'DROPPED'
-- =============================================================
CREATE TABLE IF NOT EXISTS WATCHLIST_ENTRY (
    entry_id    INT          NOT NULL AUTO_INCREMENT,
    user_id     INT          NOT NULL,
    movie_id    INT          NOT NULL,
    status      ENUM('WATCHED', 'WANT_TO_WATCH', 'DROPPED') NOT NULL DEFAULT 'WANT_TO_WATCH',
    added_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_watchlist_entry    PRIMARY KEY (entry_id),
    CONSTRAINT uq_watchlist          UNIQUE (user_id, movie_id),
    CONSTRAINT fk_wl_user            FOREIGN KEY (user_id)  REFERENCES USER  (user_id)  ON DELETE CASCADE,
    CONSTRAINT fk_wl_movie           FOREIGN KEY (movie_id) REFERENCES MOVIE (movie_id) ON DELETE CASCADE
);

-- =============================================================
--  TABLE: FOLLOWS
--  Self-referencing relationship on USER.
--  follower_id follows followee_id.
--  Prevents self-follows via check constraint.
-- =============================================================
CREATE TABLE IF NOT EXISTS FOLLOWS (
    follower_id   INT       NOT NULL,
    followee_id   INT       NOT NULL,
    followed_at   DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_follows       PRIMARY KEY (follower_id, followee_id),
    CONSTRAINT fk_follower      FOREIGN KEY (follower_id) REFERENCES USER (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_followee      FOREIGN KEY (followee_id) REFERENCES USER (user_id) ON DELETE CASCADE,
    CONSTRAINT chk_no_self_follow CHECK (follower_id <> followee_id)
);

-- =============================================================
--  INDEXES
--  Two required indexes to optimize query performance.
-- =============================================================

-- Index 1: Speed up review lookups by movie (used on Movie Detail page
--          to fetch all reviews for a given movie, and for avg rating calc).
CREATE INDEX idx_review_movie_id
    ON REVIEW (movie_id);

-- Index 2: Speed up watchlist lookups by user (used on Watchlist Dashboard
--          to fetch all entries for the logged-in user).
CREATE INDEX idx_watchlist_user_id
    ON WATCHLIST_ENTRY (user_id);

-- Bonus Index 3: Speed up movie searches by title (used on Search page).
CREATE INDEX idx_movie_title
    ON MOVIE (title);

-- Bonus Index 4: Speed up follower feed queries (used on User Profile page
--               to fetch activity from users that a person follows).
CREATE INDEX idx_follows_follower
    ON FOLLOWS (follower_id);
