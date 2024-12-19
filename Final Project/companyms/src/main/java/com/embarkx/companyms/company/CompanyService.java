package com.embarkx.companyms.company;

import java.util.List;

import com.embarkx.companyms.dto.ReviewMessage;

public interface CompanyService {
    List<Company> getAllCompanies();
    boolean updateCompany(Company company, Long id);
    void createCompany(Company company);
    boolean deleteCompanyById(Long id);
    Company getCompanyById(Long id);
    public void updateCompanyRating(ReviewMessage reviewMessage);
}
