FROM openjdk:12
RUN mkdir -p /usr/app
WORKDIR /usr/app
COPY ./target/*.jar .
EXPOSE 8080
CMD java -jar *.jar
# ENTRYPOINT ["java", "-jar", "studentsystem-0.0.1-SNAPSHOT.jar"]