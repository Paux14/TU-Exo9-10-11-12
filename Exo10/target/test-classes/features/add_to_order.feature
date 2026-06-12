Feature: Ajout de produit à une commande
  En tant qu'utilisateur, je veux ajouter des produits à ma commande.

  Scenario: Ajout d'un nouveau produit à une commande existante
    Given une commande "order-1" existe
    And un produit "prod-1" existe
    When l'utilisateur ajoute le produit "prod-1" à la commande "order-1"
    Then l'utilisateur reçoit une confirmation d'ajout

  Scenario: Ajout d'un produit déjà présent augmente la quantité
    Given une commande "order-1" contient le produit "prod-1" en quantité 1
    And un produit "prod-1" existe
    When l'utilisateur ajoute le produit "prod-1" à la commande "order-1"
    Then la quantité du produit "prod-1" dans la commande est de 2

  Scenario: Ajout à une commande inexistante retourne une erreur
    Given aucune commande "order-x" n'existe
    When l'utilisateur tente d'ajouter le produit "prod-1" à la commande "order-x"
    Then une erreur indique que la commande n'existe pas
