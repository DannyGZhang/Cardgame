USE `clients-db`;

create table if not exists clients (
                         id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         client_id VARCHAR(40),
                         store_id VARCHAR(40),
                         first_name VARCHAR(50),
                         last_name VARCHAR(50),
                         email VARCHAR(50) UNIQUE,
                         phone_number VARCHAR(50),
                         total_bought DECIMAL(6,2),
                         rebate DECIMAL(10,2)
);
