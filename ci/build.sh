#!/usr/bin/env bash

# Any error should halt this script
set -e

SUB_PROJECT=$1
LOCATION=$2
NAME=$3

IMAGE_NAME=wdmk/${NAME}:${TRAVIS_BRANCH}_${TRAVIS_BUILD_NUMBER}
IMAGE_NAME_SHORT=wdmk/${NAME}:${TRAVIS_BRANCH}
IMAGE_NAME_LATEST=wdmk/${NAME}:latest

echo Pushing images to Docker Hub

# Build this sub project
./gradlew :${SUB_PROJECT}:assemble

# Create a docker image for this sub project
docker build -t ${IMAGE_NAME} -t ${IMAGE_NAME_SHORT} ${LOCATION}

# Login into docker hub
echo "${DOCKER_PASSWORD}" | docker login -u "${DOCKER_USERNAME}" --password-stdin

echo Pushing to ${IMAGE_NAME}
docker push ${IMAGE_NAME}

# For branch builds push the image with the branch name as tag
if [[ "$TRAVIS_PULL_REQUEST" = "false" ]]; then
  echo Pushing to ${IMAGE_NAME_SHORT}
  docker push ${IMAGE_NAME_SHORT}

  # For master also push to the latest tag
  if [[ "$TRAVIS_BRANCH" = "master" ]]; then
    echo Pushing to ${IMAGE_NAME_LATEST}
    docker tag ${IMAGE_NAME} ${IMAGE_NAME_LATEST}
    docker push ${IMAGE_NAME_LATEST}
  fi
fi

# If a branch is tagged, also push to that tag in docker hub
if [[ -n "$TRAVIS_TAG" ]]; then
  IMAGE_NAME_TAG=wdmk/${SUB_PROJECT}:"$TRAVIS_TAG"
  echo Pushing to ${IMAGE_NAME_TAG}
  docker tag ${IMAGE_NAME} ${IMAGE_NAME_TAG}
  docker push ${IMAGE_NAME_TAG}
fi
