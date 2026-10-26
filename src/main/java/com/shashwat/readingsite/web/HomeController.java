package com.shashwat.readingsite.web;

import com.shashwat.readingsite.model.PublishStatus;
import com.shashwat.readingsite.repository.MoodRepository;
import com.shashwat.readingsite.repository.PieceCollectionRepository;
import com.shashwat.readingsite.repository.PieceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final PieceRepository pieceRepository;
    private final MoodRepository moodRepository;
    private final PieceCollectionRepository collectionRepository;

    public HomeController(PieceRepository pieceRepository,
                          MoodRepository moodRepository,
                          PieceCollectionRepository collectionRepository) {
        this.pieceRepository = pieceRepository;
        this.moodRepository = moodRepository;
        this.collectionRepository = collectionRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("moods", moodRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("featured", pieceRepository
                .findFirstByStatusAndFeaturedTrueOrderByPublishedOnDesc(PublishStatus.PUBLISHED)
                .orElse(null));
        model.addAttribute("collections", collectionRepository.findByPublishedTrueOrderBySortOrderAsc());
        model.addAttribute("recent", pieceRepository.findTop6ByStatusOrderByPublishedOnDesc(PublishStatus.PUBLISHED));
        return "home";
    }
}