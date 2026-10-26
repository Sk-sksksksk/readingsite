package com.shashwat.readingsite.service;

import com.shashwat.readingsite.model.Piece;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrowseServiceTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Long> countQuery;

    @Mock
    private TypedQuery<Piece> pieceQuery;

    @InjectMocks
    private BrowseService browseService;

    @Test
    void searchShouldReturnResultsSuccessfully() {
        // Step 1: Hum fake count query setup kar rahe hain (man lijiye database mein 5 pieces hain)
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(5L);

        // Step 2: Hum fake pieces ki list setup kar rahe hain
        when(em.createQuery(anyString(), eq(Piece.class))).thenReturn(pieceQuery);
        when(pieceQuery.getResultList()).thenReturn(Collections.emptyList());

        // Step 3: Hum actual search method run karte hain bina kisi filter ke (null, null)
        BrowseResult result = browseService.search(null, null, null, null, 1);

        // Step 4: Test verify kar raha hai ki method ne sahi data return kiya ya nahi
        assertNotNull(result);
        assertEquals(5L, result.total());
    }
}