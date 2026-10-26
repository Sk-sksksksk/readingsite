package com.shashwat.readingsite.service;

import com.shashwat.readingsite.model.Language;
import com.shashwat.readingsite.model.Piece;
import com.shashwat.readingsite.model.PieceCollection;
import com.shashwat.readingsite.model.PublishStatus;
import com.shashwat.readingsite.repository.PieceCollectionRepository;
import com.shashwat.readingsite.repository.PieceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Public (reader-facing) access to collections. */
@Service
public class CollectionService {

    public record CollectionSummary(PieceCollection collection, long pieceCount) {
    }

    public record CollectionDetail(PieceCollection collection, List<Piece> pieces) {
    }

    private final PieceCollectionRepository collections;
    private final PieceRepository pieces;

    public CollectionService(PieceCollectionRepository collections, PieceRepository pieces) {
        this.collections = collections;
        this.pieces = pieces;
    }

    @Transactional(readOnly = true)
    public List<CollectionSummary> listPublished() {
        return collections.findByPublishedTrueOrderBySortOrderAsc().stream()
                .map(c -> new CollectionSummary(c, collections.countPiecesByStatus(c.getId(), PublishStatus.PUBLISHED)))
                .toList();
    }

    /** The collection with its published pieces in the owner's order, optionally limited to one language. */
    @Transactional(readOnly = true)
    public Optional<CollectionDetail> findPublished(String slug, Language language) {
        return collections.findBySlugAndPublishedTrue(slug).map(c -> {
            List<Piece> all = pieces.findPublishedInCollection(c.getId());
            List<Piece> shown = language == null
                    ? all
                    : all.stream().filter(p -> p.getLanguage() == language).toList();
            return new CollectionDetail(c, shown);
        });
    }
}
