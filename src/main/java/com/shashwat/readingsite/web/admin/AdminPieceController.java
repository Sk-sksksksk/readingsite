package com.shashwat.readingsite.web.admin;

import com.shashwat.readingsite.model.Language;
import com.shashwat.readingsite.model.Piece;
import com.shashwat.readingsite.model.PieceType;
import com.shashwat.readingsite.model.PublishStatus;
import com.shashwat.readingsite.repository.MoodRepository;
import com.shashwat.readingsite.service.PieceAdminService;
import com.shashwat.readingsite.service.Slugs;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin/pieces")
public class AdminPieceController {

    private final PieceAdminService service;
    private final MoodRepository moodRepository;

    public AdminPieceController(PieceAdminService service, MoodRepository moodRepository) {
        this.service = service;
        this.moodRepository = moodRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pieces", service.listAll());
        return "admin/pieces";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new PieceForm());
        addFormData(model, null);
        return "admin/piece-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") PieceForm form, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        resolveSlug(form, null, result);
        if (result.hasErrors()) {
            addFormData(model, null);
            return "admin/piece-form";
        }
        Piece saved = service.create(form);
        redirect.addFlashAttribute("notice", "Saved “" + saved.getTitle() + "”.");
        return "redirect:/admin/pieces";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        PieceForm form = service.loadForm(id).orElseThrow(AdminPieceController::notFound);
        model.addAttribute("form", form);
        addFormData(model, id);
        return "admin/piece-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") PieceForm form,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        resolveSlug(form, id, result);
        if (result.hasErrors()) {
            addFormData(model, id);
            return "admin/piece-form";
        }
        Piece saved = service.update(id, form).orElseThrow(AdminPieceController::notFound);
        redirect.addFlashAttribute("notice", "Saved “" + saved.getTitle() + "”.");
        return "redirect:/admin/pieces";
    }

    /** Shows a draft (or published piece) exactly as readers will see it. */
    @GetMapping("/{id}/preview")
    public String preview(@PathVariable Long id, Model model) {
        Piece piece = service.findForPreview(id).orElseThrow(AdminPieceController::notFound);
        model.addAttribute("piece", piece);
        model.addAttribute("related", List.of());
        model.addAttribute("collections", List.of());
        model.addAttribute("preview", true);
        return "read";
    }

    @GetMapping("/{id}/delete")
    public String confirmDelete(@PathVariable Long id, Model model) {
        String title = service.titleOf(id).orElseThrow(AdminPieceController::notFound);
        model.addAttribute("what", "piece");
        model.addAttribute("name", title);
        model.addAttribute("action", "/admin/pieces/" + id + "/delete");
        model.addAttribute("cancelUrl", "/admin/pieces");
        return "admin/confirm-delete";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        String title = service.titleOf(id).orElseThrow(AdminPieceController::notFound);
        service.delete(id);
        redirect.addFlashAttribute("notice", "Deleted “" + title + "”.");
        return "redirect:/admin/pieces";
    }

    private void addFormData(Model model, Long pieceId) {
        model.addAttribute("pieceId", pieceId);
        model.addAttribute("moods", moodRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("languages", Language.values());
        model.addAttribute("types", PieceType.values());
        model.addAttribute("statuses", PublishStatus.values());
    }

    /** Fills in the slug from the title when left blank, and checks it is valid and unused. */
    private void resolveSlug(PieceForm form, Long ownId, BindingResult result) {
        if (result.hasFieldErrors("slug")) {
            return;
        }
        String slug = form.getSlug() == null ? "" : form.getSlug().trim().toLowerCase(Locale.ROOT);
        if (slug.isEmpty()) {
            slug = Slugs.from(form.getTitle(), 100);
        }
        if (slug.isEmpty()) {
            result.rejectValue("slug", "slug.required",
                    "Enter a web address using Latin letters, for example barish-ke-baad.");
        } else if (!Slugs.isValid(slug)) {
            result.rejectValue("slug", "slug.invalid",
                    "Use only lowercase Latin letters, digits and single hyphens, for example barish-ke-baad.");
        } else if (service.slugTaken(slug, ownId)) {
            result.rejectValue("slug", "slug.taken", "Another piece already uses this web address.");
        } else {
            form.setSlug(slug);
        }
    }

    private static ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
