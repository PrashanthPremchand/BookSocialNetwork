package com.prashanth.book.feedback;

import com.prashanth.book.book.Book;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Component
public class FeedbackMapper {

    public Feedback toFeedback(FeedbackRequest request) {

        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book.builder()
                        .id(request.bookId())
                        .build())
                .build();

    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer userId) {

        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), userId))
                .build();

    }
}
