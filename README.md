---

# Spring Boot JMR  E-commerce Backend

This is a backend project built with **Spring Boot**.
The goal is to provide a basic e-commerce style backend with authentication, product management, and order handling. I’m using this project to practice microservices, Docker, and deployment on AWS.

---

## Tech Stack

* Java 17
* Spring Boot
* Spring Data JPA (Hibernate)
* MySQL
* Spring Security + JWT
* Docker (for containerization)
* Redis & Kafka (to be added later)

--

## What’s Inside

* **auth-service** → handles user signup/login with JWT
* **product-service** → product CRUD APIs
* **order-service** → order placement APIs
* **eureka-server** → service discovery
* **api-gateway** → routes requests and checks JWT

---

## How to Run

1. Clone the repo:

   ```bash
   git clone https://github.com/your-username/springboot-ecommerce.git
   cd springboot-ecommerce
   ```

2. Update your MySQL config in `application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   ```

3. Build and run:

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. (Optional) Run with Docker:

   ```bash
   docker-compose up -d
   ```

---

## Sample APIs

**Auth Service**

* `POST /auth/signup` – create a new user
* `POST /auth/login` – login and get JWT

**Product Service**

* `GET /products` – list products
* `POST /products` – add a product

**Order Service**

* `POST /orders` – place a new order

---

## Notes

* This project is still in progress, I’ll keep adding more features (like Redis caching and Kafka for events).
* Mainly built for learning and practice, but can be extended into a real-world project.

