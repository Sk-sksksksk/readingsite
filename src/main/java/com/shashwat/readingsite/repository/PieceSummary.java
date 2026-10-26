package com.shashwat.readingsite.repository;

import com.shashwat.readingsite.model.Language;
import com.shashwat.readingsite.model.PieceType;
import com.shashwat.readingsite.model.PublishStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Lightweight view of a piece (no body text) for admin lists. */
public interface PieceSummary {
    Long getId();
    String getTitle();
    String getSlug();
    Language getLanguage();
    PieceType getType();
    PublishStatus getStatus();
    Boolean getFeatured();
    LocalDate getPublishedOn();
    LocalDateTime getUpdatedAt();
}
