package com.example.news.article.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.news.article.service.ArticleWorkflowService;
import com.example.news.comment.service.CommentModerationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/editor")
@RequiredArgsConstructor
public class EditorWorkflowController {

    private final ArticleWorkflowService articleWorkflowService;
    private final CommentModerationService commentModerationService;

    @GetMapping({"", "/articles"})
    public String dashboard(Model model) {
        model.addAttribute("reviewQueue", articleWorkflowService.findEditorReviewQueue());
        model.addAttribute("publishedArticles", articleWorkflowService.findEditorPublishedArticles());
        model.addAttribute("cancelledArticles", articleWorkflowService.findEditorCancelledArticles());
        model.addAttribute("pendingCommentCount", commentModerationService.countPendingComments());
        populateLayoutModel(
                model,
                "Hàng đợi duyệt",
                "Xem xét bài gửi, xuất bản bài đạt yêu cầu và giữ luồng biên tập luôn thông suốt.",
                "editor_queue");
        return "editor/dashboard";
    }

    @GetMapping("/article/review/{id}")
    public String reviewArticle(@PathVariable Long id, Model model) {
        model.addAttribute("article", articleWorkflowService.getArticleForEditorReview(id));
        populateLayoutModel(
                model,
                "Duyệt bài viết",
                "Xem nội dung bài gửi và quyết định xuất bản, yêu cầu chỉnh sửa hoặc hủy bài.",
                "editor_queue");
        return "editor/article-review";
    }

    @PostMapping("/article/publish/{id}")
    public String publishArticle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        articleWorkflowService.publish(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xuất bản bài viết thành công.");
        return "redirect:/editor";
    }

    @PostMapping("/article/request-changes/{id}")
    public String requestChanges(
            @PathVariable Long id,
            @RequestParam(required = false) String reviewNote,
            RedirectAttributes redirectAttributes) {
        articleWorkflowService.requestChanges(id, reviewNote);
        redirectAttributes.addFlashAttribute("successMessage", "Đã gửi lại bài viết để tác giả chỉnh sửa.");
        return "redirect:/editor";
    }

    @PostMapping("/article/cancel/{id}")
    public String cancelArticle(
            @PathVariable Long id,
            @RequestParam(required = false) String reviewNote,
            RedirectAttributes redirectAttributes) {
        articleWorkflowService.cancelByEditor(id, reviewNote);
        redirectAttributes.addFlashAttribute("successMessage", "Đã hủy bài viết thành công.");
        return "redirect:/editor";
    }

    @GetMapping("/comments")
    public String comments(Model model) {
        model.addAttribute("pendingComments", commentModerationService.findPendingComments());
        populateLayoutModel(
                model,
                "Bình luận chờ duyệt",
                "Duyệt hoặc từ chối bình luận của độc giả trước khi hiển thị công khai.",
                "editor_comments");
        return "editor/comments";
    }

    @PostMapping("/comment/approve/{id}")
    public String approveComment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        commentModerationService.approveComment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã duyệt bình luận thành công.");
        return "redirect:/editor/comments";
    }

    @PostMapping("/comment/reject/{id}")
    public String rejectComment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        commentModerationService.rejectComment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối bình luận.");
        return "redirect:/editor/comments";
    }

    private void populateLayoutModel(Model model, String pageTitle, String pageSubtitle, String activeKey) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("pageSubtitle", pageSubtitle);
        model.addAttribute("activeKey", activeKey);
    }
}
