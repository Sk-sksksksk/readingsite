package com.shashwat.readingsite.repository;

import com.shashwat.readingsite.model.PieceCollection;
import com.shashwat.readingsite.model.PublishStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PieceCollectionRepository extends JpaRepository<PieceCollection, Long> {

    List<PieceCollection> findByPublishedTrueOrderBySortOrderAsc();

    Optional<PieceCollection> findBySlug(String slug);

    @Query("""
           select c from PieceCollection c join c.pieces p
           where p.id = :pieceId and c.published = true
           order by c.sortOrder
           """)
    List<PieceCollection> findPublishedByPieceId(@Param("pieceId") Long pieceId);

    Optional<PieceCollection> findBySlugAndPublishedTrue(String slug);

    List<PieceCollection> findAllByOrderBySortOrderAscNameAsc();

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    @Query("""
           select count(p) from PieceCollection c join c.pieces p
           where c.id = :id and p.status = :status
           """)
    long countPiecesByStatus(@Param("id") Long id, @Param("status") PublishStatus status);
}
