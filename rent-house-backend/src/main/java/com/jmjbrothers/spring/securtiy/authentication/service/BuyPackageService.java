package com.jmjbrothers.spring.securtiy.authentication.service;


import com.jmjbrothers.spring.securtiy.authentication.dto.BuyPackageDto;
import com.jmjbrothers.spring.securtiy.authentication.dto.PurchaseHistoryDto;
import com.jmjbrothers.spring.securtiy.authentication.model.CreditPackage;
import com.jmjbrothers.spring.securtiy.authentication.model.BuyPackage;
import com.jmjbrothers.spring.securtiy.authentication.model.User;
import com.jmjbrothers.spring.securtiy.authentication.repository.CreditPackageRepository;
import com.jmjbrothers.spring.securtiy.authentication.repository.BuyPackageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuyPackageService {

    private final BuyPackageRepository creditTransactionRepository;
    private final CreditPackageRepository creditPackageRepository;
    private final UserInfoDetailsService userInfoDetailsService;

    public BuyPackageService(BuyPackageRepository transactionRepository, CreditPackageRepository creditPackageRepository, UserInfoDetailsService userInfoDetailsService) {
        this.creditTransactionRepository = transactionRepository;
        this.creditPackageRepository = creditPackageRepository;
        this.userInfoDetailsService = userInfoDetailsService;
    }


    @Transactional
    public BuyPackage buyPackage(BuyPackageDto transactionDto) {
        Long packageId = transactionDto.getCreditPackageId();
        Long userId = transactionDto.getUserId();
        CreditPackage creditPackage = creditPackageRepository.findById(packageId).orElse(null);
        User user = userInfoDetailsService.userFindById(userId);

        if (creditPackage == null || user == null)
            throw new RuntimeException("User or package not found!");



        BuyPackage creditTransaction = new BuyPackage();
        creditTransaction.setCreditPackage(creditPackage);
        creditTransaction.setAmountPaid(creditPackage.getPrice());
        creditTransaction.setCreditsPurchased(creditPackage.getCreditAmount());

            //get the user credit balance from user
            Integer balanceCredits =user.getBalanceCredits();
            //get the package credit amount from credit package
            Integer packageCreditAmount = creditPackage.getCreditAmount();

            //set the user credit balance after the package purchase and update credit balance
            user.setBalanceCredits(balanceCredits+packageCreditAmount);

        creditTransaction.setUser(user);



        return creditTransactionRepository.save(creditTransaction);

    }

    @Transactional
    public List<PurchaseHistoryDto> allPropertyUnlockById(Long id) {
        List<BuyPackage> myPurchaseHistory = creditTransactionRepository.findAllByUserId(id);

        return myPurchaseHistory.stream().map(this::responseHistoryDto).collect(Collectors.toList());
    }

    private PurchaseHistoryDto responseHistoryDto(BuyPackage buyPackage) {

        PurchaseHistoryDto dto = new PurchaseHistoryDto();
        dto.setPackageName(buyPackage.getCreditPackage().getName());
        dto.setCreditsPurchased(buyPackage.getCreditsPurchased());
        dto.setAmountPaid(buyPackage.getAmountPaid());
        dto.setDatePurchased(buyPackage.getDatePurchased());

        return dto;
    }


    @Transactional
    public List<PurchaseHistoryDto> allPropertyUnlock() {
        List<BuyPackage> myPurchaseHistory = creditTransactionRepository.findAll();
        return myPurchaseHistory.stream().map(this::responseHistoryDto).collect(Collectors.toList());


    }
}
