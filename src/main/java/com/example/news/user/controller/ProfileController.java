package com.example.news.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.news.category.service.CategoryService;
import com.example.news.common.exception.InvalidOperationException;
import com.example.news.user.entity.Gender;
import com.example.news.user.security.NewsUserPrincipal;
import com.example.news.user.service.ReaderActivityService;
import com.example.news.user.service.UserProfileService;
import com.example.news.user.web.ChangePasswordForm;
import com.example.news.user.web.UserProfileForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userProfileService;
    private final ReaderActivityService readerActivityService;
    private final CategoryService categoryService;

    @GetMapping
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
        populateAccountShell(principal, model, "profile", "Thông tin tài khoản");
        model.addAttribute("genders", Gender.values());
        return "user/profile";
    }

    @PostMapping
    public String updateProfile(
            @AuthenticationPrincipal NewsUserPrincipal principal,
            @Valid @ModelAttribute("profileForm") UserProfileForm profileForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        userProfileService.validateProfileForm(principal.getId(), profileForm, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("changePasswordForm", userProfileService.buildChangePasswordForm());
            model.addAttribute("genders", Gender.values());
            populateAccountShell(principal, model, "profile", "Thông tin tài khoản");
            return "user/profile";
        }

        userProfileService.updateProfile(principal.getId(), profileForm);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật thông tin tài khoản.");
        return "redirect:/profile";
    }

    @PostMapping("/password")
    public String changePassword(
            @AuthenticationPrincipal NewsUserPrincipal principal,
            @Valid @ModelAttribute("changePasswordForm") ChangePasswordForm changePasswordForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        userProfileService.validateChangePasswordForm(principal.getId(), changePasswordForm, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("profileForm", userProfileService.getProfileForm(principal.getId()));
            model.addAttribute("genders", Gender.values());
            populateAccountShell(principal, model, "profile", "Thông tin tài khoản");
            return "user/profile";
        }

        userProfileService.changePassword(principal.getId(), changePasswordForm);
        redirectAttributes.addFlashAttribute("successMessage", "Đã đổi mật khẩu thành công.");
        return "redirect:/profile";
    }

    @PostMapping("/avatar")
    public String updateAvatar(
            @AuthenticationPrincipal NewsUserPrincipal principal,
            @RequestParam("avatarFile") MultipartFile avatarFile,
            RedirectAttributes redirectAttributes) {
        try {
            userProfileService.updateAvatar(principal.getId(), avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật ảnh đại diện.");
        } catch (InvalidOperationException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/profile";
    }

    @GetMapping("/comments")
    public String comments(@AuthenticationPrincipal NewsUserPrincipal principal, Model model) {
        populateAccountShell(principal, model, "comments", "Ý kiến của bạn");
        model.addAttribute("comments", readerActivityService.findUserComments(principal.getId()));
        return "user/comments";
    }

    @GetMapping("/saved")
    public String saved(@AuthenticationPrincipal NewsUserPrincipal principal, Model model) {
        populateAccountShell(principal, model, "saved", "Tin đã lưu");
        model.addAttribute("savedArticles", readerActivityService.findSavedArticles(principal.getId()));
        return "user/saved";
    }

    @GetMapping("/viewed")
    public String viewed(@AuthenticationPrincipal NewsUserPrincipal principal, Model model) {
        populateAccountShell(principal, model, "viewed", "Tin đã xem");
        model.addAttribute("viewedArticles", readerActivityService.findViewedArticles(principal.getId()));
        return "user/viewed";
    }

    private void populateAccountShell(NewsUserPrincipal principal, Model model, String activeKey, String sectionTitle) {
        model.addAttribute("currentUser", userProfileService.getUser(principal.getId()));
        model.addAttribute("activeKey", activeKey);
        model.addAttribute("sectionTitle", sectionTitle);
        model.addAttribute("accountCommentCount", readerActivityService.countUserComments(principal.getId()));
        model.addAttribute("categories", categoryService.findAll());
    }
}
