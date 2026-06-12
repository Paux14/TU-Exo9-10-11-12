package bowling;

import java.util.ArrayList;
import java.util.List;

public class Frame {
    private int score;
    private boolean lastFrame;
    private IGenerateur generateur;
    private List<Roll> rolls;

    public Frame(IGenerateur generateur, boolean lastFrame) {
        this.lastFrame = lastFrame;
        this.generateur = generateur;
        this.rolls = new ArrayList<>();
    }

    public int getScore() {
        return score;
    }

    public boolean makeRoll() {
        if (!canRoll()) {
            return false;
        }

        int pins = generateur.randomPin(getMaxPinsForNextRoll());
        rolls.add(new Roll(pins));
        score += pins;
        return true;
    }

    private boolean canRoll() {
        if (rolls.isEmpty()) {
            return true;
        }

        if (!lastFrame) {
            if (rolls.get(0).getPins() == 10) {
                return false;
            }
            return rolls.size() < 2;
        }

        if (rolls.size() >= 3) {
            return false;
        }
        if (rolls.size() == 1) {
            return true;
        }
        return isStrike() || isSpare();
    }

    private int getMaxPinsForNextRoll() {
        if (rolls.isEmpty()) {
            return 10;
        }

        Roll firstRoll = rolls.get(0);

        if (lastFrame && firstRoll.getPins() == 10) {
            return 10;
        }

        if (rolls.size() == 1) {
            return 10 - firstRoll.getPins();
        }

        return 10;
    }

    private boolean isStrike() {
        return !rolls.isEmpty() && rolls.get(0).getPins() == 10;
    }

    private boolean isSpare() {
        return rolls.size() >= 2
                && rolls.get(0).getPins() + rolls.get(1).getPins() == 10;
    }
}
