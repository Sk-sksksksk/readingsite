package com.shashwat.readingsite.repository;

import com.shashwat.readingsite.model.Language;
import com.shashwat.readingsite.model.Piece;
import com.shashwat.readingsite.model.PublishStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PieceRepository extends JpaRepository<Piece, Long> {

    Optional<Piece> findBySlugAndStatus(String slug, PublishStatus status);

    Optional<Piece> findFirstByStatusAndFeaturedTrueOrderByPublishedOnDesc(PublishStatus status);

    List<Piece> findTop6ByStatusOrderByPublishedOnDesc(PublishStatus status);

    // Loads the piece together with its moods in one query
    @Query("select p from Piece p left join fetch p.moods where p.slug = :slug and p.status = :status")
    Optional<Piece> findDetailBySlug(@Param("slug") String slug, @Param("status") PublishStatus status);

    // Other pieces in the same language that share at least one mood
    @Query("""
           select distinct p from Piece p join p.moods m
           where p.status = :status and p.language = :language
             and p.id <> :id and m.id in :moodIds
           order by p.publishedOn desc
           """)
    List<Piece> findRelated(@Param("status") PublishStatus status,
                            @Param("language") Language language,
                            @Param("id") Long id,
                            @Param("moodIds") Collection<Long> moodIds,
                            Pageable pageable);

    // ---- Admin ----

    List<PieceSummary> findAllByOrderByUpdatedAtDesc();

    List<PieceSummary> findAllByOrderByTitleAsc();

    List<PieceSummary> findTop5ByOrderByUpdatedAtDesc();

    long countByStatus(PublishStatus status);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    // Any status, with moods loaded, for the edit form and draft preview
    @Query("select p from Piece p left join fetch p.moods where p.id = :id")
    Optional<Piece> findDetailById(@Param("id") Long id);

    // Published pieces of a collection in the owner's chosen order (collection_pieces.position)
    @Query(value = """
           select p.* from pieces p
           join collection_pieces cp on cp.piece_id = p.id
           where cp.collection_id = :collectionId and p.status = 'PUBLISHED'
           order by cp.position asc, p.published_on desc nulls last, p.id desc
           """, nativeQuery = true)
    List<Piece> findPublishedInCollection(@Param("collectionId") Long collectionId);
}
