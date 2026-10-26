package com.shashwat.readingsite.web.admin;

import com.shashwat.readingsite.model.Language;
import com.shashwat.readingsite.model.Mood;
import com.shashwat.readingsite.model.Piece;
import com.shashwat.readingsite.model.PieceType;
import com.shashwat.readingsite.model.PublishStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Form-backing object for creating and editing a piece. */
public class PieceForm {

    @NotBlank(message = "Enter a title.")
    @Size(max = 255, message = "Keep the title under 255 characters.")
    private String title;

    @Size(max = 255, message = "Keep the web address under 255 characters.")
    private String slug;

    @NotNull(message = "Choose a language.")
    private Language language;

    @NotNull(message = "Choose a type.")
    private PieceType type;

    @NotBlank(message = "Enter the text of the piece.")
    private String body;

    @Size(max = 150, message = "Keep the author name under 150 characters.")
    private String authorName;

    @Min(value = 1, message = "Reading time must be at least 1 minute.")
    @Max(value = 600, message = "Reading time must be 600 minutes or less.")
    private Integer readingTimeMinutes;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate publishedOn;

    @NotNull(message = "Choose a status.")
    private PublishStatus status = PublishStatus.DRAFT;

    private boolean featured;

    private List<Long> moodIds = new ArrayList<>();

    public static PieceForm from(Piece p) {
        PieceForm f = new PieceForm();
        f.title = p.getTitle();
        f.slug = p.getSlug();
        f.language = p.getLanguage();
        f.type = p.getType();
        f.body = p.getBody();
        f.authorName = p.getAuthorName();
        f.readingTimeMinutes = p.getReadingTimeMinutes();
        f.publishedOn = p.getPublishedOn();
        f.status = p.getStatus();
        f.featured = p.isFeatured();
        f.moodIds = new ArrayList<>(p.getMoods().stream().map(Mood::getId).toList());
        return f;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }

    public PieceType getType() { return type; }
    public void setType(PieceType type) { this.type = type; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public Integer getReadingTimeMinutes() { return readingTimeMinutes; }
    public void setReadingTimeMinutes(Integer readingTimeMinutes) { this.readingTimeMinutes = readingTimeMinutes; }

    public LocalDate getPublishedOn() { return publishedOn; }
    public void setPublishedOn(LocalDate publishedOn) { this.publishedOn = publishedOn; }

    public PublishStatus getStatus() { return status; }
    public void setStatus(PublishStatus status) { this.status = status; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public List<Long> getMoodIds() { return moodIds; }
    public void setMoodIds(List<Long> moodIds) { this.moodIds = moodIds == null ? new ArrayList<>() : moodIds; }
}
