-- Script d'initialisation pour les tests
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS guilds (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    guild_id BIGINT REFERENCES guilds(id)
);

CREATE TABLE IF NOT EXISTS channels (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    guild_id BIGINT REFERENCES guilds(id)
);