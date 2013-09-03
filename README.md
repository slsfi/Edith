# edith

## Local development

You need MySQL, Java and Git to get started.

    CREATE DATABASE edith CHARACTER SET utf8 COLLATE utf8_swedish_ci;
    CREATE DATABASE edith_test CHARACTER SET utf8 COLLATE utf8_swedish_ci;
    CREATE USER 'edith'@'localhost' IDENTIFIED BY 'edith';
    GRANT ALL PRIVILEGES ON edith.* TO 'edith'@'localhost';
    GRANT ALL PRIVILEGES ON edith_test.* TO 'edith'@'localhost';

    git@github.com:mysema/edith.git
    cd edith/
    mvn dbmaintain:updateDatabase
    mvn clean install
    mvn exec:java -Dexec.mainClass=com.mysema.edith.SLSStart -Dexec.classpathScope=test
 
