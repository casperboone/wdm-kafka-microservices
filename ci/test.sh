#!/usr/bin/env bash

set -e

# Docker compose looks in .env for environment variables
echo IMAGE_VERSION=${TRAVIS_BRANCH}_${TRAVIS_BUILD_NUMBER} > .env

# Pull the images created in build.sh from docker hub
docker-compose pull

# Start the environment in the background
docker-compose up -d

# Wait until the gateway is ready, so no 504 or 502 status is returned
attempt_num=1

until curl -Is http://localhost:8080/users | head -1 | grep -v 50
do
 if (( attempt_num == 10 ))
 then
     return 1
 else
     echo Retrying to connect
     sleep $(( attempt_num++ ))
 fi
done

# Run the end to end tests with gradle
./gradlew end-to-end-test

# Remove the docker containers
docker-compose down