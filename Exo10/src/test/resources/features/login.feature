Feature: Connexion
  En tant qu'utilisateur, je veux me connecter pour accéder à l'application.

  Scenario: Connexion réussie avec des identifiants valides
    Given un utilisateur "bob" existe avec le mot de passe "secret"
    When l'utilisateur se connecte avec "bob" et "secret"
    Then l'utilisateur est redirigé vers la page d'accueil

  Scenario: Connexion échouée avec un mauvais mot de passe
    Given un utilisateur "bob" existe avec le mot de passe "secret"
    When l'utilisateur se connecte avec "bob" et "wrongpass"
    Then l'utilisateur voit un message d'erreur de connexion
