package com.example.news.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.news.user.security.NewsUserPrincipal;
import com.example.news.user.service.UserProfileService;
import com.example.news.user.web.ChangePasswordForm;
import com.example.news.user.web.UserProfileForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/profile")
    public String profile(
            @AuthenticationPrincipal NewsUserPrincipal principal,
            Model model,
            @ModelAttribute("profileForm") UserProfileForm profileForm,
            @ModelAttribute("changePasswordForm") ChangePasswordForm changePasswordForm) {
        if (profileForm.getEmail() == null) {
            model.addAttribute("profileForm", userProfileService.getProfileForm(principal.getId()));
        }
        if (changePasswordForm.getCurrentPassword() == null) {
            model.addAttribute("changePasswordForm", userProfileService.buildChangePasswordForm());
        }
        model.addAttribute("currentUser", userProfileService.getUser(principal.getId()));
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @AuthenticationPrincipal NewsUserPrincipal principal,
            @Valid @ModelAttribute("profileForm") UserProfileForm profileForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        userProfileService.validateProfileForm(principal.getId(), profileForm, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("changePasswordForm", userProfileService.buildChangePasswordForm());
            model.addAttribute("currentUser", userProfileService.getUser(principal.getId()));
            return "user/profile";
        }

        userProfileService.updateProfile(principal.getId(), profileForm);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/profile";
    }

    @PostMapping("/profile/password")
    public String changePassword(
            @AuthenticationPrincipal NewsUserPrincipal principal,
            @Valid @ModelAttribute("changePasswordForm") ChangePasswordForm changePasswordForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        userProfileService.validateChangePasswordForm(principal.getId(), changePasswordForm, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("profileForm", userProfileService.getProfileForm(principal.getId()));
            model.addAttribute("currentUser", userProfileService.getUser(principal.getId()));
            return "user/profile";
        }

        userProfileService.changePassword(principal.getId(), changePasswordForm);
        redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
        return "redirect:/profile";
    }
}
