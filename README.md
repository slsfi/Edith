# edith

## Local development

You need MySQL, Java and Git to get started.

Insert this sweetness into your MySQL:

    CREATE DATABASE edith CHARACTER SET utf8 COLLATE utf8_swedish_ci;
    CREATE DATABASE edith_test CHARACTER SET utf8 COLLATE utf8_swedish_ci;
    CREATE USER 'edith'@'localhost' IDENTIFIED BY 'edith';
    GRANT ALL PRIVILEGES ON edith.* TO 'edith'@'localhost';
    GRANT ALL PRIVILEGES ON edith_test.* TO 'edith'@'localhost';

Clone the repository and do the basic Maven stuff:

    git@github.com:mysema/edith.git
    cd edith/
    mvn dbmaintain:updateDatabase
    mvn clean install
    mvn exec:java -Dexec.mainClass=com.mysema.edith.SLSStart -Dexec.classpathScope=test

Check `/doc/use-sls-data.txt` on how to install SLS data.

## SLS deployment

### QA (& acceptance test)
To deploy to Jelastic (our current QA and acceptance test environment):

Start by bumping version in pom.xml.

    git tag -a v2.0.0.rc7 -m 'Deployed into QA on 3.9.2013'

    mvn -Pjelastic clean package
    mvn -Pjelastic jelastic:deploy

You need to have set Mysema's Jelastic password in your `settings.xml`.

### Production

Start by bumping version in pom.xml.
Server is only accesible from Mysema VPN.
Password is "annotaatio"

    git tag -a v0.7.9 -m 'Deployed for SLS on 3.9.2013'
    mvn -Psls clean package
    scp target/edith.war timo@194.100.126.140:.
    ssh timo@194.100.126.140
    sudo service jetty stop
    sudo mv edith.war webapps

If necessary update the database.

    mvn -Psls dbmaintain:createScriptArchive
    scp target/edith.jar timo@194.100.126.140:.

Create backup before update, just in case.
Password is "Kuddnas10"

    mysqldump -u topeliusapp -p -h 192.168.0.7 --single-transaction topelius_notes > topelius_notes_dump_2014-1-9.sql
    gzip topelius_notes_dump_2014-1-9.sql    
    cd dbmaintain-2.4
    ./dbmaintain.sh updateDatabase ../edith.jar

TODO Svn backup, is that necessary sometimes?

## SKS deployment

SKS is currently not using the `master` branch, you need to checkout `edith_1`.

Start by bumping version in pom.xml.

    git tag -a v0.7.9 -m 'Deployed for SKS on 3.9.2013'
    mvn -Psks clean package
    scp target/edith.war root@128.214.12.107:/opt/jetty

    ssh root@128.214.12.107
    service jetty stop
    mv /opt/jetty/edith.war /opt/jetty/webapps/edith.war
    chown wwwrun /opt/jetty/webapps/edith.war
    service jetty start

Note that you need to be on the Mysema VPN.
 
