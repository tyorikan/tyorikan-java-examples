# Multi-stage Dockerfile for GraalVM Native Image
FROM ghcr.io/graalvm/graalvm-community:17 AS builder

# Install native-image
RUN gu install native-image

# Set working directory
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Install Maven
RUN microdnf install -y wget tar gzip
RUN wget https://archive.apache.org/dist/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz
RUN tar -xzf apache-maven-3.9.5-bin.tar.gz
RUN mv apache-maven-3.9.5 /opt/maven
RUN rm apache-maven-3.9.5-bin.tar.gz
ENV PATH="/opt/maven/bin:${PATH}"

# Set memory limits for Maven and GraalVM native image compilation
ENV MAVEN_OPTS="-Xmx4g -XX:MaxMetaspaceSize=1g"
ENV NATIVE_IMAGE_OPTS="-J-Xmx8g -J-XX:MaxDirectMemorySize=2g"

# Download dependencies first for better caching
RUN mvn dependency:go-offline -B

# Build the application and create native image
RUN mvn clean package -Pnative -DskipTests

# Runtime stage - use same base as builder to ensure GLIBC compatibility
FROM ghcr.io/graalvm/graalvm-community:17

# Install minimal runtime dependencies and create non-root user
RUN microdnf install -y ca-certificates && \
    microdnf clean all && \
    groupadd -r appuser && useradd -r -g appuser appuser

# Copy the native executable
COPY --from=builder /app/target/legacy-appmod-graalvm /app/application

# Set ownership and permissions
RUN chown appuser:appuser /app/application && \
    chmod +x /app/application

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Set the entrypoint
ENTRYPOINT ["/app/application"]
