package com.example.news.config;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.news.user.entity.Role;
import com.example.news.user.entity.User;
import com.example.news.user.repository.UserRepository;
import com.example.news.user.security.NewsUserPrincipal;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User adminUser;
    private User editorUser;
    private User authorUser;
    private User readerUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        adminUser = createUser("admin", "admin@example.com", "System Administrator", Role.ADMIN, true);
        editorUser = createUser("editor", "editor@example.com", "Editor Account", Role.EDITOR, true);
        authorUser = createUser("author", "author@example.com", "Author Account", Role.AUTHOR, true);
        readerUser = createUser("reader", "reader@example.com", "Reader Account", Role.USER, true);
    }

    @Test
    void loginPageShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void registerPageShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    void anonymousUserShouldBeRedirectedFromAdmin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void anonymousUserShouldBeRedirectedFromProfile() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void nonAdminUserShouldBeForbiddenFromAdminUsers() throws Exception {
        mockMvc.perform(get("/admin/users").with(user(principal(readerUser))))
                .andExpect(status().isForbidden());
    }

    @Test
    void userShouldBeForbiddenFromAuthorArea() throws Exception {
        mockMvc.perform(get("/author").with(user(principal(readerUser))))
                .andExpect(status().isForbidden());
    }

    @Test
    void userShouldBeForbiddenFromEditorArea() throws Exception {
        mockMvc.perform(get("/editor").with(user(principal(readerUser))))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUserShouldAccessAdminUsers() throws Exception {
        mockMvc.perform(get("/admin/users").with(user(principal(adminUser))))
                .andExpect(status().isOk());
    }

    @Test
    void authorUserShouldAccessAuthorArea() throws Exception {
        mockMvc.perform(get("/author").with(user(principal(authorUser))))
                .andExpect(status().isOk());
    }

    @Test
    void editorUserShouldAccessEditorArea() throws Exception {
        mockMvc.perform(get("/editor").with(user(principal(editorUser))))
                .andExpect(status().isOk());
    }

    @Test
    void adminDashboardShouldShowAdminEditorialAndAuthorMenus() throws Exception {
        mockMvc.perform(get("/admin").with(user(principal(adminUser))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Khu vực quản trị News")))
                .andExpect(content().string(containsString("Thu gọn thanh bên")))
                .andExpect(content().string(containsString("/admin/users")))
                .andExpect(content().string(containsString("/editor/comments")))
                .andExpect(content().string(containsString("/author/article/create")))
                .andExpect(content().string(containsString("Quay về trang tin")));
    }

    @Test
    void editorDashboardShouldHideAdminAndAuthorMenus() throws Exception {
        mockMvc.perform(get("/editor").with(user(principal(editorUser))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Khu vực quản trị News")))
                .andExpect(content().string(containsString("Thu gọn thanh bên")))
                .andExpect(content().string(containsString("/editor/comments")))
                .andExpect(content().string(not(containsString("/admin/users"))))
                .andExpect(content().string(not(containsString("/admin/categories"))))
                .andExpect(content().string(not(containsString("/author/article/create"))))
                .andExpect(content().string(containsString("Quay về trang tin")));
    }

    @Test
    void authorDashboardShouldHideAdminAndEditorialMenus() throws Exception {
        mockMvc.perform(get("/author").with(user(principal(authorUser))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Khu vực quản trị News")))
                .andExpect(content().string(containsString("Thu gọn thanh bên")))
                .andExpect(content().string(containsString("/author/article/create")))
                .andExpect(content().string(not(containsString("/editor/comments"))))
                .andExpect(content().string(not(containsString("/admin/users"))))
                .andExpect(content().string(containsString("Quay về trang tin")));
    }

    @Test
    void adminProfileShouldUseBackofficeLayoutAndShowAllMenus() throws Exception {
        mockMvc.perform(get("/profile").with(user(principal(adminUser))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Khu vực quản trị News")))
                .andExpect(content().string(containsString("Thu gọn thanh bên")))
                .andExpect(content().string(containsString("/admin/users")))
                .andExpect(content().string(containsString("/editor/comments")))
                .andExpect(content().string(containsString("/author/article/create")))
                .andExpect(content().string(containsString("Quay về trang tin")));
    }

    @Test
    void editorProfileShouldUseBackofficeLayoutWithoutAdminMenus() throws Exception {
        mockMvc.perform(get("/profile").with(user(principal(editorUser))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Khu vực quản trị News")))
                .andExpect(content().string(containsString("Thu gọn thanh bên")))
                .andExpect(content().string(containsString("/editor/comments")))
                .andExpect(content().string(not(containsString("/admin/users"))))
                .andExpect(content().string(not(containsString("/author/article/create"))))
                .andExpect(content().string(containsString("Quay về trang tin")));
    }

    @Test
    void authorProfileShouldUseBackofficeLayoutWithoutEditorialMenus() throws Exception {
        mockMvc.perform(get("/profile").with(user(principal(authorUser))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Khu vực quản trị News")))
                .andExpect(content().string(containsString("Thu gọn thanh bên")))
                .andExpect(content().string(containsString("/author/article/create")))
                .andExpect(content().string(not(containsString("/editor/comments"))))
                .andExpect(content().string(not(containsString("/admin/users"))))
                .andExpect(content().string(containsString("Quay về trang tin")));
    }

    @Test
    void userProfileShouldStayOutsideBackofficeLayout() throws Exception {
        mockMvc.perform(get("/profile").with(user(principal(readerUser))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Quay về trang chủ")))
                .andExpect(content().string(not(containsString("Khu vực quản trị News"))))
                .andExpect(content().string(not(containsString("/admin/users"))))
                .andExpect(content().string(not(containsString("/editor/comments"))))
                .andExpect(content().string(not(containsString("/author/article/create"))));
    }

    @Test
    void sharedLayoutShouldRenderOnBackofficeForms() throws Exception {
        mockMvc.perform(get("/admin/article/create").with(user(principal(adminUser))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Khu vực quản trị News")))
                .andExpect(content().string(containsString("Thu gọn thanh bên")));

        mockMvc.perform(get("/admin/user/create").with(user(principal(adminUser))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Khu vực quản trị News")))
                .andExpect(content().string(containsString("Thu gọn thanh bên")));

        mockMvc.perform(get("/author/article/create").with(user(principal(authorUser))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Khu vực quản trị News")))
                .andExpect(content().string(containsString("Thu gọn thanh bên")));

        mockMvc.perform(get("/editor/comments").with(user(principal(editorUser))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Khu vực quản trị News")))
                .andExpect(content().string(containsString("Thu gọn thanh bên")));
    }

    @Test
    void adminLoginShouldRedirectToAdminDashboard() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    void editorLoginShouldRedirectToEditorDashboard() throws Exception {
        mockMvc.perform(formLogin("/login").user("editor").password("123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/editor"));
    }

    @Test
    void authorLoginShouldRedirectToAuthorDashboard() throws Exception {
        mockMvc.perform(formLogin("/login").user("author").password("123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/author"));
    }

    @Test
    void userLoginShouldRedirectToHome() throws Exception {
        mockMvc.perform(formLogin("/login").user("reader").password("123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    private User createUser(String username, String email, String fullName, Role role, boolean enabled) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole(role);
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    private NewsUserPrincipal principal(User user) {
        return new NewsUserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled());
    }
}
