package com.shashwat.readingsite.repository;

import com.shashwat.readingsite.model.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MoodRepository extends JpaRepository<Mood, Long> {

    List<Mood> findAllByOrderBySortOrderAsc();

    Optional<Mood> findBySlug(String slug);
}