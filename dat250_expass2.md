DAT250 Experimental Assignment 2 - Report

Here are some of the issues I've encountered whilst working with this project:

1. IntelliJ Gradle Sync Issues
   After moving the project folder manually in the file explorer, IntelliJ failed to sync the project due to missing module references. The issue was resolved by:
- Removing broken module references via `File > Project Structure > Modules`.
- Reimporting the project using `File > Open` and selecting the correct folder.
- Recreating the `.iml` file and ensuring the Gradle build system was properly configured.

2. Missing PollManager Bean
   Spring Boot failed to autowire the `PollManager` component in the test class. This was resolved by:
- Ensuring `PollManager` was annotated with `@Component`.
- Verifying that the package structure allowed Spring to scan the class.
- Using `@SpringBootApplication` in the main class to enable component scanning.

VoteOption Equality in Tests
A test failed when deleting a poll did not remove associated votes. The issue was due to `VoteOption` objects not being recognized as equal. This was resolved by:
- Implementing `equals()` and `hashCode()` in the `VoteOption` class.
- Updating the `pollContainsOption()` method to correctly compare vote options using `Set.stream().anyMatch(...)`.


Pending Issues

- Mockito warning about dynamic agent loading was shown during test execution. This does not affect current functionality but may require configuration updates in future JDK versions.
- The test scenario assumes that votes are tied to poll options, but more robust linking between votes and polls could be implemented for better integrity.