b:
	mvn clean package

i:
	mvn clean install

t:
	mvn clean test

c:
	mvn clean test -Dcoverage
	open ./all/target/site/jacoco-aggregate/index.html

d:
	mvn clean deploy

bd:
	docker build --no-cache -t retrofit-veslo .

ex:
	mvn package -DskipTests=true
	cd ./example && mvn test -DskipTests=false -Dmaven.test.failure.ignore=true && mvn allure:serve

upv:
	mvn versions:use-latest-versions -DgenerateBackupPoms=false
