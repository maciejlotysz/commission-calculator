# Digital-Colliers-recruitment-task 2.0

This is REST API for simple web application with single endpoint to get calculated commissions of bank's users transaction.
A whole application has been written in Kotlin and requires jdk17.

## Run Application 
  There are two ways to start application:
  
   1. From IntelliJ
   2. From docker
  
 ### *IntelliJ* 
  
   1. Download a zip or open a project from Version Controll in IntelliJ
   
   2. In Run -> Edit Configuration set a profile to run mongodb on localhost.
      - in Build and run section add  to VM Options: -Dspring.profiles.active=local
   ![image](https://user-images.githubusercontent.com/71899548/174598766-990dc1d7-14f3-497b-8179-e63bd93ba420.png)
    
   3. In terminal enter a command `docker-compose up -d` to get mongoDB and mongo-express (access on localhost:8081)
    
   4. Run application with `Shift + F9`
     
  ### *Docker*
   
   Application can be run from docker image.
    
   1. Download a zip file from GitHub repository and unpack it.
   
   2. Go to directory of unpacked project and run a terminal.
   
   4. Run docker-compose file with a command `docker-compose up -d`
       - You can also build an image by yourself with command `docker build -t recruitmenttask:latest .`  Remember that you need to be in right directory!
    
   
   5. Pull an image from my dockerHub by typing a command `docker pull maciejlotysz/recruitmenttask`
   
   6. Run docker container with a command `docker run --name task01mlotysz -v csv_files:/src/main/resources/files/:ro -d -p 8080:8080 --network="mongodb_network" maciejlotysz/recruitmenttask:latest`
    
   Application is running :)
   
   ## Test API
   
   To test API amd GET requests you can use:
   
   ### *Postman*
   
   - GET localhost:8080/fees/calculate-commission?customerId=1
      * In Params set KEY = `customerId`. Value = Array of comma separated Strings
      * Authorization - list of users&password is in application.yml file
       
   ### *Swagger (OpenAPI)*
   
   - access http://localhost:8080/swagger-ui.html
      * list of users&password is in application.yml file
   
   ## Run Tests
   
   There are two possible ways to run tests:
   
   1. With IntelliJ
   2. From Terminal

   ### *IntelliJ*
   
   Simply go to src/test then to TransactionFeeControllerTest.class and CommissionServiceTest.class  and run test in both classes with `Ctrl + Shift + F10`
   
   ### *Terminal*
   
   Make sure you have installed gradle on your local machine.
   
   1. To run test in TransactionFeeControllerTest.class enter a command `./gradlew test --tests TransactionFeeControllerTest`

   2. To run test in CommissionServiceTest.class enter a command `./gradlew test --tests CommissionServiceTest`
   
