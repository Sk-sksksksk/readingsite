package com.shashwat.readingsite.web;

import com.shashwat.readingsite.model.Language;
import com.shashwat.readingsite.model.Mood;
import com.shashwat.readingsite.repository.MoodRepository;
import com.shashwat.readingsite.service.BrowseResult;
import com.shashwat.readingsite.service.BrowseService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class MoodController {

    private final MoodRepository moodRepository;
    private final BrowseService browseService;

    public MoodController(MoodRepository moodRepository, BrowseService browseService) {
        this.moodRepository = moodRepository;
        this.browseService = browseService;
    }

    @GetMapping("/moods/{slug}")
    public String mood(@PathVariable String slug,
                       @RequestParam(name = "lang", defaultValue = "") String lang,
                       @RequestParam(name = "page", defaultValue = "1") int page,
                       Model model) {
        Mood mood = moodRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Language language = Params.parseEnum(Language.class, lang);

        BrowseResult result = browseService.search(language, null, mood.getSlug(), "", page);

        model.addAttribute("mood", mood);
        model.addAttribute("moods", moodRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("result", result);
        model.addAttribute("lang", language == null ? "" : language.name());
        return "mood";
    }
}
