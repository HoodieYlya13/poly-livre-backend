.PHONY: build up down logs clean setup_env

setup_env:
	./setup_env.sh

build: setup_env
	docker-compose build

up: setup_env
	docker-compose up -d

down:
	docker-compose down

logs:
	docker-compose logs -f

clean:
	docker-compose down -v
	mvn clean