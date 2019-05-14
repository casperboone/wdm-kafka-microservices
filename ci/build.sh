#!/usr/bin/env bash

set -ev

SUB_PROJECT=$1
IMAGE_NAME=wdmk/${SUB_PROJECT}:${TRAVIS_BRANCH}_${TRAVIS_BUILD_NUMBER}
IMAGE_NAME_SHORT=wdmk/${SUB_PROJECT}:${TRAVIS_BRANCH}

# Build and test this sub project
./gradlew :${SUB_PROJECT}:assemble :${SUB_PROJECT}:check

# Create a docker image for this sub project
docker build -t ${IMAGE_NAME} -t ${IMAGE_NAME_SHORT} ${SUB_PROJECT}

# Login into docker hub
echo "${DOCKER_PASSWORD}" | docker login -u "${DOCKER_USERNAME}" --password-stdin

# Push the image to docker hub
docker push ${IMAGE_NAME}

# For branch builds push the image with the branch name as tag
if [[ "$TRAVIS_PULL_REQUEST" = "false" ]]; then
  docker push ${IMAGE_NAME_SHORT}

  # For master also push to the latest tag
  if [[ "$TRAVIS_BRANCH" = "master" ]]; then
    docker tag ${IMAGE_NAME} ${IMAGE_NAME_LATEST}
    docker push ${IMAGE_NAME} ${IMAGE_NAME_LATEST}
  fi
fi

# If a branch is tagged, also push to that tag in docker hub
if [[ -n "$TRAVIS_TAG" ]]; then
  IMAGE_NAME_TAG=wdmk/${SUB_PROJECT}:"$TRAVIS_TAG"
  docker tag ${IMAGE_NAME} ${IMAGE_NAME_TAG}
  docker push ${IMAGE_NAME} ${IMAGE_NAME_TAG}
fi
