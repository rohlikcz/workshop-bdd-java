.PHONY: compose-up dependencies test

compose-up:
	docker-compose up -d

dependencies:
	./mvnw dependency:resolve compile

test: compose-up dependencies
	./mvnw clean test