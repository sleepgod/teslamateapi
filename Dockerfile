FROM amazoncorretto:17

COPY target/teslamateapi.jar /

EXPOSE 8080

CMD ["java","-jar","/teslamateapi.jar"]
