#!/usr/bin/env bash

spring init \
--boot-version=3.0.2 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=CardGame-service \
--package-name=com.comicstore.cardgameservice \
--groupId=com.comicstore.cardgameservice \
--dependencies=web \
--version=1.0.0-SNAPSHOT \
CardGame-service