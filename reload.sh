#!/bin/bash

#----------------------- INICIA PROCESSO DE BUILD AQUI!!! -----------------------------------------------------
#seta vars de build
export name=saml20
export group=luis
export calculatedVersion=1.0
export outerport=9054

#gera artefato 
./gradlew build 

#----------------------- FINALIZA PROCESSO DE BUILD AQUI!!! -----------------------------------------------------


#----------------------- INICIA PROCESSO DE DEPLOYMENT AQUI!!! -----------------------------------------------------

#vars IDP SAML
export JENKINS_METADATA_URL="https://myidp.com/auth/realms/REALM/protocol/saml"
export JENKINS_ENTITY_ID="https://myidp.com/auth/realms/REALM"

#geração do Keystore a partir dos certificados publico e privado

export PUBLIC_CERT_VALUE=$(cat <<EOF
-----BEGIN CERTIFICATE-----
MUKHJ...O55dCA6E5OWeeF8sf36+DHg8j1l9pG5pnFQ==
-----END CERTIFICATE-----
EOF
)

export KEYCLOAK_PUBLIC_CERT_VALUE=$(cat <<EOF
-----BEGIN CERTIFICATE-----
NLUKN....NNbJ55
-----END CERTIFICATE-----
EOF
)

export PRIVATE_CERT_VALUE=$(cat <<EOF
-----BEGIN PRIVATE KEY-----
KJUMLUNLUN....8GnxnffHeQsXYMFuZNGVyZQN
-----END PRIVATE KEY-----
EOF
)

export JENKINS_PRIVATE_CERT=file:/certs/sp.jks
#NEW
export JENKINS_SP_PUBLIC_CERT=file:/certs/pub.cer
export JENKINS_SP_PRIVATE_CERT=file:/certs/pk.cer
export JENKINS_IDP_PUBLIC_CERT=file:/certs/kc.pub.cer



export JENKINS_PRIVATE_CERT_PASSWD=service_provider_passwd
export JENKINS_PRIVATE_CERT_ALIAS=service_provider_alias
export KEYCLOAK_KEYSTORE_PASS=keyCloak_pass

echo "$PUBLIC_CERT_VALUE" > pub.cer
echo "$PRIVATE_CERT_VALUE" > pk.cer
echo "$KEYCLOAK_PUBLIC_CERT_VALUE" > kc.pub.cer

#apaga eventual arq temporario (remover depois)
rm cert.p12
rm sp.jks

#gera pkcs12
cat pk.cer pub.cer | openssl pkcs12 -export -out cert.p12 -passin pass:"$JENKINS_PRIVATE_CERT_PASSWD" -passout pass:"$JENKINS_PRIVATE_CERT_PASSWD" -name "$JENKINS_PRIVATE_CERT_ALIAS"

#gera jks
keytool -importkeystore -alias "$JENKINS_PRIVATE_CERT_ALIAS" -srckeystore cert.p12 -srcstoretype pkcs12 -destkeystore sp.jks -srcstorepass "$JENKINS_PRIVATE_CERT_PASSWD" -deststorepass "$JENKINS_PRIVATE_CERT_PASSWD"

#gera keycloak jks
keytool -import -trustcacerts -file kc.pub.cer -alias server -keystore keycloak_client.jks -storepass "$KEYCLOAK_KEYSTORE_PASS" -noprompt

# agrupa variaveis que serao passadas para o container 
env | grep 'JENKINS' > env.list


# apaga app (em execução)  e imagem
docker rm -f $name
docker rmi  $group/$name:$calculatedVersion


#gera nova imagem
docker build -t $group/$name:$calculatedVersion . --build-arg keystorepass="$KEYCLOAK_KEYSTORE_PASS"

#roda app a partir da imagem
docker run --name $name -d -p $outerport:8999  -e "TZ=America/Sao_Paulo" --env-file ./env.list  $group/$name:$calculatedVersion

# apaga arquivos temporarios 
rm ./env.list 
rm ./keycloak_client.jks
rm ./pub.cer
rm ./pk.cer
rm ./kc.pub.cer 

docker logs -f $name

