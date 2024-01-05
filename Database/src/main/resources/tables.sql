CREATE DATABASE IF NOT EXISTS challenge;
use challenge;

CREATE TABLE IF NOT EXISTS challenge.language
(
    `uuid`   UUID PRIMARY KEY NOT NULL,
    language VARCHAR(30)      NOT NULL
);