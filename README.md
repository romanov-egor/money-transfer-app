# Money Transfer App
This project provides simple RESTful API to manage accounts and transfer money between them.

## Technology stack

* Jersey
* Hibernate
* Apache Tomcat (embedded)
* HSQLDB
* JUnit

## Build and Run Instructions

To build and run this application you will need:
* JDK 1.8 or higher
* Apache Maven 3.5.0 or higher

Don't forget to set `JAVA_HOME` and `M2_HOME` environment variables and add them to `PATH`.

Clone project with
```
    git clone https://github.com/romanov-egor/money-transfer-app.git
```
or download zip file and extract to any folder.

### To build application
Open command line, go to project folder and execute
```
    mvn clean package
```

### To run application
Build application, go to `target` folder
```
    cd target
```
And execute `jar-with-dependencies` file with Java
```
    java -jar money-transfer-app-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### To run tests
Open command line, go to project folder and execute
```
    mvn clean test
```

## API description
Description format:
```
    REQUEST_METHOD Path
    JSON (if required)
```

Get all accounts:
```
    GET /account
```

Get account by Id:
```
    GET /account/id
```

Create account:
```
    POST /account
    {
        "holderName": Account Holder Name,
        "balance": Account Balance
    }
```

Update existing account by Id:
```
    PUT /account
    {
        "id": Account Id,
        "holderName": Account Holder Name,
        "balance": Account Balance
    }
```

Delete existing account by Id:
```
    DELETE /account/id
```

Transfer money from one account to another:
```
    POST /transfer
    {
        "senderId": Sender Account Id,
        "recipientId": Recipient Account Id,
        "transferAmount": Amount of funds for transfer
    }
```