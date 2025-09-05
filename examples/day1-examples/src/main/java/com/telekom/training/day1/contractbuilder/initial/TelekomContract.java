package com.telekom.training.day1.contractbuilder.initial;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * ANTI-PATTERN: Long Parameter Constructor & Telescoping Constructor Pattern
 * 
 * Diese Klasse zeigt die typischen Probleme von komplexen Objekten
 * ohne Builder Pattern:
 * 
 * Code-Smells:
 * - Constructor mit 15+ Parametern
 * - Telescoping Constructor Pattern (multiple overloads)
 * - Parameter-Reihenfolge ist wichtig und fehleranfällig
 * - Schwer lesbar bei der Instanzierung
 * - Schwer erweiterbar (neue Parameter = Breaking Changes)
 * - Optional Parameters werden mit null/default values behandelt
 * - Keine Validation während der Konstruktion
 */
public class TelekomContract {
    
    // Viele Felder die konfiguriert werden müssen
    private String contractId;
    private String customerId;
    private String contractType;
    private String internetSpeed;
    private String internetTechnology;
    private String phoneNumber;
    private String phoneFeatures;
    private String tvPackage;
    private String tvEquipment;
    private double internetPrice;
    private double phonePrice;
    private double tvPrice;
    private double installationFee;
    private double discountPercentage;
    private int contractDurationMonths;
    private LocalDate startDate;
    private LocalDate endDate;
    private String billingCycle;
    private String installationAddress;
    private String billingAddress;
    private String technicalContact;
    private String salesPerson;
    private String specialConditions;
    private boolean autoRenewal;
    private boolean paperlessBilling;
    private String paymentMethod;
    private String bankAccount;
    private String priorityLevel;
    private Map<String, String> additionalServices;
    private List<String> contractDocuments;
    private String promotionCode;
    private LocalDateTime creationDate;
    
    // ANTI-PATTERN: Constructor mit 15+ Parametern!
    public TelekomContract(String contractId, String customerId, String contractType,
                          String internetSpeed, String internetTechnology, String phoneNumber,
                          String phoneFeatures, String tvPackage, String tvEquipment,
                          double internetPrice, double phonePrice, double tvPrice,
                          double installationFee, double discountPercentage, int contractDurationMonths,
                          LocalDate startDate, String billingCycle, String installationAddress,
                          String billingAddress, String technicalContact, String salesPerson,
                          boolean autoRenewal, boolean paperlessBilling, String paymentMethod,
                          String bankAccount, String priorityLevel, String promotionCode) {
        
        this.contractId = contractId;
        this.customerId = customerId;
        this.contractType = contractType;
        this.internetSpeed = internetSpeed;
        this.internetTechnology = internetTechnology;
        this.phoneNumber = phoneNumber;
        this.phoneFeatures = phoneFeatures;
        this.tvPackage = tvPackage;
        this.tvEquipment = tvEquipment;
        this.internetPrice = internetPrice;
        this.phonePrice = phonePrice;
        this.tvPrice = tvPrice;
        this.installationFee = installationFee;
        this.discountPercentage = discountPercentage;
        this.contractDurationMonths = contractDurationMonths;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(contractDurationMonths);
        this.billingCycle = billingCycle;
        this.installationAddress = installationAddress;
        this.billingAddress = billingAddress;
        this.technicalContact = technicalContact;
        this.salesPerson = salesPerson;
        this.autoRenewal = autoRenewal;
        this.paperlessBilling = paperlessBilling;
        this.paymentMethod = paymentMethod;
        this.bankAccount = bankAccount;
        this.priorityLevel = priorityLevel;
        this.promotionCode = promotionCode;
        this.creationDate = LocalDateTime.now();
        
        // Validation hier ist schwierig und unübersichtlich
        validateContract();
    }
    
    // TELESCOPING CONSTRUCTORS - auch ein Anti-Pattern
    public TelekomContract(String contractId, String customerId, String contractType,
                          String internetSpeed, String phoneNumber, double internetPrice,
                          double phonePrice, LocalDate startDate, String installationAddress) {
        // Viele Parameter mit Default-Values - fehleranfällig!
        this(contractId, customerId, contractType, internetSpeed, "DSL", phoneNumber,
             "BASIC", null, null, internetPrice, phonePrice, 0.0, 0.0, 0.0, 24,
             startDate, "MONTHLY", installationAddress, installationAddress,
             null, null, true, false, "BANK_TRANSFER", null, "STANDARD", null);
    }
    
    public TelekomContract(String contractId, String customerId, String contractType,
                          String internetSpeed, String phoneNumber, String tvPackage,
                          double internetPrice, double phonePrice, double tvPrice,
                          LocalDate startDate, String installationAddress, String billingAddress) {
        // Noch mehr Parameter, noch mehr Verwirrung
        this(contractId, customerId, contractType, internetSpeed, "DSL", phoneNumber,
             "BASIC", tvPackage, "STANDARD_BOX", internetPrice, phonePrice, tvPrice,
             49.99, 0.0, 24, startDate, "MONTHLY", installationAddress, billingAddress,
             null, null, true, false, "BANK_TRANSFER", null, "STANDARD", null);
    }
    
