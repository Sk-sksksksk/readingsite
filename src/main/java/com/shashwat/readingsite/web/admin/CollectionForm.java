package com.shashwat.readingsite.web.admin;

import com.shashwat.readingsite.model.PieceCollection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Form-backing object for creating and editing a collection. */
public class CollectionForm {

    @NotBlank(message = "Enter a name.")
    @Size(max = 150, message = "Keep the name under 150 characters.")
    private String name;

    @Size(max = 150, message = "Keep the web address under 150 characters.")
    private String slug;

    @Size(max = 2000, message = "Keep the description under 2000 characters.")
    private String description;

    @Min(value = 0, message = "Enter 0 or higher.")
    @Max(value = 9999, message = "Enter 9999 or lower.")
    private Integer sortOrder = 0;

    private boolean published = true;

    private List<Long> pieceIds = new ArrayList<>();

    /** Position of each piece inside the collection, keyed by piece id. Lower numbers come first. */
    private Map<Long, Integer> positions = new LinkedHashMap<>();

    public static CollectionForm from(PieceCollection c, Map<Long, Integer> positions) {
        CollectionForm f = new CollectionForm();
        f.name = c.getName();
        f.slug = c.getSlug();
        f.description = c.getDescription();
        f.sortOrder = c.getSortOrder();
        f.published = c.isPublished();
        f.pieceIds = new ArrayList<>(c.getPieces().stream().map(p -> p.getId()).toList());
        f.positions = new LinkedHashMap<>(positions);
        return f;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public List<Long> getPieceIds() { return pieceIds; }
    public void setPieceIds(List<Long> pieceIds) { this.pieceIds = pieceIds == null ? new ArrayList<>() : pieceIds; }

    public Map<Long, Integer> getPositions() { return positions; }
    public void setPositions(Map<Long, Integer> positions) {
        this.positions = positions == null ? new LinkedHashMap<>() : positions;
    }
}
