package rooms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rooms.exception.AlreadyCancelledException;
import rooms.exception.InvalidTimeSlotException;
import rooms.exception.ReservationNotFoundException;
import rooms.exception.RoomNotFoundException;
import rooms.exception.TimeSlotConflictException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RoomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleRoomNotFound(RoomNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleReservationNotFound(ReservationNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(TimeSlotConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleTimeSlotConflict(TimeSlotConflictException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(AlreadyCancelledException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleAlreadyCancelled(AlreadyCancelledException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(InvalidTimeSlotException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidTimeSlot(InvalidTimeSlotException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException e) {
        return Map.of("error", "Données invalides");
    }
}
