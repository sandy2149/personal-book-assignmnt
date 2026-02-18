# Personal Book List with Google Books Integration

## Context
You have a Spring Boot project with a REST endpoint `/books` that returns
all books from an in-memory H2 database. The code includes a `Book` entity,
`BookRepository`, `BookController`, and a Google Books integration (`/books/google`)
exposing a search that returns the upstream Google schema via `GoogleBookService`.

## Using the Google Books API
* Base URL: `https://www.googleapis.com/books/v1`
* Search endpoint: `GET /volumes?q={query}` (e.g., `q=effective+java`, optional `maxResults`, `startIndex`).
* Volume details endpoint: `GET /volumes/{id}` to fetch a single book by Google volume ID.
* This project uses `GoogleBookService` with a configurable base URL. Set
  `google.books.base*url` in `application.properties` (or override in tests)
  to point to the real API or a mock server. The search route is exposed as
  `GET /google?q={query}` returning the upstream Google schema.

## Task
Implement the following, with accompanying tests for each change (tests are
mandatory):

1. Add a new REST endpoint that takes a Google Books volume ID as a parameter
   and adds the book to your personal list.

   * Endpoint: `POST /books/{googleId}` (path variable `googleId`).
   * Behavior:
       * Fetch the book details from the Google Books API (via `GoogleBookService`).
       * Map appropriate fields from `GoogleBook` to your `Book` entity
       (e.g., id, title, first author, pageCount).
       * Persist the mapped `Book` using `BookRepository`.
       * Return `201 Created` with the persisted `Book` in the response body.
   * Tests (Spring Boot tests):
       * Happy path: valid `googleId` returns 201 and persists the book with
       correct fields.
       * Error path: invalid or missing upstream data returns an appropriate
       error (e.g., `400 Bad Request`), and nothing is persisted.
       * Prefer mocking the downstream (e.g., MockWebServer/WireMock) to avoid
       flakiness; a smoke test hitting the real API is optional.

2. Keep existing functionality intact and verify:

   * The existing `GET /books` endpoint still returns all persisted books.
   * The Google search endpoint `/google` continues to return the
     Google schema payload as-is.
   * Tests should seed data where needed and assert on JSON responses and
     status codes.

You may refactor or add code as needed, but keep the existing structure.
Aim to complete within 30 minutes.

## Implementation Details

The assignment requirements have been implemented as follows:

### New Endpoint Implemented

**POST /books/{googleId}**

- Fetches book details from Google Books API using `/volumes/{id}`.
- Maps relevant fields:
  - id
  - title
  - first author
  - pageCount
- Persists book into H2 database.
- Returns `201 Created` with persisted book.

### Error Handling

- Duplicate book → `400 Bad Request`
- Invalid Google volume ID → `400 Bad Request`
- Upstream 404 errors are converted into business exceptions.
- Global exception handler ensures consistent API responses.

### Existing Endpoints Preserved

- `GET /books` returns all persisted books.
- `GET /google?q={query}` returns upstream Google schema unchanged.

### Integration Testing

- Integration tests implemented using `@SpringBootTest`.
- MockWebServer used to mock Google API for stable testing.
- Database cleaned between tests.
- Both happy path and error path covered.

## How to Run

### Build
mvn clean install

### Run
Application runs at : http://localhost:8080

### Add book
POST /books/ka2VUBqHiWkC

### Get all books
GET /books

### Search Google
GET /google?q=effective+java

## DB
- H2 database is in-memory and resets on restart.
- Google base URL configurable via:
-    google.books.base-url

