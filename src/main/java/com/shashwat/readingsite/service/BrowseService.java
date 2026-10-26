package com.shashwat.readingsite.service;

import com.shashwat.readingsite.model.Language;
import com.shashwat.readingsite.model.Piece;
import com.shashwat.readingsite.model.PieceType;
import com.shashwat.readingsite.model.PublishStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BrowseService {

    public static final int PAGE_SIZE = 12;

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public BrowseResult search(Language language, PieceType type, String moodSlug, String q, int page) {
        StringBuilder where = new StringBuilder(" where p.status = :status");
        Map<String, Object> params = new HashMap<>();
        params.put("status", PublishStatus.PUBLISHED);

        if (language != null) {
            where.append(" and p.language = :language");
            params.put("language", language);
        }
        if (type != null) {
            where.append(" and p.type = :type");
            params.put("type", type);
        }
        if (moodSlug != null && !moodSlug.isBlank()) {
            where.append(" and exists (select m.id from p.moods m where m.slug = :mood)");
            params.put("mood", moodSlug);
        }
        if (q != null && !q.isBlank()) {
            where.append(" and (lower(p.title) like :q escape '!' or lower(p.body) like :q escape '!')");
            params.put("q", likePattern(q.trim()));
        }

        TypedQuery<Long> countQuery = em.createQuery("select count(p) from Piece p" + where, Long.class);
        params.forEach(countQuery::setParameter);
        long total = countQuery.getSingleResult();

        int totalPages = (int) Math.max(1, Math.ceil(total / (double) PAGE_SIZE));
        int safePage = Math.min(Math.max(page, 1), totalPages);

        TypedQuery<Piece> query = em.createQuery(
                "select p from Piece p" + where + " order by p.publishedOn desc nulls last, p.id desc",
                Piece.class);
        params.forEach(query::setParameter);
        query.setFirstResult((safePage - 1) * PAGE_SIZE);
        query.setMaxResults(PAGE_SIZE);
        List<Piece> pieces = query.getResultList();

        return new BrowseResult(pieces, total, safePage, totalPages);
    }

    // Escapes % and _ so a reader's text is searched literally
    private static String likePattern(String q) {
        String escaped = q.toLowerCase()
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return "%" + escaped + "%";
    }
}