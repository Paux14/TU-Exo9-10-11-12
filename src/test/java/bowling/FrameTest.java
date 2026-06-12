package bowling;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FrameTest {

    @Mock
    private IGenerateur generateur;

    @Test
    void shouldIncreaseScoreWhenFirstRollIsMadeInStandardFrame() {
        when(generateur.randomPin(10)).thenReturn(3);

        Frame frame = new Frame(generateur, false);

        frame.makeRoll();

        assertEquals(3, frame.getScore());
    }

    @Test
    void shouldIncreaseScoreWhenSecondRollIsMadeInStandardFrame() {
        when(generateur.randomPin(10)).thenReturn(3);
        when(generateur.randomPin(7)).thenReturn(4);

        Frame frame = new Frame(generateur, false);

        frame.makeRoll();
        frame.makeRoll();

        assertEquals(7, frame.getScore());
    }

    @Test
    void shouldRejectSecondRollWhenStandardFrameStartsWithStrike() {
        when(generateur.randomPin(10)).thenReturn(10);

        Frame frame = new Frame(generateur, false);

        assertTrue(frame.makeRoll());
        assertFalse(frame.makeRoll());
    }

    @Test
    void shouldRejectThirdRollWhenStandardFrameAlreadyHasTwoRolls() {
        when(generateur.randomPin(10)).thenReturn(3);
        when(generateur.randomPin(7)).thenReturn(4);

        Frame frame = new Frame(generateur, false);

        frame.makeRoll();
        frame.makeRoll();

        assertFalse(frame.makeRoll());
    }

    @Test
    void shouldIncreaseScoreWhenSecondRollIsMadeAfterStrikeInLastFrame() {
        when(generateur.randomPin(10)).thenReturn(10, 5);

        Frame frame = new Frame(generateur, true);

        frame.makeRoll();
        frame.makeRoll();

        assertEquals(15, frame.getScore());
    }

    @Test
    void shouldAcceptThirdRollWhenLastFrameStartsWithStrike() {
        when(generateur.randomPin(10)).thenReturn(10, 5, 3);

        Frame frame = new Frame(generateur, true);

        assertTrue(frame.makeRoll());
        assertTrue(frame.makeRoll());
        assertTrue(frame.makeRoll());
    }

    @Test
    void shouldAcceptSecondRollWhenLastFrameStartsWithStrike() {
        when(generateur.randomPin(10)).thenReturn(10, 5);

        Frame frame = new Frame(generateur, true);

        assertTrue(frame.makeRoll());
        assertTrue(frame.makeRoll());
    }

    @Test
    void shouldIncreaseScoreWhenThirdRollIsMadeAfterStrikeInLastFrame() {
        when(generateur.randomPin(10)).thenReturn(10, 5, 3);

        Frame frame = new Frame(generateur, true);

        frame.makeRoll();
        frame.makeRoll();
        frame.makeRoll();

        assertEquals(18, frame.getScore());
    }

    @Test
    void shouldAcceptThirdRollWhenLastFrameStartsWithSpare() {
        when(generateur.randomPin(10)).thenReturn(7);
        when(generateur.randomPin(3)).thenReturn(3);
        when(generateur.randomPin(10)).thenReturn(5);

        Frame frame = new Frame(generateur, true);

        frame.makeRoll();
        frame.makeRoll();

        assertTrue(frame.makeRoll());
    }

    @Test
    void shouldIncreaseScoreWhenThirdRollIsMadeAfterSpareInLastFrame() {
        when(generateur.randomPin(10)).thenReturn(7, 5);
        when(generateur.randomPin(3)).thenReturn(3);

        Frame frame = new Frame(generateur, true);

        frame.makeRoll();
        frame.makeRoll();
        frame.makeRoll();

        assertEquals(15, frame.getScore());
    }

    @Test
    void shouldRejectThirdRollWhenLastFrameHasNoStrikeOrSpare() {
        when(generateur.randomPin(10)).thenReturn(3);
        when(generateur.randomPin(7)).thenReturn(4);

        Frame frame = new Frame(generateur, true);

        frame.makeRoll();
        frame.makeRoll();

        assertFalse(frame.makeRoll());
    }

    @Test
    void shouldRejectFourthRollInLastFrame() {
        when(generateur.randomPin(10)).thenReturn(10, 5, 3);

        Frame frame = new Frame(generateur, true);

        frame.makeRoll();
        frame.makeRoll();
        frame.makeRoll();

        assertFalse(frame.makeRoll());
    }
}
