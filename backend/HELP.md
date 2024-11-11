# Getting Started

# Frontend 
install node : https://nodejs.org/en

in the frontend terminal : 
npm install -g @angular/cli
npm install
npm start

# Backend
jdk 17 needed : https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html

in the backend terminal : 
./mvnw clean install
java -jar .\target\Social-App-0.0.1-SNAPSHOT.jar

# DataBase
install MySQL
MySQL Workbench

create your local database following this : 
    spring.datasource.url=jdbc:mysql://localhost:3306/my_database
    spring.datasource.username=my_user
    spring.datasource.password=handsonhcidb

in a terminal :
mysql -u my_user -p
handsonhcidb
CREATE DATABASE my_database
CREATE USER 'my_user'@'localhost' IDENTIFIED BY 'handsonhcidb';
GRANT ALL PRIVILEGES ON my_database.* TO 'my_user'@'localhost';
FLUSH PRIVILEGES;



### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.7/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.7/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.6.7/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.6.7/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Spring Security](https://docs.spring.io/spring-boot/docs/2.6.7/reference/htmlsingle/#boot-features-security)
* [Validation](https://docs.spring.io/spring-boot/docs/2.6.7/reference/htmlsingle/#boot-features-validation)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)

