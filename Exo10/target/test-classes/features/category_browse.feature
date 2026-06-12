Feature: Navigation par catégorie
  En tant qu'utilisateur, je veux naviguer par catégorie pour découvrir les produits disponibles.

  Scenario: Accès aux produits d'une catégorie existante
    Given des produits dans la catégorie "Électronique"
    When l'utilisateur sélectionne la catégorie "Électronique"
    Then l'utilisateur voit les produits de la catégorie "Électronique"
