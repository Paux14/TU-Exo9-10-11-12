package bowling;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FrameStepDefinitions {

    private IGenerateur mockGenerateur;
    private Frame frame;
    private List<Boolean> rollResults;

    @Before
    public void setUp() {
        mockGenerateur = mock(IGenerateur.class);
        rollResults = new ArrayList<>();
    }

    @Given("a standard frame")
    public void aStandardFrame() {
        frame = new Frame(mockGenerateur, false);
    }

    @Given("a last frame")
    public void aLastFrame() {
        frame = new Frame(mockGenerateur, true);
    }

    @Given("the generator is configured as:")
    public void theGeneratorIsConfiguredAs(DataTable dataTable) {
        // Group return values by max, preserving insertion order for same-max chaining
        LinkedHashMap<Integer, List<Integer>> returnsByMax = new LinkedHashMap<>();
        for (Map<String, String> row : dataTable.asMaps()) {
            int max = Integer.parseInt(row.get("max"));
            int returns = Integer.parseInt(row.get("returns"));
            returnsByMax.computeIfAbsent(max, k -> new ArrayList<>()).add(returns);
        }
        for (Map.Entry<Integer, List<Integer>> entry : returnsByMax.entrySet()) {
            Integer[] values = entry.getValue().toArray(new Integer[0]);
            if (values.length == 1) {
                when(mockGenerateur.randomPin(entry.getKey())).thenReturn(values[0]);
            } else {
                Integer[] rest = Arrays.copyOfRange(values, 1, values.length);
                when(mockGenerateur.randomPin(entry.getKey())).thenReturn(values[0], rest);
            }
        }
    }

    @When("the player makes {int} roll(s)")
    public void thePlayerMakesRolls(int n) {
        for (int i = 0; i < n; i++) {
            rollResults.add(frame.makeRoll());
        }
    }

    @Then("the score should be {int}")
    public void theScoreShouldBe(int expected) {
        assertEquals(expected, frame.getScore());
    }

    @Then("all rolls should have been accepted")
    public void allRollsShouldHaveBeenAccepted() {
        for (int i = 0; i < rollResults.size(); i++) {
            assertTrue(rollResults.get(i), "Expected roll " + (i + 1) + " to be accepted");
        }
    }

    @Then("roll {int} should have been accepted")
    public void rollNShouldHaveBeenAccepted(int n) {
        assertTrue(rollResults.get(n - 1), "Expected roll " + n + " to be accepted");
    }

    @Then("the next roll should be rejected")
    public void theNextRollShouldBeRejected() {
        assertFalse(frame.makeRoll(), "Expected roll to be rejected");
    }

    @Then("the next roll should be accepted")
    public void theNextRollShouldBeAccepted() {
        assertTrue(frame.makeRoll(), "Expected roll to be accepted");
    }
}
