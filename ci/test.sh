#!/usr/bin/env bash

set -e

# Docker compose looks in .env for environment variables
echo IMAGE_VERSION=${TRAVIS_BRANCH}_${TRAVIS_BUILD_NUMBER} > .env

# Pull the images created in build.sh from docker hub
docker-compose pull

# Start the environment in the background
docker-compose up -d

function await {
    port=$1
    attempt_num=1

    # Wait until status 200 is returned
    until curl -Is http://localhost:${port} | head -1 | grep 200 > /dev/null
    do
     if (( attempt_num == 10 ))
     then
         return 1
     else
         echo Trying to connect to localhost:${port}
         sleep $(( attempt_num++ ))
     fi
    done
}

# Check if al services are up
await 10001
echo Orders service is up
await 10002
echo Payments service is up
await 10003
echo Stock service is up
await 10004
echo Users service is up

# Run the end to end tests with gradle
./gradlew end-to-end-test

# Remove the docker containers
docker-compose down