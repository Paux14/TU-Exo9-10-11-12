Feature: Standard Frame
  A standard (non-last) bowling frame

  Scenario: First roll increases score in standard frame
    Given a standard frame
    And the generator is configured as:
      | max | returns |
      | 10  | 3       |
    When the player makes 1 roll
    Then the score should be 3

  Scenario: Second roll increases score in standard frame
    Given a standard frame
    And the generator is configured as:
      | max | returns |
      | 10  | 3       |
      | 7   | 4       |
    When the player makes 2 rolls
    Then the score should be 7

  Scenario: Second roll rejected when standard frame starts with strike
    Given a standard frame
    And the generator is configured as:
      | max | returns |
      | 10  | 10      |
    When the player makes 1 roll
    Then roll 1 should have been accepted
    And the next roll should be rejected

  Scenario: Third roll rejected when standard frame already has two rolls
    Given a standard frame
    And the generator is configured as:
      | max | returns |
      | 10  | 3       |
      | 7   | 4       |
    When the player makes 2 rolls
    Then all rolls should have been accepted
    And the next roll should be rejected
