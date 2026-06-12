Feature: Réservation de salles de réunion

  Scenario: Réservation acceptée quand la salle existe et le créneau est libre
    Given une salle "Salle A" de capacité 10 existe
    When je réserve cette salle pour "Alice" du "2025-01-15T09:00" au "2025-01-15T10:00"
    Then la réservation est créée avec le statut "CONFIRMED"

  Scenario: Réservation refusée quand la salle n'existe pas
    Given aucune salle n'existe
    When je tente de réserver la salle "id-inexistant" pour "Bob" du "2025-01-15T09:00" au "2025-01-15T10:00"
    Then une erreur de salle introuvable est levée

  Scenario: Réservation refusée quand le créneau chevauche une réservation existante
    Given une salle "Salle B" de capacité 5 existe
    And une réservation existe pour cette salle du "2025-01-15T09:00" au "2025-01-15T11:00"
    When je tente de réserver cette salle pour "Charlie" du "2025-01-15T10:00" au "2025-01-15T12:00"
    Then une erreur de conflit de créneau est levée
