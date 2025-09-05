# Simplified single-stage build for Java 11 Maven project
FROM openjdk:11-jdk-slim

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Set environment variables
ENV JAVA_HOME=/usr/local/openjdk-11
ENV MAVEN_HOME=/usr/share/maven
ENV PATH=$PATH:$MAVEN_HOME/bin
ENV MAVEN_OPTS="-Xmx1024m"

# Default command - can be overridden
CMD ["mvn", "--version"]

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD java -version || exit 1

# Labels for documentation
LABEL maintainer="Telekom Architecture Training"
LABEL description="Java 11 Maven environment for Design Patterns Examples"
LABEL version="1.0.0"