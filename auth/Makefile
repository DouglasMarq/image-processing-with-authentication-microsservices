GRADLE:=./gradlew

DOCKER_NET_CONFIG?=$(if $(filter Darwin,$(shell uname -s)),-p 8080:8080 -p 4000:4000, --net host -p 8080:8080 -p 4000:4000)

format:
	$(GRADLE) spotlessApply

format/check:
	$(GRADLE) spotlessCheck

build:
	$(GRADLE) build -x test -x spotlessCheck

build/docker: build
	docker build -t douglasmarq/auth-service:latest .

test:
	./gradlew test
