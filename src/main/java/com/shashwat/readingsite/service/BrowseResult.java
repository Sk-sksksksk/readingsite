package com.shashwat.readingsite.service;

import com.shashwat.readingsite.model.Piece;

import java.util.List;

public record BrowseResult(List<Piece> pieces, long total, int page, int totalPages) {
}