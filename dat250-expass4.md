DAT250 Experiment Assignment 4 – Report

During this experiment, I worked with Java Persistence Architecture (JPA) to model and persist entities such as users, polls, vote options, and votes. While setting up the application I encountered several technical challenges that required careful debugging and adjustments to the entity relationships.

Technical Challenges and Resolutions

One of the first issues I faced was a Detached entity passed to persist error. This occurred when persisting entities that referenced other entities already managed by the persistence context. For example, when persisting a Poll that referenced a User, Hibernate attempted to persist the User again, resulting in an exception. I resolved this by removing unnecessary cascade settings (such as CascadeType.ALL) from @ManyToOne relationships and ensuring that entities were created and persisted within the same transaction.

Another issue was a TransientPropertyValueException, which indicated that a Poll referenced a VoteOption that hadn’t been persisted. To fix this, I added cascading to the Poll.options relationship and made sure that each VoteOption had its poll field correctly set when added to a poll.

I also encountered a unique constraint violation in the join table between Vote and VoteOption. Initially, I had modeled this relationship as @ManyToMany, but this caused problems when multiple votes referenced the same option. Changing the mapping to a @ManyToOne from Vote to VoteOption, and a corresponding @OneToMany in VoteOption, resolved the issue and better reflected the intended data model.

Additionally, I had to align the entity mappings with the native SQL queries used in the test cases. For example, the test queried the users table and expected a column named id, while my entity used userID. I updated the @Table and @Id annotations to match the expected names, which allowed the queries to execute successfully.

Finally, I ran into a logic issue where the test expected two votes for a specific option, but only one was counted. This was due to incorrect presentationOrder values and unintended cascading that caused duplicate votes to be persisted. I fixed this by correctly setting the presentationOrder using options.size() and removing cascade settings from User.votes.

