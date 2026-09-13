Feature: Cucumber Mathematics
  As a vegetable enthusiast
  I want to manage my vegetable inventory
  So that I can track consumption and prevent over-consumption

  # ------------------------------------------------------------------------
  # Valid consumption: the inventory must decrease by exactly the eaten amount.
  # ------------------------------------------------------------------------

  @smoke @math
  Scenario Outline: Let's eat cucumbers
    Given I have <initial> cucumbers in stock
    When I eat <eaten> cucumbers
    Then I have <remaining> cucumbers

    Examples:
      | initial | eaten | remaining |
      | 5       | 3     | 2         |
      | 10      | 4     | 6         |
      | 8       | 8     | 0         |

  # ------------------------------------------------------------------------
  # Business rule: a user cannot consume more vegetables than are available.
  # The operation must be rejected, the inventory preserved, and a meaningful
  # validation message returned. The inventory must NEVER become negative.
  # ------------------------------------------------------------------------

  @regression @validation
  Scenario Outline: Prevent eating more carrots than available
    Given I have <initial> carrots in stock
    When I try to eat <eaten> carrots
    Then the operation should be rejected
    And I should see the message "<message>"
    And I should still have <initial> carrots

    Examples:
      | initial | eaten | message                                      |
      | 10      | 12    | Cannot eat more carrots than are available |
      | 5       | 6     | Cannot eat more carrots than are available |
      | 0       | 1     | Cannot eat more carrots than are available |

  # ------------------------------------------------------------------------
  # Mixed operations initialised from a Cucumber DataTable.
  # ------------------------------------------------------------------------

  @smoke @math
  Scenario: Let's make a salad
    Given I have the following vegetables
      | vegetable | quantity |
      | cucumber  | 8        |
      | carrot    | 5        |
    When I eat 3 cucumbers
    And I eat 2 carrots
    Then I have 5 cucumbers
    And I have 3 carrots
    And I have 8 vegetables
