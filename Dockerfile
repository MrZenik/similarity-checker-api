#FROM openjdk:17-jdk-alpine AS build
#
#RUN apk add --no-cache python3 python3-dev py3-pip
#
#WORKDIR /app
#COPY . .
#RUN ./mvnw package -DskipTests
#
#FROM openjdk:17-jdk-alpine
#ARG JAR_FILE=target/*.jar
#COPY --from=build /app/${JAR_FILE} app.jar
#COPY --from=build /app/src/main/resources/similarity-checker /app/similarity-checker
#
#RUN apk add --no-cache python3 py3-numpy py3-pygments py3-jinja2 py3-tqdm py3-setuptools
#RUN apk add --no-cache python3-dev py3-pip && \
#    pip3 install --upgrade pip && \
#    pip3 install psycopg2-binary
#
#ENTRYPOINT ["java","-jar","/app.jar"]

FROM openjdk:17-jdk-alpine

RUN apk add --no-cache python3 python3-dev py3-pip

WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests

RUN apk add --no-cache python3 py3-numpy py3-pygments py3-jinja2 py3-tqdm py3-setuptools
RUN apk add --no-cache python3-dev py3-pip && \
    pip3 install --upgrade pip && \
    pip3 install psycopg2-binary

COPY src/main/resources/similarity-checker /app/similarity-checker
RUN mv target/*.jar app.jar
