ifneq ($(MAKECMDGOALS),$(findstring $(MAKECMDGOALS),build-doc-image run-doc-image push version))
    VERSION := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
else
	ifneq (version,$(firstword $(MAKECMDGOALS)))
		VERSION := latest
	endif
endif
$(eval $(VERSION):;@:)

SHELL=/bin/bash -o pipefail

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
	cd ./example && mvn versions:use-latest-versions -DgenerateBackupPoms=false

ver:
	mvn versions:set -DnewVersion=${VERSION}
	mvn install