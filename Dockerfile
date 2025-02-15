FROM amazoncorretto:23-alpine-jdk

ENV DB_NAME=thesketchboard
ENV DB_USER=bossman
ENV DB_PASS=dtPiZkd5tRVGDdWcAKur0maag0o7i1hP
ENV DB_HOST=dpg-cunrtma3esus73cmncgg-a.oregon-postgres.render.com
ENV DB_PORT=5432

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]