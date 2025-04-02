# Car Selling Platform API (Spring Boot)


## Overview

This is a backend API for a car selling platform built using **Spring Boot**. The platform allows users to post cars for sale, view car listings and manz more. It also includes premium features such as statistics tracking, currency conversion, and a profanity filter for posted cars. The platform includes various functionalities like role-based access control (RBAC), Kafka event streaming, and integration with Azure services.

The goal of this project is to demonstrate my coding skills, ability integrate new technologies, and provide a functional backend application with various features.

---

## Technologies Used

- **Payment Integration**: Stripe (for handling premium payments)
- **External APIs**: 
  - Currency API (for live currency exchange rates): https://api.exchangeratesapi.io/v1
  - GeoDB (for city and country validation): https://wft-geo-db.p.rapidapi.com/v1
  - Profanity API: https://www.purgomalum.com/service/json

- **Backend**: Spring Boot (Java)
- **Authentication**: JWT (JSON Web Tokens) (for secure user authentication)
- **Event Streaming**: Kafka (for real-time data streaming)
- **Caching**: Redis (for dynamic currency conversion and statistics tracking)
- **Database**: Azure SQL Database (cloud-based for data storage)
- **Cloud Services**: Azure (SQL Database, Blob Storage)
- **Email Service**: FreeMarker (for templated emails)
- **API Documentation**: Swagger (for API exploration and testing)
- **Containerization**: Docker, Docker Compose (for local setup and deployment)

---

## Features

### **Docker Support**
The application is containerized using **Docker** with a **Docker Compose** file. This allows for an easy local setup, making it convenient to run and test the application in a consistent environment.

### **Azure Integration**
The platform uses **Azure SQL Database** for data storage and **Azure Blob Storage** for storing images. It is designed to be easily deployable to an **Azure Kubernetes Cluster** for scalable deployment.

### **Stripe Payment Integration**
The platform uses **Stripe** to handle payments for premium features. Users can purchase premium features such as:
- Viewing statistics on car listings (e.g., how many times a listing has been viewed).
- Accessing additional filtering options.

### **Statistics**
A premium feature that tracks how many times a car listing has been viewed using **Kafka** and **Redis**. This feature provides real-time data processing and valuable insights for users who purchase the premium plan.

### **User Authentication (JWT)**
Utilizes **JWT (JSON Web Tokens)** for secure user authentication with **Spring Security**. Each request carries a JWT token, ensuring that only authorized users can access specific resources.

### **Role-Based Access Control (RBAC)**
Implements **RBAC**, including different access levels for:
- **Administrators**: Manage user roles and moderate listings.
- **Managers**: Review and ban inappropriate listings.
- **Users**: Post car listings and access basic features.

### **Profanity Filter**
A custom profanity filter checks car descriptions when they are posted. Users are given three attempts to pass the filter. If they fail, the listing is sent to a **moderator** for further review and potential banning.

### **Premium Features**
Users can purchase a premium plan that grants access to additional features, such as:
- **Viewing statistics** of car listings (e.g., views count).
This provides a business model for monetization and enhances the user experience.

### **Pagination**
The API supports pagination for car listings, improving performance and making it easier for users to browse through a large set of data.

### **Swagger Documentation**
API documentation is generated using **Swagger** for easy exploration of the available endpoints. This allows developers to test and interact with the API without needing a front-end.

### **Email Notifications**
Email notifications are sent using **FreeMarker templates** to notify users of important actions, such as:
- Successful car listings.
- Premium feature purchases etc.

### **Car Listing**
Users can post cars for sale, including detailed information such as:
- **Brand**
- **Model**
- **Price**
- **Horsepower**

Listings can be easily created and viewed by other users.

### **Filters**
Car listings can be filtered by:
- **Brand**
- **Model**
- **Horsepower**

Currency conversion is dynamically handled using **Redis**, providing a seamless experience for users from different countries.

---

## Setup Instructions

### Prerequisites

- Java 11 or higher
- Docker
- Maven
- Git
- Azure Account (for database and blob storage)

### Steps to Run Locally

1. **Clone the Repository**

   ```bash
   git clone https://github.com/tyndyk/autoria
   ```

2. **Build the Project**

   Run the following command to build the project:

   ```bash
   mvn clean install
   ```

3. **Configure Environment Variables**

   Set up your Spring profiles for different environments (dev, prod, etc.). The application will not start in production mode unless at least one **admin** and one **manager** have been registered. You need to provide the configuration for the **Azure SQL Database** and **Azure Blob Storage** in the `application.properties` or `application.yml` file.

   **Spring Profiles Example:**
   - **dev**: Local development profile
   - **prod**: Production profile (requires admin and manager setup)

4. **Run the Application**

   You can run the project locally by executing:

   ```bash
   mvn spring-boot:run
   ```

   Alternatively, to run using Docker:

   1. Build the Docker image:

      ```bash
      docker build -t car-selling-platform .
      ```

   2. Run the Docker container:

      ```bash
      docker-compose up
      ```

---

## Special Setup for Production Mode

- **Admin and Manager Role Setup**: In production mode, the application will require that at least one user be assigned the **admin** role and at least one user be assigned the **manager** role before it can start. The **admin** user has the ability to register new administrators and users, while the **manager** user has the role of moderating and reviewing car listings that have been flagged for profanity.
- **Spring Profiles**: Make sure to set up the correct Spring profiles (e.g., `application-prod.properties`) and configure your environment for production to ensure proper startup.

## Project Structure

Here’s a brief overview of the project structure:

```
src/
└── main/
    └── java/
        └── com/
            └── example/
                └── auto_ria/
                    ├── configurations/        # Configuration files for the project
                    ├── controllers/          # REST controllers and APIs
                    ├── dao/                  # Data access objects (repositories for data interaction)
                    ├── dto/                  # Data Transfer Objects for API communication
                    ├── enums/                # Enum classes defining fixed sets of values
                    ├── exceptions/           # Custom exceptions for error handling
                    ├── filters/              # Filters for request/response processing
                    ├── kafka/                # Kafka-related configuration and consumers
                    ├── mail/                 # Mail sending and related utilities
                    ├── models/               # Models representing core entities of the project
                    ├── security/             # Security-related configurations and classes (authentication, authorization)
                    ├── services/             # Service layer for business logic
                    └── setup/                # Initial setup and project configuration scripts
```
---

## Future Enhancements

- **Chat Feature**: Implement a real-time chat feature using WebSockets for buyer-seller communication.
- **Kubernetes Deployment**: Deployment using Kubernetes for better scalability and easier management of the application.
- **Advanced Filtering**: Enhance the filtering system with more complex search parameters, such as location-based search.

---

## Contact Information

- LinkedIn: https://www.linkedin.com/in/alina-tyndyk-701281282
- GitHub: https://github.com/tyndyk
- Email: alinatyndyk777@gmail.com
