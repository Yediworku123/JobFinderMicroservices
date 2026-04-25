# 💼 Job Finder Microservices Platform

A full-scale microservices-based job application system built with Spring Boot, event-driven architecture, and secure authentication.

---

## 🚀 Features

* 🔐 Authentication & Authorization via Keycloak
* 📄 Job posting and management
* 📥 Job application with file upload (MinIO)
* 📧 Email notifications on application events
* ⚡ Event-driven communication using Kafka
* 🌐 API Gateway with JWT relay

---

## 🏗️ Architecture

User Request → API Gateway → Microservices → Database
↓
Kafka Events
↓
Notification Service → Email

---

## 🧩 Services

* Profile Service (User & Company Management)
* Job Service (Job Posting & Search)
* Application Service (Apply + File Upload)
* Notification Service (Kafka Consumer + Email)
* API Gateway (Routing + Security)

---

## ⚙️ Tech Stack

* Java + Spring Boot
* Kafka (Event Streaming)
* PostgreSQL (Database)
* MinIO (File Storage)
* Keycloak (Authentication)
* Docker (Containerization)

---

## ▶️ How to Run

### 1. Clone the repo

```bash
git clone https://github.com/your-username/job-finder-microservices.git
cd job-finder-microservices
```

### 2. Setup environment variables

Create a `.env` file:

```
KEYCLOAK_URL=http://localhost:8280
KEYCLOAK_SECRET=your-secret
```

### 3. Run with Docker

```bash
docker-compose up --build
```

---

## 📬 Event Flow Example

1. User applies for a job
2. Application Service saves data
3. Kafka publishes `APPLICATION_SUBMITTED`
4. Notification Service consumes event
5. Email sent via SMTP

---

## 📌 Status

✅ Fully functional microservices system
✅ End-to-end event-driven flow working
✅ Secure authentication implemented

---

## ✨ Author

Built by Yedi Worku 💻
