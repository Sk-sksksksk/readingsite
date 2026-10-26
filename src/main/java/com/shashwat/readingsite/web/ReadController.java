package com.shashwat.readingsite.web;

import com.shashwat.readingsite.model.Mood;
import com.shashwat.readingsite.model.Piece;
import com.shashwat.readingsite.model.PublishStatus;
import com.shashwat.readingsite.repository.PieceCollectionRepository;
import com.shashwat.readingsite.repository.PieceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class ReadController {

    private final PieceRepository pieceRepository;
    private final PieceCollectionRepository collectionRepository;

    public ReadController(PieceRepository pieceRepository,
                          PieceCollectionRepository collectionRepository) {
        this.pieceRepository = pieceRepository;
        this.collectionRepository = collectionRepository;
    }

    @GetMapping("/read/{slug}")
    public String read(@PathVariable String slug, Model model) {
        Piece piece = pieceRepository.findDetailBySlug(slug, PublishStatus.PUBLISHED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Long> moodIds = piece.getMoods().stream().map(Mood::getId).toList();
        List<Piece> related = moodIds.isEmpty()
                ? List.of()
                : pieceRepository.findRelated(PublishStatus.PUBLISHED, piece.getLanguage(),
                piece.getId(), moodIds, PageRequest.of(0, 3));

        model.addAttribute("piece", piece);
        model.addAttribute("related", related);
        model.addAttribute("collections", collectionRepository.findPublishedByPieceId(piece.getId()));
        return "read";
    }
}