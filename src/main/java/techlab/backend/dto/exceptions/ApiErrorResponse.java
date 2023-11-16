package techlab.backend.dto.exceptions;

import java.util.List;

public record ApiErrorResponse(
        String description,
        String code,
        String exceptionName,
        String exceptionMessage
){}
