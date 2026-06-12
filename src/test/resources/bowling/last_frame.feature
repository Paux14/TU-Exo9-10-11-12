Feature: Last Frame
  The last (10th) bowling frame with special rules

  Scenario: Score increases after second roll following strike in last frame
    Given a last frame
    And the generator is configured as:
      | max | returns |
      | 10  | 10      |
      | 10  | 5       |
    When the player makes 2 rolls
    Then the score should be 15

  Scenario: Second roll accepted after strike in last frame
    Given a last frame
    And the generator is configured as:
      | max | returns |
      | 10  | 10      |
      | 10  | 5       |
    When the player makes 2 rolls
    Then all rolls should have been accepted

  Scenario: Third roll accepted after strike in last frame
    Given a last frame
    And the generator is configured as:
      | max | returns |
      | 10  | 10      |
      | 10  | 5       |
      | 10  | 3       |
    When the player makes 3 rolls
    Then all rolls should have been accepted

  Scenario: Score increases after third roll following strike in last frame
    Given a last frame
    And the generator is configured as:
      | max | returns |
      | 10  | 10      |
      | 10  | 5       |
      | 10  | 3       |
    When the player makes 3 rolls
    Then the score should be 18

  Scenario: Third roll accepted after spare in last frame
    Given a last frame
    And the generator is configured as:
      | max | returns |
      | 10  | 7       |
      | 3   | 3       |
      | 10  | 5       |
    When the player makes 3 rolls
    Then all rolls should have been accepted

  Scenario: Score increases after third roll following spare in last frame
    Given a last frame
    And the generator is configured as:
      | max | returns |
      | 10  | 7       |
      | 3   | 3       |
      | 10  | 5       |
    When the player makes 3 rolls
    Then the score should be 15

  Scenario: Third roll rejected when last frame has no strike or spare
    Given a last frame
    And the generator is configured as:
      | max | returns |
      | 10  | 3       |
      | 7   | 4       |
    When the player makes 2 rolls
    Then all rolls should have been accepted
    And the next roll should be rejected

  Scenario: Fourth roll rejected in last frame
    Given a last frame
    And the generator is configured as:
      | max | returns |
      | 10  | 10      |
      | 10  | 5       |
      | 10  | 3       |
    When the player makes 3 rolls
    Then all rolls should have been accepted
    And the next roll should be rejected
