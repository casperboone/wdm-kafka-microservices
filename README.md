# Web-scale Data Management on Kafka

[![Build Status](https://travis-ci.com/casperboone/wdm-kafka-microservices.svg?branch=master)](https://travis-ci.com/casperboone/wdm-kafka-microservices)

## Setup

### Docker compose (without build)

To start with docker-compose run the following commands.

```bash
# Pull the images
docker-compose pull

# Start the services
docker-compose up
```

All services are now available on `http://localhost:8080` in their respective directories.

You can specify a specific version by setting the `IMAGE_VERSION` environment variable to a tag on docker hub. Available tags are `<branchname>`, `<branchname_buildnumber>`, `latest`.

### Docker compose (with build)

To start with docker-compose with building first run the following commands.

```bash
# Build the jar files
./gradlew assemble

# Build the docker images
docker-compose build

# Start the services
docker-compose up
```

### Kubernetes

Run the following commands to get up and running. Kubernetes is set up to pull latest.

```bash
# Start Kafka & Zookeeper
kubectl apply -f k8s/development

# Start services
kubectl apply -f k8s/production
```

After this the service runs on `localhost`.
