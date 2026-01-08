package com.prashanth.book.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    Page<Feedback> findAllByBookId(Integer bookId, Pageable pageable);
}