    public TelekomContract(String contractId, String customerId, String contractType,
                          String internetSpeed, String internetTechnology, String phoneNumber,
                          String phoneFeatures, String tvPackage, String tvEquipment,
                          double internetPrice, double phonePrice, double tvPrice,
                          double installationFee, double discountPercentage,
                          int contractDurationMonths, LocalDate startDate, String billingCycle,
                          String installationAddress, String billingAddress) {
        // Immer noch zu viele Parameter, aber weniger als der "Full Constructor"
        this(contractId, customerId, contractType, internetSpeed, internetTechnology,
             phoneNumber, phoneFeatures, tvPackage, tvEquipment, internetPrice,
             phonePrice, tvPrice, installationFee, discountPercentage,
             contractDurationMonths, startDate, billingCycle, installationAddress,
             billingAddress, null, null, true, false, "BANK_TRANSFER", null, "STANDARD", null);
    }
    
    // Validation Logic - schwer zu maintainen bei so vielen Parametern
    private void validateContract() {
        if (contractId == null || contractId.trim().isEmpty()) {
            throw new IllegalArgumentException("Contract ID is required");
        }
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (internetPrice < 0 || phonePrice < 0 || tvPrice < 0) {
            throw new IllegalArgumentException("Prices cannot be negative");
        }
        if (contractDurationMonths < 1 || contractDurationMonths > 36) {
            throw new IllegalArgumentException("Contract duration must be between 1 and 36 months");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
        // Viele weitere Validations...
    }
    
    // PROBLEM: Client Code ist sehr schwer lesbar
    public static void demonstrateUsageProblems() {
        // Was bedeuten all diese Parameter? Unmöglich zu lesen!
        TelekomContract contract1 = new TelekomContract(
            "CTR-001", "CUST-001", "INTERNET_PHONE_TV",
            "100", "DSL", "+49301234567", "PREMIUM", "BASIC_TV",
            "STANDARD_BOX", 39.95, 19.95, 24.95, 49.99, 10.0, 24,
            LocalDate.now().plusDays(7), "MONTHLY", "Musterstraße 1, Berlin",
            "Musterstraße 1, Berlin", "tech@example.com", "Hans Müller",
            true, true, "DIRECT_DEBIT", "DE123456789", "VIP", "SUMMER2024"
        );
        
        // Parameter-Reihenfolge vergessen = Bug!
        // Hier sind internetPrice und phonePrice vertauscht - schwer zu erkennen!
        TelekomContract contract2 = new TelekomContract(
            "CTR-002", "CUST-002", "INTERNET_PHONE",
            "250", "FIBER", "+49301234568", "BASIC", null, null,
            19.95, 49.95, 0.0, 0.0, 15.0, 12,  // <- Preise vertauscht!
            LocalDate.now().plusDays(14), "MONTHLY", "Teststraße 2, Hamburg",
            "Teststraße 2, Hamburg", null, "Maria Schmidt",
            false, true, "BANK_TRANSFER", null, "STANDARD", null
        );
    }
    
    // Getters - sehr viele davon
    public String getContractId() { return contractId; }
    public String getCustomerId() { return customerId; }
    public String getContractType() { return contractType; }
    public String getInternetSpeed() { return internetSpeed; }
    public String getInternetTechnology() { return internetTechnology; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPhoneFeatures() { return phoneFeatures; }
    public String getTvPackage() { return tvPackage; }
    public String getTvEquipment() { return tvEquipment; }
    public double getInternetPrice() { return internetPrice; }
    public double getPhonePrice() { return phonePrice; }
    public double getTvPrice() { return tvPrice; }
    public double getInstallationFee() { return installationFee; }
    public double getDiscountPercentage() { return discountPercentage; }
    public int getContractDurationMonths() { return contractDurationMonths; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getBillingCycle() { return billingCycle; }
    public String getInstallationAddress() { return installationAddress; }
    public String getBillingAddress() { return billingAddress; }
    public String getTechnicalContact() { return technicalContact; }
    public String getSalesPerson() { return salesPerson; }
    public String getSpecialConditions() { return specialConditions; }
    public boolean isAutoRenewal() { return autoRenewal; }
    public boolean isPaperlessBilling() { return paperlessBilling; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getBankAccount() { return bankAccount; }
    public String getPriorityLevel() { return priorityLevel; }
    public Map<String, String> getAdditionalServices() { return additionalServices; }
    public List<String> getContractDocuments() { return contractDocuments; }
    public String getPromotionCode() { return promotionCode; }
    public LocalDateTime getCreationDate() { return creationDate; }
    
    public double calculateTotalMonthlyPrice() {
        double total = internetPrice + phonePrice + tvPrice;
        return total * (1.0 - discountPercentage / 100.0);
    }
    
    public double calculateTotalContractValue() {
        return calculateTotalMonthlyPrice() * contractDurationMonths + installationFee;
    }
    
    @Override
    public String toString() {
        return String.format("TelekomContract{id='%s', customer='%s', type='%s', monthlyPrice=%.2f, duration=%d months}", 
                           contractId, customerId, contractType, calculateTotalMonthlyPrice(), contractDurationMonths);
    }
}