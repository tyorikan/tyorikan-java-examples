# 04-spring-web-virtual-thread

This project is a Spring Boot application that uses Virtual Threads.

## How to build

To build the application and create a container image using Jib, run the following command:

```bash
./mvnw compile jib:dockerBuild
```

## How to run

To run the application from the container image, run the following command:

```bash
docker run -p 8080:8080 demo:latest
```
