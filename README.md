# LeaveServiceHRM
**A Simple Employee Leave Management System**  
Built with Spring Boot, MySQL, Thymeleaf & Lombok

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.4-brightgreen.svg?style=flat&logo=spring-boot)
![Java](https://img.shields.io/badge/Java-17-blue.svg?style=flat&logo=java)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg?style=flat&logo=mysql)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-green.svg?style=flat)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

## Features

### Employee Can:
- Apply for leave
- View their own leave history
- Edit or delete **pending** leaves

### Admin Can:
- View all leave requests
- Approve or Reject pending leaves

## Tech Stack

| Layer        | Technology |
|--------------|------------|
| Backend      | Spring Boot 3.3.4 (Java 17) |
| Frontend     | Thymeleaf |
| Database     | MySQL |
| Dependencies | Spring Web, JPA, Lombok, MySQL Driver, DevTools |

## Project Structure

```
LeaveServiceHRM/
├── src/main/java/com/example/leaveservicehrm/
│   ├── controller/      
│   │     └── LeaveController.java
│   ├── entity/          
│   │     └── Leave.java
│   ├── repository/      
│         └── LeaveRepository.java
│   ├── model/           
│         └── CurrentUser.java
│   └── LeaveServiceHrmApplication.java
│
├── src/main/resources/
│   ├── templates/       
│   │     ├── employee/
│   │     └── admin/
│   └── application.properties
│
└── pom.xml
```

## How to Run (Step-by-Step)

### 1. Install Requirements
- Java 17+
- MySQL 8+
- IntelliJ IDEA

### 2. Create Database

```sql
CREATE DATABASE leavedb CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 3. Configure Application Properties
Edit: `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/leavedb
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.thymeleaf.cache=false
```

### 4. Run the App

1. Open project in IntelliJ IDEA
2. Run: **LeaveServiceHrmApplication.java**
3. Open in browser:

```
http://localhost:8080
```

## Ready to Use
Employees can apply leave, and Admin can approve or reject.

## Postman Documentation
https://documenter.getpostman.com/view/31052814/2sB3dHVD1e