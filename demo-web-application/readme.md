**Demonstration project for Piano**

To run tests and make this app work type

`mvn clean verify spring-boot:run`

By default, application can be accessed on 
`http://localhost:8080/`

Purpose of this application is to implement authentication workflow using JWT token.

Basic scenario:

1. User opens `localhost:8080/auth?redirectUrl=http://httpbin.org/get?someparam=`
2. System redirects user to the login\registration page
3. User enters his credentials for registration
4. User is redirected to  page that indicates his successful authentication
5. In 5 seconds User is redirected to the address from his request with his JWT. 

_Technologies used_: Spring Boot + MVC + Security, Thymeleaf, JPA over H2 Db

`Надеюсь, меня простят за то, что я накодил`

**Task description:**

**The idea**
Create a Java (or Kotlin) web application that allows a user to be authenticated by login and password. As an authentication process result JWT should be returned. That token is required for further authorization which is out of this exercise scope.  

**Use case**
1. User opens your web application page with url like this `https://yourapp:8080/auth?redirect_url=https%3A%2F%2Fhttpbin.org%2Fget%3Fsomeparam%3D...`. This page is acting like a sign in/sign up form.
    1) If user was already logged in then go to step 3.
2. User sends their credentials via form or do logout.
    1) In case of sign in: app should authenticate existing user based on data stored in the persistent layer.
    2) In case of sign up: app should create a new user record in persistent layer.
    3) In case of logout: sign up/sign in is not available until user is logged in. After logout user can sign in again.
3. Page indicates with an authentication result. And in case of successful result user should be redirected with JWT to redirect_url from the query param.

**Requirements**
1. Web app should contain at least two endpoints:
   1) For sign in
   2) For sign up
2. Basic exception situation should be properly handled and logged.
3. Basic use cases should be covered by integration test(s).
4. Attach sql script that stands for your application’s persistent layer schema.
