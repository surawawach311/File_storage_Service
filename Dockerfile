FROM goyalzz/ubuntu-java-8-maven-docker-image
RUN mkdir -p /build
WORKDIR /build
COPY pom.xml /build
#Download all required dependencies into one layer
RUN mvn -B dependency:resolve dependency:resolve-plugins
#Copy source code
COPY src /build/src
RUN mvn package -DskipTests
COPY docker-run-spring-boot.sh /build
# Run springboot
EXPOSE 8005
ENTRYPOINT ["bash", "/build/docker-run-spring-boot.sh"]