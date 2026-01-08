package com.prashanth.book.feedback;

import jakarta.validation.constraints.*;

public record FeedbackRequest(

        @NotNull(message = "200")
        @DecimalMin(value = "0.0", inclusive = true, message = "201")
        @DecimalMax(value = "5.0", inclusive = true, message = "202")
        Double note,

        @NotBlank(message = "203")
        String comment,

        @NotNull(message = "204")
        @Positive(message = "205")
        Integer bookId
) {
}

