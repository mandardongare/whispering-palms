set location=%cd%
call mvn install:install-file -Dfile=%location%\ojdbc8.jar -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=12.2.0.1 -Dpackaging=jar
pause
