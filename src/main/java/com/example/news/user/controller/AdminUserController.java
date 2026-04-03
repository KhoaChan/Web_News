package com.example.news.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.news.user.entity.Role;
import com.example.news.user.security.NewsUserPrincipal;
import com.example.news.user.service.UserManagementService;
import com.example.news.user.web.AdminUserForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminUserController {

    private final UserManagementService userManagementService;

    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userManagementService.findAllUsers());
        return "admin/user-list";
    }

    @GetMapping("/admin/user/create")
    public String createUserForm(Model model) {
        populateFormModel(model, userManagementService.buildCreateForm());
        return "admin/user-form";
    }

    @GetMapping("/admin/user/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        populateFormModel(model, userManagementService.getAdminUserForm(id));
        return "admin/user-form";
    }

    @PostMapping("/admin/user/save")
    public String saveUser(
            @AuthenticationPrincipal NewsUserPrincipal principal,
            @Valid @ModelAttribute("userForm") AdminUserForm userForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        userManagementService.validateAdminUserForm(principal.getId(), userForm, bindingResult);
        if (bindingResult.hasErrors()) {
            populateFormModel(model, userForm);
            return "admin/user-form";
        }

        if (userForm.getId() == null) {
            userManagementService.createUser(userForm);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully.");
        } else {
            userManagementService.updateUser(principal.getId(), userForm);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully.");
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/admin/user/toggle-status/{id}")
    public String toggleStatus(
            @AuthenticationPrincipal NewsUserPrincipal principal,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        var updatedUser = userManagementService.toggleEnabled(principal.getId(), id);
        String message = updatedUser.isEnabled() ? "User enabled successfully." : "User disabled successfully.";
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/admin/users";
    }

    private void populateFormModel(Model model, AdminUserForm userForm) {
        model.addAttribute("userForm", userForm);
        model.addAttribute("roles", Role.values());
    }
}
