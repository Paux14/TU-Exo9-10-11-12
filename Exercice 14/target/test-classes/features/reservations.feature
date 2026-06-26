# language: fr
Fonctionnalité: Gestion des réservations MédiaCity

  Scénario: Réservation d'un ouvrage indisponible
    Etant donné un ouvrage "Dune" actuellement emprunté
    Et un adhérent actif "Alice"
    Quand "Alice" réserve l'ouvrage "Dune"
    Alors la réservation est enregistrée avec le statut "EN_ATTENTE"

  Scénario: Plusieurs réservations sur le même ouvrage
    Etant donné un ouvrage "1984" actuellement emprunté
    Et un adhérent actif "Bob"
    Et un adhérent actif "Carol"
    Quand "Bob" réserve l'ouvrage "1984"
    Et "Carol" réserve l'ouvrage "1984"
    Alors il y a 2 réservations en attente pour "1984"

  Scénario: Restitution d'un ouvrage réservé notifie le prochain réservant
    Etant donné un ouvrage "Fondation" actuellement emprunté
    Et un adhérent actif "David" a réservé l'ouvrage "Fondation"
    Quand l'ouvrage "Fondation" est restitué
    Alors la réservation de "David" passe au statut "DISPONIBLE"

  Scénario: Refus de réservation pour un adhérent suspendu
    Etant donné un ouvrage "Le Seigneur des Anneaux" actuellement emprunté
    Et un adhérent suspendu "Eve"
    Quand "Eve" tente de réserver l'ouvrage "Le Seigneur des Anneaux"
    Alors une erreur d'adhérent suspendu est levée

  Scénario: Un ouvrage disponible ne peut pas être réservé
    Etant donné un ouvrage "Fahrenheit 451" disponible en rayon
    Et un adhérent actif "Frank"
    Quand "Frank" tente de réserver l'ouvrage "Fahrenheit 451"
    Alors une erreur indique que l'ouvrage est déjà disponible
