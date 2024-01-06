CREATE DATABASE IF NOT EXISTS challenge;
use challenge;

CREATE TABLE IF NOT EXISTS challenge.language
(
    `uuid`   UUID PRIMARY KEY NOT NULL,
    language VARCHAR(30)      NOT NULL
);

CREATE TABLE IF NOT EXISTS challenge.timer
(
    id      INTEGER PRIMARY KEY   NOT NULL,
    reverse BOOLEAN DEFAULT FALSE NOT NULL,
    time    BIGINT                NOT NULL
);

CREATE TABLE IF NOT EXISTS challenge.challenge_data
(
    name    VARCHAR(255) PRIMARY KEY NOT NULL,
    enabled BOOLEAN DEFAULT FALSE    NOT NULL
);

CREATE TABLE IF NOT EXISTS challenge.challenge_settings
(
    id          INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
    name        VARCHAR(255)                       NOT NULL,
    setting_key VARCHAR(255)                       NOT NULL,
    setting     JSON                               NOT NULL,
    FOREIGN KEY (name) REFERENCES challenge.challenge_data (name),
    UNIQUE (name, setting_key)
);