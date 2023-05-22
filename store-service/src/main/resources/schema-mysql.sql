USE `store-db`;


create table if not exists stores (
                        id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        store_id VARCHAR(40),
                        date_opened DATE,
                        street_address VARCHAR(50) UNIQUE,
                        province VARCHAR(50),
                        city VARCHAR(50),
                        postal_code VARCHAR(50) UNIQUE,
                        email VARCHAR(50),
                        phone_number VARCHAR(50),
                        status varchar(40)
);


create table if not exists inventories (
                                    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                    inventory_id VARCHAR(40),
                                    type VARCHAR(40),
                                    store_id VARCHAR(40),
                                    last_updated DATE,
                                    status varchar(40)
);