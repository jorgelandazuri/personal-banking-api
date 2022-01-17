Feature: Account creation, balance & transfers API testing.
### Account id "1" is for Bank Treasury and initialized at app start-up

## CREATE ACCOUNT AND BALANCE

  #Second account id: 2
  Scenario: Create a new account and get balance
    Given An account needs to be created with:
      | documentId     | ABC-1           |
      | nameAndSurname | Jorge Landazuri |
      | initialDeposit | 100.03          |
    When the create account request is executed
    Then the account is created successfully
    And the balance for the account 2 is "100.03"

  Scenario: Failed to create account and get balance (incorrect initial deposit)
    Given An account needs to be created with:
      | documentId     | ABC-1           |
      | nameAndSurname | Another Name    |
      | initialDeposit | -100.03         |
    When the create account request is executed
    Then the account is not created due to invalid initial deposit
    And the balance for the account 3 does not exist

  Scenario: Failed to create account and get balance (blank document)
    Given An account needs to be created with:
      | documentId     ||
      | nameAndSurname | Another Name    |
      | initialDeposit | 100.03          |
    When the create account request is executed
    Then the account is not created due to invalid document id
    And the balance for the account 3 does not exist

  Scenario: Failed to create account and get balance (blank nameAndSurname)
    Given An account needs to be created with:
      | documentId     | ABC-1           |
      | nameAndSurname ||
      | initialDeposit | 100.03          |
    When the create account request is executed
    Then the account is not created due to invalid name and surname
    And the balance for the account 3 does not exist

## TRANSFERS

  #Second account id: 3
  Scenario: Create a second new account and get balance
    Given An account needs to be created with:
      | documentId     | ABC-2           |
      | nameAndSurname | Another Name    |
      | initialDeposit | 200.03          |
    When the create account request is executed
    Then the account is created successfully
    And the balance for the account 3 is "200.03"

  #The first transaction for every account is the initial deposit.
  Scenario: Transfer from first account to second
    Given A transfer needs to be made with:
      | sourceAccountId      | 2      |
      | destinationAccountId | 3      |
      | amount               | 100    |
    When the transfer request is executed
    Then the transfer is successful
    And the transfers list for account 2 has 2 transactions
    And the transfers list for account 3 has 2 transactions
    And the balance for the account 2 is "0.03"
    And the balance for the account 3 is "300.03"

  Scenario: Transfer failure due to not existing account
    Given A transfer needs to be made with:
      | sourceAccountId      | 2      |
      | destinationAccountId | 10     |
      | amount               | 10     |
    When the transfer request is executed
    Then the transfer is unsuccessful with not existing destination account message
    And the transfers list for account 10 does not exists

  Scenario: Transfer failure more than balance.
    Given A transfer needs to be made with:
      | sourceAccountId      | 2      |
      | destinationAccountId | 3      |
      | amount               | 0.04   |
    When the transfer request is executed
    Then the transfer is unsuccessful with not existent account or enough balance
    And the transfers list for account 2 has 2 transactions
    And the balance for the account 2 is "0.03"