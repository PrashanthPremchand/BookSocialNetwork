package com.prashanth.book.feedback;

import com.prashanth.book.book.Book;
import com.prashanth.book.book.BookRepository;
import com.prashanth.book.common.PageResponse;
import com.prashanth.book.exception.OperationNotPermittedException;
import com.prashanth.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;

    public Integer saveFeedback(FeedbackRequest request, Authentication connectedUser) {

        Book book = getBook(request.bookId());
        checkBorrowable(book);
        User user = getUser(connectedUser);
        checkNotOwner(book, user);
        Feedback feedback = feedbackMapper.toFeedback(request);
        return feedbackRepository.save(feedback).getId();

    }

    public PageResponse<FeedbackResponse> findAllFeedbackByBook(Integer bookId, Integer page, Integer size, Authentication connectedUser) {

        Pageable pageable = PageRequest.of(page, size);
        User user = getUser(connectedUser);
        Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks
                .stream()
                .map(feedback -> feedbackMapper.toFeedbackResponse(feedback, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }

    private Book getBook(Integer bookId){

        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the id: " + bookId));

    }

    private User getUser(Authentication connectedUser){

        return (User) connectedUser.getPrincipal();

    }

    private void checkNotOwner(Book book, User user){

        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Owner is not allowed to give feedback on their own book"
            );
        }

    }

    private void checkBorrowable(Book book){

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You cannot give feedback to archived or not sharable book");
        }

    }

}
