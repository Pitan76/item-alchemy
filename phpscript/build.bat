cd "..\"

set modid=smallstairs

git fetch origin
timeout /T 1
call gradlew.bat build
call gradlew.bat generatePomFileForMavenJavaPublication
copy /y ".\build\publications\mavenJava\pom-default.xml" ".\fabric\build\publications\mavenFabric\%modid%.pom"

cd ".\php"

start upload_maven.bat
pause