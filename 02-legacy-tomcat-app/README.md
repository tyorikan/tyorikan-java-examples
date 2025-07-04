# legacy-tomcat-app

This is a sample legacy Java web application using Struts2 and Spring Framework, designed to be run on Apache Tomcat.

## Prerequisites

*   Java Development Kit (JDK) 8
*   Apache Maven
*   Docker

## Build

To build the application and create a `.war` file, run the following command:

```sh
mvn clean install
```

This will generate `target/legacy-tomcat-app.war`.

## Run

### Using Docker

1.  **Build the Docker image:**

    ```sh
    docker build -t legacy-tomcat-app .
    ```

2.  **Run the Docker container:**

    ```sh
    docker run -p 8080:8080 legacy-tomcat-app
    ```

### Accessing the Application

Once the application is running, you can access it in your web browser at the following URL:

[http://localhost:8080/legacy-tomcat-app/](http://localhost:8080/legacy-tomcat-app/)
