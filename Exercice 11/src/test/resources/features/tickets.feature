Feature: Gestion des tickets de support
  En tant qu'opérateur, je veux gérer des tickets de support
  afin de suivre et résoudre les problèmes signalés.

  Scenario: Création d'un ticket valide
    Given aucun ticket n'existe
    When je crée un ticket avec le titre "Problème de connexion" et la priorité "HIGH"
    Then le ticket est créé avec le statut "OPEN"
    And le ticket possède un identifiant

  Scenario: Résolution d'un ticket
    Given un ticket "Problème réseau" avec la priorité "MEDIUM" a été créé
    When je modifie le statut du ticket à "RESOLVED"
    Then le statut du ticket est "RESOLVED"

  Scenario: Refus de modification d'un ticket déjà résolu
    Given un ticket "Bug critique" avec la priorité "HIGH" a été créé
    And le ticket a été résolu
    When je tente de modifier le statut du ticket à "IN_PROGRESS"
    Then une erreur de transition invalide est levée

  Scenario: Consultation d'un ticket inexistant
    Given aucun ticket n'existe
    When je consulte le ticket avec l'identifiant "id-inexistant"
    Then une erreur de ticket introuvable est levée
