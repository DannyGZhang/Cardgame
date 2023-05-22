
insert into stores (store_id, date_opened, street_address, province, city, postal_code, email, phone_number, status) values ('c293820a-d989-48ff-8410-24062a69d99e', '2016-05-12', '971 Oneill Trail', 'Quebec', 'Windsor', 'J1S 5J4', 'email@email1.com', '222-666-7777', 'OPEN');
insert into stores (store_id, date_opened, street_address, province, city, postal_code, email, phone_number, status) values ('1b5fb4a0-8761-47a6-bacb-ab3c99f8c480', '2012-06-08', '73046 Clarendon Terrace', 'Quebec', 'Magog', 'J1X 6J9', 'email1@email2.com', '255-777-8889','OPEN');



insert into inventories (inventory_id,store_id, last_updated, type, status) values ('d9dfcbd7-2bff-4a45-91d8-adae65c80f0c','c293820a-d989-48ff-8410-24062a69d99e', '2022-03-26','IN_STORE', 'OPEN');
insert into inventories (inventory_id,store_id, last_updated, type, status) values ('61865bbd-8f75-4ff9-b8a1-def97aa0a73c','1b5fb4a0-8761-47a6-bacb-ab3c99f8c480', '2022-07-06','OUTSIDE_STORE', 'OPEN');



