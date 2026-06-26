# language: fr
Fonctionnalité: Gestion des comptes bancaires

  Scénario: Création d'un nouveau compte
    Quand je crée un compte avec le numéro "ACC-001" et le titulaire "Alice"
    Alors le compte "ACC-001" existe avec un solde de 0.0

  Scénario: Dépôt d'argent sur un compte
    Etant donné un compte "ACC-002" appartenant à "Bob" avec un solde de 0.0
    Quand je dépose 300.0 sur le compte "ACC-002"
    Alors le solde du compte "ACC-002" est de 300.0

  Scénario: Retrait avec fonds suffisants
    Etant donné un compte "ACC-003" appartenant à "Charlie" avec un solde de 500.0
    Quand je retire 200.0 du compte "ACC-003"
    Alors le solde du compte "ACC-003" est de 300.0

  Scénario: Retrait avec fonds insuffisants
    Etant donné un compte "ACC-004" appartenant à "Diana" avec un solde de 100.0
    Quand je tente de retirer 500.0 du compte "ACC-004"
    Alors une erreur de fonds insuffisants est levée

  Scénario: Virement entre deux comptes
    Etant donné un compte "ACC-005" appartenant à "Eve" avec un solde de 1000.0
    Et un compte "ACC-006" appartenant à "Frank" avec un solde de 0.0
    Quand je vire 400.0 du compte "ACC-005" vers le compte "ACC-006"
    Alors le solde du compte "ACC-005" est de 600.0
    Et le solde du compte "ACC-006" est de 400.0

  Scénario: Virement refusé pour solde insuffisant
    Etant donné un compte "ACC-007" appartenant à "Grace" avec un solde de 50.0
    Et un compte "ACC-008" appartenant à "Hank" avec un solde de 0.0
    Quand je tente de virer 200.0 du compte "ACC-007" vers le compte "ACC-008"
    Alors une erreur de fonds insuffisants est levée
