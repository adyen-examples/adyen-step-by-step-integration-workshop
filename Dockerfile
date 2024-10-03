FROM amazoncorretto:17-alpine-jdk
COPY build/libs/adyen-step-by-step-integration-workshop-0.0.1-SNAPSHOT.jar adyen-step-by-step-integration-workshop-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/adyen-step-by-step-integration-workshop-0.0.1-SNAPSHOT.jar"]