Feature: Création de compte
  En tant qu'utilisateur, je veux créer un compte pour pouvoir passer des commandes.

  Scenario: Inscription réussie avec des informations valides
    Given un formulaire d'inscription est disponible
    When l'utilisateur s'inscrit avec l'email "alice@example.com", le nom "alice" et le mot de passe "pass123"
    Then l'utilisateur reçoit une confirmation d'inscription

  Scenario: Échec de l'inscription avec un nom d'utilisateur déjà existant
    Given un utilisateur "alice" est déjà enregistré
    When l'utilisateur tente de s'inscrire avec le nom "alice"
    Then une erreur indique que le compte existe déjà
