FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY web ./web
RUN mvn clean package -DskipTests

FROM tomcat:8.5-jdk17-temurin

# Instala o unzip para inspeção do .war
RUN apt-get update && apt-get install -y unzip

# Copia o .war gerado pelo Maven
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war
COPY context.xml /usr/local/tomcat/conf/
EXPOSE 8080 5005
CMD ["catalina.sh", "run"]
