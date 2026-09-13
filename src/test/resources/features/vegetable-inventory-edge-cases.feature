Feature: Vegetable Inventory - Edge Cases & Real-Life Scenarios
  As a vegetable enthusiast
  I want extra assurance around the inventory business rules
  So that boundary, negative and realistic everyday flows are all verified

  # ============================================================================
  # BOUNDARY SCENARIOS
  # Exercise the edges of the inventory business rules without violating them.
  # ============================================================================

  @math @boundary
  Scenario Outline: Eating exactly the whole stock leaves zero (upper boundary)
    Given I have <initial> carrots in stock
    When I eat <eaten> carrots
    Then I have <remaining> carrots

    Examples:
      | initial | eaten | remaining |
      | 1       | 1     | 0         |
      | 9       | 9     | 0         |

  @math @boundary
  Scenario: Eating nothing leaves the stock unchanged (lower boundary)
    Given I have 7 cucumbers in stock
    When I eat 0 cucumbers
    Then I have 7 cucumbers

  @validation @boundary
  Scenario Outline: Reject eating just one more than available (closest rejection boundary)
    Given I have <initial> carrots in stock
    When I try to eat <eaten> carrots
    Then the operation should be rejected
    And I should see the message "Cannot eat more carrots than are available"
    And I should still have <initial> carrots

    Examples:
      | initial | eaten |
      | 10      | 11    |
      | 1       | 2     |
      | 0       | 1     |

  @validation @boundary
  Scenario: Eating from an empty cucumber stock is rejected
    Given I have 0 cucumbers in stock
    When I try to eat 1 cucumbers
    Then the operation should be rejected
    And I should still have 0 cucumbers

  # ============================================================================
  # NEGATIVE SCENARIOS
  # Invalid quantities must be rejected before the inventory is mutated.
  # ============================================================================

  @validation @negative
  Scenario Outline: Adding a negative quantity is rejected and stock is preserved
    Given I have <initial> carrots in stock
    When I try to add <negative> carrots
    Then the operation should be rejected
    And I should see the message "<message>"
    And I should still have <initial> carrots

    Examples:
      | initial | negative | message                                  |
      | 5       | -1       | Cannot use a negative quantity of carrots |
      | 0       | -100     | Cannot use a negative quantity of carrots |

  @validation @negative
  Scenario Outline: Eating a negative quantity is rejected and stock is preserved
    Given I have <initial> cucumbers in stock
    When I try to eat <negative> cucumbers
    Then the operation should be rejected
    And I should see the message "<message>"
    And I should still have <initial> cucumbers

    Examples:
      | initial | negative | message                                    |
      | 5       | -3       | Cannot use a negative quantity of cucumbers |
      | 2       | -1       | Cannot use a negative quantity of cucumbers |

  @validation @negative
  Scenario Outline: Repeated over-consumption attempts never drive the stock negative
    Given I have <initial> carrots in stock
    When I try to eat <attempt1> carrots
    And I try to eat <attempt2> carrots
    Then I should still have <initial> carrots

    Examples:
      | initial | attempt1 | attempt2 |
      | 3       | 5        | 4        |
      | 2       | 3        | 5        |

  # ============================================================================
  # REAL-LIFE SCENARIOS
  # Everyday, multi-step flows that combine valid consumption and restocking.
  # ============================================================================

  @smoke @math
  Scenario: A working week of lunches gradually consumes the carrot supply
    Given I have 12 carrots in stock
    When I eat 3 carrots
    And I eat 4 carrots
    And I eat 2 carrots
    Then I have 3 carrots

  @smoke @math
  Scenario: Restocking after a busy weekend replenishes the cucumber supply
    Given I have 8 cucumbers in stock
    When I eat 6 cucumbers
    And I restock 10 cucumbers
    Then I have 12 cucumbers

  @smoke @math
  Scenario: A rejected over-consumption at dinner does not spoil later meals
    Given I have 4 carrots in stock
    When I try to eat 5 carrots
    And I eat 2 carrots
    Then I have 2 carrots

  @smoke @math
  Scenario: Preparing a family salad consumes from both vegetables
    Given I have the following vegetables
      | vegetable | quantity |
      | cucumber  | 10       |
      | carrot    | 6        |
    When I eat 4 cucumbers
    And I eat 3 carrots
    Then I have 6 cucumbers
    And I have 3 carrots
    And I have 9 vegetables
