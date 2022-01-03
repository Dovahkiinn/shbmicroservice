# SHB Handelsbanken demo microtjänst
How to run and test

* Get the zipped files or clone the repository.
      git clone https://github.com/Dovahkiinn/shbmicroservice.git
* You will need AT LEAST java version 14 as your JAVA_HOME ddirectory
* You need Maven. 
* Go to the root directory and run this from command line 
                     mvn spring-boot:run
* Verify that In Memory database by opening http://localhost:8080/h2-ui/login.jsp is working. 
![image](https://user-images.githubusercontent.com/16741284/147893630-b7689fa6-45c3-4510-a3b4-02b8ebb89571.png)
* Swagger docs here http://localhost:8080/v2/api-docs
* Swagger UI here http://localhost:8080/swagger-ui.html
* Use Swagger UI to create the account, see all the accounts for a perticular user or see one specific account
* TODO: Transactions assignment. 
