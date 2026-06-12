Feature: Recherche de produits
  En tant qu'utilisateur, je veux rechercher des produits pour trouver rapidement ce dont j'ai besoin.

  Scenario: Recherche par mot-clé retourne des résultats
    Given des produits correspondant au mot-clé "clavier"
    When l'utilisateur recherche "clavier"
    Then l'utilisateur voit une liste de produits

  Scenario: Recherche par prix maximum
    Given des produits disponibles à moins de 50.0 euros
    When l'utilisateur recherche des produits avec un prix maximum de 50.0
    Then l'utilisateur voit une liste de produits filtrés par prix
