package com.shashwat.readingsite.service;

import com.shashwat.readingsite.model.Piece;
import com.shashwat.readingsite.model.PieceCollection;
import com.shashwat.readingsite.repository.PieceCollectionRepository;
import com.shashwat.readingsite.repository.PieceRepository;
import com.shashwat.readingsite.web.admin.CollectionForm;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Owner-side create, edit and delete of collections, including piece order. */
@Service
public class CollectionAdminService {

    /** A collection plus how many pieces (of any status) it holds, for the admin list. */
    public record CollectionRow(PieceCollection collection, int pieceCount) {
    }

    private final PieceCollectionRepository collections;
    private final PieceRepository pieces;

    @PersistenceContext
    private EntityManager em;

    public CollectionAdminService(PieceCollectionRepository collections, PieceRepository pieces) {
        this.collections = collections;
        this.pieces = pieces;
    }

    @Transactional(readOnly = true)
    public List<CollectionRow> listAll() {
        return collections.findAllByOrderBySortOrderAscNameAsc().stream()
                .map(c -> new CollectionRow(c, c.getPieces().size()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<CollectionForm> loadForm(Long id) {
        return collections.findById(id).map(c -> CollectionForm.from(c, loadPositions(id)));
    }

    @Transactional(readOnly = true)
    public Optional<String> nameOf(Long id) {
        return collections.findById(id).map(PieceCollection::getName);
    }

    @Transactional(readOnly = true)
    public boolean slugTaken(String slug, Long ownId) {
        return ownId == null ? collections.existsBySlug(slug) : collections.existsBySlugAndIdNot(slug, ownId);
    }

    @Transactional
    public PieceCollection create(CollectionForm form) {
        return save(new PieceCollection(), form);
    }

    /** Returns empty when the collection does not exist. */
    @Transactional
    public Optional<PieceCollection> update(Long id, CollectionForm form) {
        return collections.findById(id).map(c -> save(c, form));
    }

    @Transactional
    public void delete(Long id) {
        collections.deleteById(id);
    }

    private PieceCollection save(PieceCollection c, CollectionForm f) {
        c.setName(f.getName().trim());
        c.setSlug(f.getSlug());
        c.setDescription(f.getDescription() == null || f.getDescription().isBlank()
                ? null : f.getDescription().replace("\r\n", "\n").strip());
        c.setSortOrder(f.getSortOrder() == null ? 0 : f.getSortOrder());
        c.setPublished(f.isPublished());

        List<Long> ids = f.getPieceIds().stream().distinct().toList();
        List<Piece> chosen = pieces.findAllById(ids);
        c.getPieces().clear();
        c.getPieces().addAll(chosen);

        PieceCollection saved = collections.save(c);
        em.flush(); // make sure the join rows exist before setting their positions

        for (Piece p : chosen) {
            Integer position = f.getPositions().get(p.getId());
            em.createNativeQuery("""
                            update collection_pieces set position = :position
                            where collection_id = :collectionId and piece_id = :pieceId
                            """)
                    .setParameter("position", position == null ? 0 : position)
                    .setParameter("collectionId", saved.getId())
                    .setParameter("pieceId", p.getId())
                    .executeUpdate();
        }
        return saved;
    }

    private Map<Long, Integer> loadPositions(Long collectionId) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(
                        "select piece_id, position from collection_pieces where collection_id = :id")
                .setParameter("id", collectionId)
                .getResultList();
        Map<Long, Integer> positions = new LinkedHashMap<>();
        for (Object[] row : rows) {
            positions.put(((Number) row[0]).longValue(), ((Number) row[1]).intValue());
        }
        return positions;
    }
}
