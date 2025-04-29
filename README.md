# Sketchboard


## 📋 Table of Contents

- [Team Members](#team-members)
- [Mentor](#mentor)
- [Project Description](#project-description)
- [Live Demo](#live-demo)
- [Technology Stack](#technology-stack)
- [Database Setup](#database-setup)
- [Steps to Run](#steps-to-run)
- [Demo Account for Testing](#demo-account-for-testing-api-endpoints)
- [Data Flow Diagram](#data-flow-diagram)
- [Database Design](#database-design)
- [Resource](#resource)
- [Bug Report](#bug-report)
- [Feature Request](#feature-request)

## Team Members

| Name                   | Role        | GitHub Profile                                     |
|------------------------|-------------|---------------------------------------------------|
| Asifur Rahman       | Team Leader | [asif17r](https://github.com/asif17r)               |
| Souvik Sanyal       | Member      | [souvik00](https://github.com/souvik00)             |
| Sanjoy Kumar        | Member      | [sanjoykumar-s](https://github.com/sanjoykumar-s)   |


## Mentor

- [Piash-Haque](https://github.com/Piash-Haque)

## Project Description
Sketchboard is the team name for our project, Lost and Found. This project aims to create a platform where users can report lost items and search for found items. It helps in connecting people who have lost their belongings with those who have found them, making the process of recovering lost items more efficient and organized.

## Live Demo
<a href="http://lostnfoundbd.duckdns.org/" title="Live Demo">
  <img src="https://imgs.search.brave.com/cBG_utGw1E4d3AK61O-BD3O9TWSP1dM5ZRl6Mhal7iE/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jZG4u/cHJvZC53ZWJzaXRl/LWZpbGVzLmNvbS81/ZTBhNWQ5ZDc0MzYw/OGQwZjNlYTY3NTMv/NjVmYzQ1YzVmMWZh/MjE2MmZhNzE1Yjkx/X19PcGVuJTIwTGlu/a18uc3Zn" width="24" /> Visit
</a>



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
   
### Demo account for testing API endpoints
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

| Name                   | Role        | GitHub Profile                                     |
|------------------------|-------------|---------------------------------------------------|
| Asifur Rahman       | Team Leader | [asif17r](https://github.com/asif17r)               |
| Souvik Sanyal       | Member      | [souvik00](https://github.com/souvik00)             |
| Sanjoy Kumar        | Member      | [sanjoykumar-s](https://github.com/sanjoykumar-s)   |


## Mentor

- [Piash-Haque](https://github.com/Piash-Haque)

## Data Flow Diagram

 ### Level0 DFD:
 ![dfd0](Docs/dfd0.jpg)

 ### Level1 DFD:
 ![dfd1](Docs/dfd1.png)
 
##  Database Design

The ER diagram illustrates our relational database structure,

![ER Diagram](Docs/ER_Diagram.png)

## Resource 
- [Project Planning](https://github.com/orgs/Learnathon-By-Geeky-Solutions/projects/115)
- [Project Proposal](https://docs.google.com/presentation/d/15ELny6vCHERjQ6sfL45u-4FKbRbK7ax-UIAIhu9b7XM/edit?slide=id.p#slide=id.p)

 
## Bug Report
---
name: Bug report
about: Create a report to help us improve
title: ''
labels: ''
assignees: ''

---

**Describe the bug**
A clear and concise description of what the bug is.

**To Reproduce**
Steps to reproduce the behavior:
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

**Expected behavior**
A clear and concise description of what you expected to happen.

**Screenshots**
If applicable, add screenshots to help explain your problem.

**Desktop (please complete the following information):**
 - OS: [e.g. iOS]
 - Browser [e.g. chrome, safari]
 - Version [e.g. 22]

**Smartphone (please complete the following information):**
 - Device: [e.g. iPhone6]
 - OS: [e.g. iOS8.1]
 - Browser [e.g. stock browser, safari]
 - Version [e.g. 22]

**Additional context**
Add any other context about the problem here.
## Feature Request
---
name: Feature request
about: Suggest an idea for this project
title: ''
labels: ''
assignees: ''

---

**Is your feature request related to a problem? Please describe.**
A clear and concise description of what the problem is. Ex. I'm always frustrated when [...]

**Describe the solution you'd like**
A clear and concise description of what you want to happen.

**Describe alternatives you've considered**
A clear and concise description of any alternative solutions or features you've considered.

**Additional context**
Add any other context or screenshots about the feature request here.
