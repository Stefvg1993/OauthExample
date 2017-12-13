# OauthExample
A Spring boot example for Oauth authentication. Proper user management through an sql data storage. Check also the Angular 4 example for a proper login website with user management: https://github.com/Stefvg1993/OauthAngularExample.
Every call that goes to 'url'/secured/... has to be authenticated. Every other call is unsecured.


## Requirements
* An sql database, the required schema is available in the source code.
* Java 8 SDK

## Get things running
**Change the following properties in the application.properties to your settings:**
* Datasource settings
spring.datasource.url -> sql url (including db)
spring.datasource.username -> sql username
spring.datasource.password -> sql password

* Mail server settings
spring.mail.host -> smtp host
spring.mail.port -> smpt port
spring.mail.username -> username/email
spring.mail.password -> password

**Security settings**
Change the following settings in the config class file to your needs:
* AdditionalWebConfig.java
  Change the allowed origins url to the the url(s) of your requesting applications or '*' for all
  
* AuthorizationServerConfig.java
  In this class, you'll define the client id's and the secrets that are used to encode the tokens at the application side. 
  ```
          configurer
                .inMemory()
                .withClient(CLIENT_ID)
                .secret(SIGNING_KEY)
                .authorizedGrantTypes(GRANT_TYPE)
                .scopes(SCOPE_READ, SCOPE_WRITE)
                .and()
                .withClient....;
  ```
  You can add as many clients as you want with '.and()'.
  
# Requesting a token/logging in
Authentication is made possible through a POST call to the REST service on url /oauth/token
* Headers:
  key: Authorization, value: 'Basic encodedkey' -> encodedkey is the base64 encoding of a combination 'client:secret' that you entered in the security settings.
  
* Body:
  key: username, value: 'username' -> a username that is available in the system
  key: password, value: 'password' -> the password belonging to the username in the system
  key: grant_type, value: 'password' -> Indicates that it is password authentication that is used
  
This returns, if the combination username/password is correct, a response with a token that you have to use with every authenticated request.

## Use the token
With every request (that needs authentication), add the token to the Http headers:
* Key: Authorization, value: 'Bearer token' where token is the received token

## Sample users
In the 'insert_sample_data.sql' file, you'll find 2 users that can be used to test the system. Both passwords are 'password'.

#API
Check the Resource files for the api methods that can be used to register/update/delete users or request a new password/confirm a new password.
