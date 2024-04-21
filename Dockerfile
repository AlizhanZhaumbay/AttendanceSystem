FROM maven:3.9-amazoncorretto-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM amazoncorretto:21
COPY --from=build /target/attendance-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV DB_URL=${DB_URL}
ENV WEBSITE_URL=${WEBSITE_URL}
ENTRYPOINT ["java", "-jar", "app.jar"]

