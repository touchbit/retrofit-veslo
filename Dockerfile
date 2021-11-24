FROM maven:3.8.4-jdk-8

WORKDIR /app
COPY . /app

RUN mvn clean package
