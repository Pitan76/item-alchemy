cd "..\"

set modid=itemalchemy

git fetch origin
timeout /T 1
call gradlew.bat build
call gradlew.bat generatePomFileForMavenJavaPublication
copy /y ".\build\publications\mavenJava\pom-default.xml" ".\build\publications\mavenJava\%modid%.pom"

timeout /T 1

cd ".\php"

start upload_maven.bat
pause