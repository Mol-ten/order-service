FROM openjdk:21-jdk-slim
WORKDIR /
COPY target/order-service-0.0.1-SNAPSHOT.jar order-service.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "TechShop-CatalogService.jar"]