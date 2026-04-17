describe("News browse and search flow", () => {
  it("navigates home, search, category, and detail pages", () => {
    cy.visit("/");
    cy.contains("News").should("be.visible");

    cy.get("input[name='keyword']").type("laptop{enter}");
    cy.url().should("include", "/search");

    cy.visit("/category/the-thao");
    cy.url().should("include", "/category/the-thao");

    cy.visit("/article/doi-tuyen-viet-nam-chuan-bi-cho-giai-dau-lon");
    cy.url().should("include", "/article/doi-tuyen-viet-nam-chuan-bi-cho-giai-dau-lon");
    cy.contains("Bình luận").should("be.visible");
  });
});
