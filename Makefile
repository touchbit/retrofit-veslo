b:
	mvn clean package

i:
	mvn clean install

t:
	mvn clean surefire-report:report
	open ./core/target/site/jacoco/index.html
	open ./jackson/target/site/jacoco/index.html
	open ./validator/target/site/jacoco/index.html

d:
	mvn clean deploy
