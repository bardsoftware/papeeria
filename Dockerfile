FROM java:8 
MAINTAINER Dmitry Barashev <dbarashev@bardsoftware.com>

ADD target/sufler-1.0-jar-with-dependencies.jar /opt/sufler/sufler.jar
ADD launch-sufler-app /opt/sufler/launch-sufler-app
ADD launch-sufler-index /opt/sufler/launch-sufler-index

EXPOSE 8080
