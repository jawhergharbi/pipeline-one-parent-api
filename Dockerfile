# Creates a docker image based on an alpine linux with openjdk11 installed
FROM adoptopenjdk/openjdk11:alpine-jre

# Changed the working directory to /opt/app
WORKDIR /opt/app

# Define application executable (WATCH OUT!!! what does it happen if we change the version?)
ARG JAR_FILE=target/pipeline-api-0-0-01.jar

# cp application executable jar to /opt/app/app.jar
COPY ${JAR_FILE} app.jar

# Run the application: java -jar /opt/app/app.jar
ENTRYPOINT ["java","-Djava.security.edg=faile:/dev/./urandom", "-jar","app.jar"]
