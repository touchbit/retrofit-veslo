b:
	mvn clean package

i:
	mvn clean install

t:
	mvn clean test
	open ./all/target/site/jacoco-aggregate/index.html

d:
	mvn clean deploy

bd:
	docker build --no-cache -t retrofit-dual-response-extension .
