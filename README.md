# Cinefiles Backend API

A robust, enterprise-ready Spring Boot REST API designed to manage movie metadata, user posts, and social interactions. This project bridges local data persistence with third-party data procurement to create a seamless, high-performance backend system.

## 🚀 Key Architecture & Features

* **Spring Boot REST API:** Decoupled architecture serving dynamic, structured JSON payloads to frontend clients.
* **Database Connection Pooling:** Implemented **HikariCP** to efficiently manage concurrent MySQL connections, preventing timeouts under high traffic.
* **Automated Data Procurement:** Integrated with the external **OMDb API**. The system intelligently intercepts search queries, fetches external movie metadata, and automatically caches it into the local relational database to drastically reduce future network latency.
* **Security First:** All sensitive database credentials and API keys are abstracted via environment variables.

## 🛠️ Tech Stack
* **Language:** Java (Core)
* **Framework:** Spring Boot
* **Database:** MySQL
* **Tools/Libraries:** HikariCP, Maven, JDBC

## ⚙️ How to Run Locally
1. Clone the repository: `git clone https://github.com/ankitdash-1407/CineFiles-Spring-Boot-API.git`
2. Ensure you have a local MySQL server running.
3. Set the following Environment Variables in your IDE or system:
   * `DB_URL`
   * `DB_USER`
   * `DB_PASSWORD`
   * `OMDB_API_KEY`
4. Run `BackendApplication.java` to start the Tomcat server on `localhost:8080`.
