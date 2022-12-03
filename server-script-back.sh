#!/usr/bin/env bash

export IMAGE=$1
docker-compose -f docker-compose-spring.yaml up --detach