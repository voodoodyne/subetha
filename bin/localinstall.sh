./bin/mvn  install:install-file -Dfile=lib/dist/subethasmtp-UNVERSIONED.jar -DgroupId=org.subethamail -DartifactId=subetha-smtp -Dversion=UNVERSIONED -Dpackaging=jar

./bin/mvn  install:install-file -Dfile="$(cygpath -wa /projects/resin/resin-4.0.25/lib/resin.jar)" -DgroupId=com.caucho -DartifactId=resin -Dversion=4.0.25 -Dpackaging=jar

