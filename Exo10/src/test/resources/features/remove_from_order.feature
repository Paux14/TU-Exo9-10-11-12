Feature: Suppression de produit d'une commande
  En tant qu'utilisateur, je veux supprimer des produits de ma commande.

  Scenario: Diminution de la quantité si quantité supérieure à 1
    Given une commande "order-1" contient le produit "prod-1" en quantité 2
    When l'utilisateur supprime le produit "prod-1" de la commande "order-1"
    Then la quantité du produit "prod-1" dans la commande est de 1

  Scenario: Retrait du produit si quantité égale à 1
    Given une commande "order-1" contient le produit "prod-1" en quantité 1
    When l'utilisateur supprime le produit "prod-1" de la commande "order-1"
    Then le produit "prod-1" n'est plus dans la commande

  Scenario: Suppression d'un produit absent retourne une erreur
    Given une commande "order-1" existe sans le produit "prod-x"
    When l'utilisateur tente de supprimer le produit "prod-x" de la commande "order-1"
    Then une erreur indique que le produit n'est pas dans la commande

  Scenario: Suppression dans une commande inexistante retourne une erreur
    Given aucune commande "order-x" n'existe
    When l'utilisateur tente de supprimer le produit "prod-1" de la commande "order-x"
    Then une erreur indique que la commande n'existe pas
