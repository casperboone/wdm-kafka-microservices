# Web-scale Data Management on Kafka

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

To start with docker-compsose with building first run the following commands.

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
# Install nginx ingress in the cluster
helm install stable/nginx-ingress --name nginx-ingress

# Start Kafka & Zookeeper
kubectl apply -f k8s/development

# Start services
kubectl apply -f k8s/production
```

After this the orders, payments, stock and users services are accessible on ports 30001, 30002, 30003 and 30004 respectively (this will change to something more sensible).