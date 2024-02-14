cd "..\"

set modid=smallstairs

git fetch origin
timeout /T 1
call gradlew.bat build
call gradlew.bat generatePomFileForMavenCommonPublication
call gradlew.bat generatePomFileForMavenForgePublication
call gradlew.bat generatePomFileForMavenFabricPublication
copy /y ".\common\build\publications\mavenCommon\pom-default.xml" ".\common\build\publications\mavenCommon\%modid%.pom"
copy /y ".\fabric\build\publications\mavenFabric\pom-default.xml" ".\fabric\build\publications\mavenFabric\%modid%.pom"
copy /y ".\forge\build\publications\mavenForge\pom-default.xml" ".\forge\build\publications\mavenForge\%modid%.pom"

cd ".\php"

start upload_maven.bat
pause