GRADLE:=./gradlew

DOCKER_NET_CONFIG?=$(if $(filter Darwin,$(shell uname -s)),-p 8080:8080 -p 4000:4000, --net host -p 8080:8080 -p 4000:4000)

format:
	$(GRADLE) spotlessApply

format/check:
	$(GRADLE) spotlessCheck

clean:
	$(GRADLE) clean

build: clean
	$(GRADLE) build
	$(GRADLE) spotlessCheck

build/docker: build
	docker build -t douglasmarq/bff-service:latest .

test/unitTest:
	./gradlew test

testIntegration:
	./gradlew integrationTest -x test