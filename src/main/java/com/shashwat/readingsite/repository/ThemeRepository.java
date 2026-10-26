package com.shashwat.readingsite.repository;

import com.shashwat.readingsite.model.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
}