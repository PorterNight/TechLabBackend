package techlab.backend.service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import techlab.backend.dto.exceptions.ApiErrorResponse;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR )
    @ExceptionHandler({RestResponseException.class})
    public ResponseEntity<ApiErrorResponse> botClientException(RestResponseException ex) {

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                "Error processing request",  // String description,
                String.valueOf(ex.getCode()),    // String code,
                ex.getClass().toString(),        // String exceptionName,
                ex.getMessage()                 // String exceptionMessage,
        );
        return ResponseEntity.status(ex.getCode()).body(errorResponse);
    }
}
