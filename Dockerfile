FROM maven:3.8.7 as build
WORKDIR /app
COPY pom.xml /app
RUN mvn dependency:resolve
COPY . /app
RUN mvn clean
RUN mvn package -DskipTests -X

FROM openjdk:17
COPY --from=build /app/target/*.jar /app.jar
EXPOSE 8080
CMD ["java","-Dspring.profiles.active=prod", "-jar", "/app.jar"]