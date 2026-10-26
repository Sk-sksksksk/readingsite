package com.shashwat.readingsite.web.admin;

import com.shashwat.readingsite.model.PieceCollection;
import com.shashwat.readingsite.repository.PieceRepository;
import com.shashwat.readingsite.service.CollectionAdminService;
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

import java.util.Locale;

@Controller
@RequestMapping("/admin/collections")
public class AdminCollectionController {

    private final CollectionAdminService service;
    private final PieceRepository pieceRepository;

    public AdminCollectionController(CollectionAdminService service, PieceRepository pieceRepository) {
        this.service = service;
        this.pieceRepository = pieceRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("collections", service.listAll());
        return "admin/collections";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new CollectionForm());
        addFormData(model, null);
        return "admin/collection-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") CollectionForm form, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        resolveSlug(form, null, result);
        if (result.hasErrors()) {
            addFormData(model, null);
            return "admin/collection-form";
        }
        PieceCollection saved = service.create(form);
        redirect.addFlashAttribute("notice", "Saved “" + saved.getName() + "”.");
        return "redirect:/admin/collections";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        CollectionForm form = service.loadForm(id).orElseThrow(AdminCollectionController::notFound);
        model.addAttribute("form", form);
        addFormData(model, id);
        return "admin/collection-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") CollectionForm form,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        resolveSlug(form, id, result);
        if (result.hasErrors()) {
            addFormData(model, id);
            return "admin/collection-form";
        }
        PieceCollection saved = service.update(id, form).orElseThrow(AdminCollectionController::notFound);
        redirect.addFlashAttribute("notice", "Saved “" + saved.getName() + "”.");
        return "redirect:/admin/collections";
    }

    @GetMapping("/{id}/delete")
    public String confirmDelete(@PathVariable Long id, Model model) {
        String name = service.nameOf(id).orElseThrow(AdminCollectionController::notFound);
        model.addAttribute("what", "collection");
        model.addAttribute("name", name);
        model.addAttribute("action", "/admin/collections/" + id + "/delete");
        model.addAttribute("cancelUrl", "/admin/collections");
        model.addAttribute("note", "The pieces inside it are not deleted.");
        return "admin/confirm-delete";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        String name = service.nameOf(id).orElseThrow(AdminCollectionController::notFound);
        service.delete(id);
        redirect.addFlashAttribute("notice", "Deleted “" + name + "”.");
        return "redirect:/admin/collections";
    }

    private void addFormData(Model model, Long collectionId) {
        model.addAttribute("collectionId", collectionId);
        model.addAttribute("pieceOptions", pieceRepository.findAllByOrderByTitleAsc());
    }

    private void resolveSlug(CollectionForm form, Long ownId, BindingResult result) {
        if (result.hasFieldErrors("slug")) {
            return;
        }
        String slug = form.getSlug() == null ? "" : form.getSlug().trim().toLowerCase(Locale.ROOT);
        if (slug.isEmpty()) {
            slug = Slugs.from(form.getName(), 100);
        }
        if (slug.isEmpty()) {
            result.rejectValue("slug", "slug.required",
                    "Enter a web address using Latin letters, for example quiet-evening.");
        } else if (!Slugs.isValid(slug)) {
            result.rejectValue("slug", "slug.invalid",
                    "Use only lowercase Latin letters, digits and single hyphens, for example quiet-evening.");
        } else if (service.slugTaken(slug, ownId)) {
            result.rejectValue("slug", "slug.taken", "Another collection already uses this web address.");
        } else {
            form.setSlug(slug);
        }
    }

    private static ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
