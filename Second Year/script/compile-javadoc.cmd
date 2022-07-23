cd ..\..

SET pack=info\kgeorgiy\java\advanced\implementor
SET dot-pack=info.kgeorgiy.java.advanced.implementor

SET modules=.\java-advanced-2022\modules
SET out=.\java-advanced\javadoc

SET kgeorgiy-pack=%modules%\%dot-pack%\%pack%
SET impl=.\java-advanced\java-solutions\info\kgeorgiy\ja\alekseev\implementor\Implementor.java

javadoc -private %impl% %kgeorgiy-pack%\ImplerException.java %kgeorgiy-pack%\JarImpler.java %kgeorgiy-pack%\Impler.java -d %out%
