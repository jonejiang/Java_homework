FROM java:8-jdk

ADD build/libs/video-service-*.jar /video-service.jar

EXPOSE 8080
RUN bash -c 'touch /video-service.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/video-service.jar"]
