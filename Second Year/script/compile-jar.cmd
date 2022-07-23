SET pack=info\kgeorgiy\java\advanced\implementor
SET dot-pack-jar=info.kgeorgiy.java.advanced.implementor.jar

SET artifacts=..\..\..\java-advanced-2022\artifacts
SET impl=..\..\java-solutions\info\kgeorgiy\ja\alekseev\implementor\Implementor.java
SET script=..\..\script

mkdir temp
cd temp

jar xf %artifacts%\%dot-pack-jar% %pack%\Impler.class
jar xf %artifacts%\%dot-pack-jar% %pack%\JarImpler.class
jar xf %artifacts%\%dot-pack-jar% %pack%\ImplerException.class
javac -cp %artifacts%\* %impl% -d .

jar cfm %script%\Implementor.jar %script%\MANIFEST.MF .

cd %script%
rmdir /s /q temp
