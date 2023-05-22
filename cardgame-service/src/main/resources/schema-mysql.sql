USE `cardgame-db`;

create table if not exists card_games
(
    id
                   INTEGER
        NOT
            NULL
        AUTO_INCREMENT
        PRIMARY
            KEY,
    card_id
                   VARCHAR(40),
    card_game_name VARCHAR(50) UNIQUE,
    company        VARCHAR(50),
    release_date   DATE,
    is_active      BIT
);



create table if not exists card_game_sets
(
    id
                    INTEGER
        NOT
            NULL
        AUTO_INCREMENT
        PRIMARY
            KEY,
    set_id
                    VARCHAR(40),
    card_id
                    VARCHAR(40),
    name            VARCHAR(40) UNIQUE,
    release_date    DATE,
    number_of_cards INTEGER
);


