package com.jmjbrothers.spring.securtiy.authentication.service;

import com.jmjbrothers.spring.securtiy.authentication.model.CreditPackage;
import com.jmjbrothers.spring.securtiy.authentication.repository.CreditPackageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CreditPackageService {

    @Autowired
    private CreditPackageRepository packageRepository;


    @Transactional
    public CreditPackage addCreditPackage(CreditPackage cPackage) {
        return packageRepository.save(cPackage);
    }

    @Transactional
    public List<CreditPackage> allCreditPackage() {
        return packageRepository.findAll();
    }

    @Transactional
    public CreditPackage updatePackage(CreditPackage editBody) {

        CreditPackage creditPackage = packageRepository.findById(editBody.getId()).orElse(null);

        assert creditPackage != null;
        creditPackage.setName(editBody.getName());
        creditPackage.setCreditAmount(editBody.getCreditAmount());
        creditPackage.setPrice(editBody.getPrice());

        return creditPackage;

    }

    @Transactional
    public String deletePackageById(Long id) {
        packageRepository.deleteById(id);
        return "Delete package successfully.";
    }
}
