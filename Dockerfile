FROM openjdk:8-jdk-alpine

RUN mkdir -p -m 777 /certs 
ADD ./sp.jks /certs/sp.jks

#NEW 
ADD ./pub.cer /certs/pub.cer
ADD ./pk.cer /certs/pk.cer
ADD ./kc.pub.cer /certs/kc.pub.cer


RUN chmod 777 /certs/sp.jks
ADD ./keycloak_client.jks oauth_client.jks
ADD ./build/libs/saml20-1.0.jar  app.jar

ARG keystorepass=${keystorepass}
ENV JAVA_OPTS="-Xss2048k -Djavax.net.ssl.trustStore=/oauth_client.jks -Djavax.net.ssl.trustStoreType=jks -Djavax.net.ssl.trustStorePassword=${keystorepass}" 
#ENV JAVA_OPTS="-Xss2048k"
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
