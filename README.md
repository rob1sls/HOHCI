# Social-Networking-App-Using-Spring-Boot-And-Angular

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




## App Demo Video
[![Alt text](https://img.youtube.com/vi/HMN8NCSm98s/0.jpg)](https://www.youtube.com/watch?v=HMN8NCSm98s)

## App Features:

- Users can create new account.
- Users can follow/unfollow other users.
- Users can create/update/delete new posts.
- Users can like/unlike posts.
- Users can share posts.
- Posts can have text content, an image and tags.
- Users will have timeline which will contain posts from following users.
- Users can update profile info/email/password.
- Users can verify their email addresses.
- Users can reset their passwords.
- Users will receive notifications when their posts are liked/shared/commented on.

## Technologies Used:

### Backend

- Java
- Spring Boot
- Spring Security
- Spring REST
- MySQL
- H2 Database
- JUnit
- Mockito
- Maven

### Frontend

- Angular
- Angular Material UI
- Typescript

### Tests

- Unit test
- Integration test
