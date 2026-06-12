package support.exception;

import support.model.Status;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(Status current, Status target) {
        super("Transition invalide : " + current + " -> " + target);
    }
}
