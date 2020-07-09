1 - Create a SAML client under any Keycloak realm and map it to the app DNS
  1.1 - Inport the X509 certificate in Keycloak settings for the client (SAML Keys)
  1.2 - Make sure Valid Redirect URLs are set, along with Logout Service Binding URLs

3 - Update reload.sh with information (to be injected into src/main/resources/application.yml)
  3.1 - Set build-related information (variables are prefixed with JENKINS)
  3.2 - Set certificates (SP & IDP) 
  3.3 - Set IDP metadata and entity id (IDP Url for issuer) based on the realm

4 - Run reload.sh script to trigger build and initiate a container


* SAMLResponse was parsed to enable fetching attributes

* This example was created according to references below:
  - https://medium.com/@piraveenaparalogarajah/add-saml-authentication-to-spring-boot-app-with-wso2-is-17ed3d12c60d
  - https://docs.spring.io/spring-security/site/docs/5.2.1.RELEASE/reference/htmlsingle/#saml2

* Try adding SAML attributes - (see examples under src/main/resources/static: index.html, js/login_ng.js)
