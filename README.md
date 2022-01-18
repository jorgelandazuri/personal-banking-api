# PERSONAL BANKING SPRING BOOT REST API
Java Spring Boot REST API POC project. The API allows: 
- Account creation with initial deposit. 
- Transfers between 2 accounts. 
- Balance retrieval. 
- Transaction history for a given account.

## API endpoints
<img src="https://github.com/jorgelandazuri/personal-banking-api/blob/master/src/test/resources/repo/img/api-create-account.png" width="100%" height="20%"/>

<img src="https://github.com/jorgelandazuri/personal-banking-api/blob/master/src/test/resources/repo/img/api-get-balance.png" width="100%" height="20%"/>

<img src="https://github.com/jorgelandazuri/personal-banking-api/blob/master/src/test/resources/repo/img/api-make-transfer.png" width="100%" height="20%"/>

<img src="https://github.com/jorgelandazuri/personal-banking-api/blob/master/src/test/resources/repo/img/api-get-transactions.png" width="100%" height="20%"/>


## Tech stack
- Java Spring Boot v.2.3.12.RELEASE
- Spring validation v.2.3.12.RELEASE
- Spring Data JPA v.2.3.12.RELEASE
- MySQL database server v.8.0.23
- Mockito and Junit for unit testing.
- Spring Mock MVC for controllers unit testing.
- REST-Assured and Cucumber for integration and BDD testing of the API.
- Swagger2 v.3.0.0 for API documentation and manual testing.

## Instructions to try it out
- Run a fresh MySQL database instance on localhost:3306
  - via docker: 'docker run --name bank -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password123 -d mysql:latest'
- Build the project with maven (mvn clean install)
  - It will run the jUnit tests and API automated test (Cucumber + RestAssured)
- Run the Springboot application, it will start on port 8080.
- On start, the database will be pre-populated with some account holders and a Bank Treasury account.  
- The API is documented with Swagger2 on 'http://localhost:8080/api/v1/swagger-ui/index.html'.
