.PHONY: build up down logs clean mail-up mail-logs help

build:
	docker compose build

up:
	docker compose up -d

down:
	docker compose down

logs:
	docker compose logs -f

clean:
	docker compose down -v
	mvn clean

mail-up:
	docker compose up -d mailpit

mail-logs:
	docker compose logs -f mailpit

help:
	@echo "Usage: make [target]"
	@echo ""
	@echo "Targets:"
	@echo "  build       Build the docker images"
	@echo "  up          Start all services in detached mode"
	@echo "  down        Stop all services"
	@echo "  logs        Follow logs for all services"
	@echo "  clean       Stop services, remove volumes, and clean maven target"
	@echo "  mail-up     Start only the mailpit service"
	@echo "  mail-logs   Follow logs for the mailpit service"
	@echo "  help        Show this help message"