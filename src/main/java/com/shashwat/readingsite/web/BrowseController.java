package com.shashwat.readingsite.web;

import com.shashwat.readingsite.model.Language;
import com.shashwat.readingsite.model.PieceType;
import com.shashwat.readingsite.repository.MoodRepository;
import com.shashwat.readingsite.service.BrowseResult;
import com.shashwat.readingsite.service.BrowseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BrowseController {

    private final BrowseService browseService;
    private final MoodRepository moodRepository;

    public BrowseController(BrowseService browseService, MoodRepository moodRepository) {
        this.browseService = browseService;
        this.moodRepository = moodRepository;
    }

    @GetMapping("/browse")
    public String browse(@RequestParam(name = "lang", defaultValue = "") String lang,
                         @RequestParam(name = "type", defaultValue = "") String type,
                         @RequestParam(name = "mood", defaultValue = "") String mood,
                         @RequestParam(name = "q", defaultValue = "") String q,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         Model model) {

        Language language = parse(Language.class, lang);
        PieceType pieceType = parse(PieceType.class, type);

        BrowseResult result = browseService.search(language, pieceType, mood, q, page);

        model.addAttribute("result", result);
        model.addAttribute("moods", moodRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("types", PieceType.values());
        model.addAttribute("lang", language == null ? "" : language.name());
        model.addAttribute("type", pieceType == null ? "" : pieceType.name());
        model.addAttribute("mood", mood);
        model.addAttribute("q", q);
        return "browse";
    }

    private static <E extends Enum<E>> E parse(Class<E> enumType, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(enumType, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}