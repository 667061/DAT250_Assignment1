# DAT250 – Experiment Assignment 5

## Technical Problems Encountered

During this assignment I integrated Redis (and partly Valkey) into the poll application to handle session tracking and caching of poll results. While the task description mentioned MongoDB, my main focus was on Redis. Some of the issues I faced were similar in nature to what one might expect when setting up any new database technology:

- **Installation and connectivity**: Ensuring that the Redis server was running locally and that the Java Jedis client could connect to `localhost:6379`. At first, connection attempts failed because the server was not started, but this was resolved by verifying the service status and configuration.
- **Data modelling challenges**: Deciding how to represent complex objects such as polls and vote options in Redis. I experimented with both storing captions directly and using option IDs as hash fields. I concluded that using option IDs was safer to avoid collisions, but it required mapping back to captions when presenting results.
- **Circular dependencies in entities**: My JPA entities (`User → Poll → VoteOption → Vote → User`) created infinite recursion when serializing to JSON. This caused `StackOverflowError` at runtime. I resolved this by adding `@JsonManagedReference` / `@JsonBackReference` annotations and excluding recursive fields from Lombok’s `toString()`.

## Pending Issues

- **Vote uniqueness**: I implemented logic to ensure that a user can only vote once per poll, and that changing a vote decrements the old option and increments the new one. While this works, it could be further refined to handle edge cases (e.g. concurrent updates).
- **Cache invalidation**: I added cache invalidation on new votes by deleting the Redis hash for poll results. A more sophisticated approach (e.g. partial updates or pub/sub notifications) could be explored in the future.
- **MongoDB**: The assignment text mentions MongoDB, but in my implementation I focused on Redis. I did not encounter MongoDB‑specific issues in this round, but if MongoDB were to be integrated, similar installation and connection challenges would be expected.

---

Overall, the main learning outcome was understanding how to use Redis data structures (Sets and Hashes) to manage session state and cached aggregates, and how to integrate them into a Spring Boot application with Jedis.
