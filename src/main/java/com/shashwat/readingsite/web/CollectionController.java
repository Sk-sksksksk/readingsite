package com.shashwat.readingsite.web;

import com.shashwat.readingsite.model.Language;
import com.shashwat.readingsite.service.CollectionService;
import com.shashwat.readingsite.service.CollectionService.CollectionDetail;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/collections")
    public String list(Model model) {
        model.addAttribute("collections", collectionService.listPublished());
        return "collections";
    }

    @GetMapping("/collections/{slug}")
    public String detail(@PathVariable String slug,
                         @RequestParam(name = "lang", defaultValue = "") String lang,
                         Model model) {
        Language language = Params.parseEnum(Language.class, lang);
        CollectionDetail detail = collectionService.findPublished(slug, language)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("collection", detail.collection());
        model.addAttribute("pieces", detail.pieces());
        model.addAttribute("lang", language == null ? "" : language.name());
        return "collection";
    }
}
