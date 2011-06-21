# Clears test database

mvn dbmaintain:createDatabase

# Clears prod database

mvn -Pprod dbmaintain:createDatabase

# Updates test database and starts from scratch on errors

mvn dbmaintain:updateDatabase

# Update prod database. Halts on errors

mvn -Pprod dbmaintain:updateDatabase

# Creates archive for SLS

mvn -Psls dbmaintain:createScriptArchive

# Creates archive for SKS

mvn -Psks dbmaintain:createScriptArchive