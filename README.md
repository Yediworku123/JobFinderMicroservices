# JobFinder Microservices

A backend microservices-based job platform built using Spring Boot, Docker, and Kafka.  
The system is designed to handle job postings, applications, user profiles, and notifications through distributed services.

---

## 🚀 Architecture

This project follows a microservices architecture with the following services:

- **API Gateway** – Entry point for all requests
- **Profile Service** – Manages user profiles (job seekers & companies)
- **Job Service** – Handles job postings and job-related operations
- **Application Service** – Manages job applications
- **Notification Service** – Sends notifications via Kafka events

---

## 🧰 Tech Stack

- Java / Spring Boot
- Spring Cloud (Feign, Gateway)
- Kafka (Event-driven communication)
- Docker & Docker Compose
- REST APIs
- Maven

---

## ⚙️ How to Run

### 1. Clone the repository
```bash
git clone https://github.com/Yediworku123/JobFinderMicroservices.git
cd JobFinderMicroservices
