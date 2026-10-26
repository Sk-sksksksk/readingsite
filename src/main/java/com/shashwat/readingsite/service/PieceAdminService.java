package com.shashwat.readingsite.service;

import com.shashwat.readingsite.model.Mood;
import com.shashwat.readingsite.model.Piece;
import com.shashwat.readingsite.model.PublishStatus;
import com.shashwat.readingsite.repository.MoodRepository;
import com.shashwat.readingsite.repository.PieceRepository;
import com.shashwat.readingsite.repository.PieceSummary;
import com.shashwat.readingsite.web.admin.PieceForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Owner-side create, edit and delete of pieces. */
@Service
public class PieceAdminService {

    private static final int WORDS_PER_MINUTE = 200;

    private final PieceRepository pieces;
    private final MoodRepository moods;

    public PieceAdminService(PieceRepository pieces, MoodRepository moods) {
        this.pieces = pieces;
        this.moods = moods;
    }

    @Transactional(readOnly = true)
    public List<PieceSummary> listAll() {
        return pieces.findAllByOrderByUpdatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Optional<PieceForm> loadForm(Long id) {
        return pieces.findDetailById(id).map(PieceForm::from);
    }

    @Transactional(readOnly = true)
    public Optional<Piece> findForPreview(Long id) {
        return pieces.findDetailById(id);
    }

    @Transactional(readOnly = true)
    public Optional<String> titleOf(Long id) {
        return pieces.findById(id).map(Piece::getTitle);
    }

    @Transactional(readOnly = true)
    public boolean slugTaken(String slug, Long ownId) {
        return ownId == null ? pieces.existsBySlug(slug) : pieces.existsBySlugAndIdNot(slug, ownId);
    }

    @Transactional
    public Piece create(PieceForm form) {
        Piece piece = new Piece();
        apply(piece, form);
        return pieces.save(piece);
    }

    /** Returns empty when the piece does not exist. */
    @Transactional
    public Optional<Piece> update(Long id, PieceForm form) {
        return pieces.findDetailById(id).map(piece -> {
            apply(piece, form);
            return pieces.save(piece);
        });
    }

    @Transactional
    public void delete(Long id) {
        pieces.deleteById(id);
    }

    private void apply(Piece piece, PieceForm f) {
        String body = f.getBody().replace("\r\n", "\n").replace("\r", "\n").strip();

        piece.setTitle(f.getTitle().trim());
        piece.setSlug(f.getSlug());
        piece.setLanguage(f.getLanguage());
        piece.setType(f.getType());
        piece.setBody(body);
        piece.setAuthorName(blankToNull(f.getAuthorName()));
        piece.setReadingTimeMinutes(f.getReadingTimeMinutes() != null
                ? f.getReadingTimeMinutes()
                : estimateMinutes(body));
        piece.setStatus(f.getStatus());
        piece.setFeatured(f.isFeatured());

        LocalDate publishedOn = f.getPublishedOn();
        if (f.getStatus() == PublishStatus.PUBLISHED && publishedOn == null) {
            publishedOn = LocalDate.now();
        }
        piece.setPublishedOn(publishedOn);

        List<Mood> chosen = moods.findAllById(f.getMoodIds());
        piece.getMoods().clear();
        piece.getMoods().addAll(chosen);
    }

    static int estimateMinutes(String body) {
        int words = body.isBlank() ? 0 : body.trim().split("\\s+").length;
        return Math.max(1, (int) Math.ceil(words / (double) WORDS_PER_MINUTE));
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
