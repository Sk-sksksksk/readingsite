package com.shashwat.readingsite.web.admin;

import com.shashwat.readingsite.model.PublishStatus;
import com.shashwat.readingsite.repository.PieceCollectionRepository;
import com.shashwat.readingsite.repository.PieceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final PieceRepository pieceRepository;
    private final PieceCollectionRepository collectionRepository;

    public AdminController(PieceRepository pieceRepository, PieceCollectionRepository collectionRepository) {
        this.pieceRepository = pieceRepository;
        this.collectionRepository = collectionRepository;
    }

    @GetMapping("/admin/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("publishedCount", pieceRepository.countByStatus(PublishStatus.PUBLISHED));
        model.addAttribute("draftCount", pieceRepository.countByStatus(PublishStatus.DRAFT));
        model.addAttribute("collectionCount", collectionRepository.count());
        model.addAttribute("recent", pieceRepository.findTop5ByOrderByUpdatedAtDesc());
        return "admin/dashboard";
    }
}
