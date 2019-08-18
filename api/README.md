# Adventureland API

The Adventureland API is a REST-based application programming interface for
managing and playing Adventureland-based games.

[Running the API on AWS](aws.md)

[Adventureland API Reference](api.md)

## Running Local

Using Docker, start a MySQL 5.7 server:

    docker run --name mysql1 -p 3306:3306 -e MYSQL_ROOT_PASSWORD=my-secret-pw -e MYSQL_ROOT_HOST=% -e MYSQL_DATABASE=ebdb -d mysql:5.7 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci 

which does the following:
 * sets the root password
 * allows connects from remote clients
 * creates a database named `ebdb`
 * sets the server's default character encoding to `utf8mb4` (Read [this](https://medium.com/@adamhooper/in-mysql-never-use-utf8-use-utf8mb4-11761243e434) to understand why)
 

Next, from the project root folder, run the API Spring Boot application:

    mvn -pl api spring-boot:run -DRDS_PASSWORD=my-secret-pw