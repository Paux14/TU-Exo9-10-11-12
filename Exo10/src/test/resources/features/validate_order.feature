Feature: Validation de commande
  En tant qu'utilisateur, je veux valider une commande.

  Scenario: Validation réussie d'une commande existante
    Given une commande "order-1" existe
    When l'utilisateur valide la commande "order-1"
    Then l'utilisateur reçoit une confirmation de validation

  Scenario: Validation d'une commande inexistante retourne une erreur
    Given aucune commande "order-x" n'existe
    When l'utilisateur tente de valider la commande "order-x"
    Then une erreur indique que la commande n'existe pas
