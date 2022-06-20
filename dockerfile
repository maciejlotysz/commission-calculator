FROM amazoncorretto:17-alpine-full
WORKDIR /app/
ADD build/libs/*.jar /app/app.jar
ADD src/main/resources/files/transactions.csv /app/src/main/resources/files/transactions.csv
ADD src/main/resources/files/fee_wages.csv /app/src/main/resources/files/fee_wages.csv
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
