# Sky Survey API

This is a REST API for  survey platform. Admins can create and manage surveys and questions. Users can view open surveys, fill them in, and upload PDF certificates. Admins can then view the answers, search them by email, and download the uploaded files.

The main idea of the design is that surveys and questions are data, not code. When an admin adds a new survey or a new question, nothing in the code changes. The API reads the rules for each question from the database and uses them to validate answers and build responses.

All API responses are in XML, as required by the task.

## Features

- Create, view, edit and delete surveys
- Activate and deactivate surveys
- Add, edit and delete questions, with six question types: short text, long text, email, single choice, multiple choice and file upload
- Add, edit and remove options for choice questions through the question edit
- A public list of open surveys and their active questions for respondents
- Submit a response with text answers and PDF uploads in one request
- View submitted responses with paging and an email search
- Download any uploaded certificate by its id


## Technologies Used

- Java 21
- Spring Boot 4.1 with Spring Web MVC and Spring Data JPA
- Hibernate as the JPA provider
- PostgreSQL 16, running in Docker
- Jackson XML 
- Lombok 
- Maven for the build
- Docker Compose for the database
- Postman for testing

## Prerequisites

Install these before you start:

- JDK 21
- Maven
- Docker Desktop (it will run PostgreSQL for you, so you do not need to install Postgres yourself)
- Postman
- Git

## Installation

1. Clone the project and enter the folder:

```bash
git clone 
cd simple-survey-api
```

2. Start the database:

```bash
docker compose up -d
```

This starts PostgreSQL, creates the `sky_survey_db` database, and runs the SQL script that builds all the tables. The script only runs the first time, on a fresh volume.

3. Settings. The app reads its database settings from environment variables, with these defaults:

| Variable | Default |
|---|---|
| DB_HOST | localhost |
| DB_PORT | 5432 |
| DB_NAME | sky_survey_db |
| DB_USER | postgres |
| DB_PASSWORD | postgres |



If the database ever gets into a bad state, you can reset it completely with:

```bash
docker compose down -v
docker compose up -d
```

This wipes all data and rebuilds the tables from the script.

## Running Locally

Start the app:

```bash
mvn spring-boot:run
```

The API runs on port 8080.

Quick check that it works: open Postman and send

```
GET http://localhost:8080/api/surveys
```

On a new database you should get an empty `<surveys/>` response with status 200.



## Database

- ERD diagram: `sky-survey-api\db\sky_survey_ERD.jpeg`
- SQL script: `sky-survey-api\db\sky_survey_db.sql` 

The database has 7 tables. On the survey side: `surveys`, `questions` and `options`. On the answer side: `responses`, `answers`, `answer_options` and `uploaded_files`.

A few points about the shape:

- The `questions` table holds all six question types in one table. Each type uses its own set of nullable settings columns, for example `max_length` for text questions and `file_format` for file questions.
- `answer_options` is a join table with a  primary key made of `answer_id` and `option_id`. It records which options a choice answer picked.
- Every main table has a `deleted_at` column. Deleting a record sets this timestamp instead of removing the row.

## API Overview

| Method | Path | What it does |
|---|---|---|
| POST | /api/surveys | Create a survey |
| GET | /api/surveys | List all surveys (admin) |
| GET | /api/surveys/{id} | Get one survey |
| PUT | /api/surveys/{id} | Edit a survey |
| PATCH | /api/surveys/{id}/activate | Activate a survey |
| PATCH | /api/surveys/{id}/deactivate | Deactivate a survey |
| DELETE | /api/surveys/{id} | Delete a survey (soft) |
| GET | /api/surveys/available | List open surveys for respondents |
| POST | /api/surveys/{surveyId}/questions | Add a question |
| GET | /api/surveys/{surveyId}/questions | List a survey's questions. Respondents get active questions only. Add `?includeInactive=true` for the admin view 
| PUT | /api/surveys/{surveyId}/questions/{id} | Edit a question, including its options |
| PATCH | /api/surveys/{surveyId}/questions/{id}/activate | Activate a question |
| PATCH | /api/surveys/{surveyId}/questions/{id}/deactivate | Deactivate a question |
| DELETE | /api/surveys/{surveyId}/questions/{id} | Delete a question (soft) |
| POST | /api/surveys/{surveyId}/responses | Submit a response (multipart form data with file uploads) |
| GET | /api/surveys/{surveyId}/responses | List responses. Supports `?page=`, `?pageSize=` and `?email=` |
| GET | /api/certificates/{id} | Download an uploaded certificate |

Sample requests and saved sample responses for every endpoint are in the Postman collection. See the next section.

## Testing with the Postman Collection

The collection file is in the repo at `sky-survey-api\postman\Sky Survey API's.postman_collection.json` 

1. Open Postman, click Import, and drop the file in.
2. Check the collection variable `baseUrl`. It should be `http://localhost:8080`.
3. Open the Collection Runner and run the whole collection from top to bottom. Each folder creates its own test data, so the order just works.



## Assumptions

**User identification.**

Respondents are identified by email only. There are no user accounts
for respondents. The same person can submit more than once.


**Accessing pages.**

Admin actions do not need a login. The project takes admin
and respondent actions as open, so every endpoint is reachable without
logging in. Adding an authenticationa and authorisation will ensure both admins and users can only access what is meant for them.

**Caching for performance.**

The system is write heavy, not read heavy. I expect many respondents
submitting and admins checking results now and then. If read traffic grows, the survey list
and the public question list will be the first things to cache.

