# Sketchboard

## Project Description
Sketchboard is the team name for our project, Lost and Found. This project aims to create a platform where users can report lost items and search for found items. It helps in connecting people who have lost their belongings with those who have found them, making the process of recovering lost items more efficient and organized.

## Technology Stack

- **Backend:** Spring Boot
- **Frontend:** React.js ([Repository](https://github.com/asif17r/lostnfound-frontend)) *(This frontend is for testing purposes only; the deployment frontend may differ significantly.)*

- **Database:** PostgreSQL
- **Containerization:** Docker
- **Build Tool:** Maven
- **Version Control:** Git

### Database Setup

The database will be created and configured automatically during application start-up using the spring-docker-compose dependency.

### Steps to Run

1. **Clone the Repository**
    ```bash
    git clone git@github.com:Learnathon-By-Geeky-Solutions/sketchboard.git
    cd sketchboard
    ```
2. **Run the application**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
   
### Demo account for testing [API endpoints](https://precision-multiple-tie-bomb.trycloudflare.com)
- Bearer Token
   ```
   eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huZG9lQGdtYWlsLmNvbSIsImlhdCI6MTc0MjUwNTc2NywiZXhwIjo5MjIzMzcyMDM2ODU0Nzc1fQ.dI4cK-rf-OPvOboWBmUi8HFdQuEaTJSyQlvzpzZrTobEzckxJTGeshsuBlJVxVkV
   ```
- Or you can use login credentials
   ```bash
   Email: johndoe@gmail.com
   Password: password
   ```

## Team Members

- [asif17r](https://github.com/asif17r) (Team Leader)
- [sanjoykumar-s](https://github.com/sanjoykumar-s)
- [souvik00](https://github.com/souvik00)

## Mentor

- [Piash-Haque](https://github.com/Piash-Haque)
