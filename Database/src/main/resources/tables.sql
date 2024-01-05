CREATE DATABASE IF NOT EXISTS challenge;
use challenge;

CREATE TABLE IF NOT EXISTS challenge.language
(
    `uuid`   UUID PRIMARY KEY NOT NULL,
    language VARCHAR(30)      NOT NULL
);

CREATE TABLE IF NOT EXISTS challenge.timer
(
    id      INT PRIMARY KEY       NOT NULL,
    reverse BOOLEAN DEFAULT FALSE NOT NULL,
    time    BIGINT                NOT NULL
);