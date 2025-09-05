package com.telekom.training.day1.contractbuilder.fixed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * REFACTORED Contract using Builder Pattern
 * 
 * Improvements:
 * - Builder Pattern für komplexe Objekterstellung
 * - Fluent Interface für bessere Lesbarkeit
 * - Step-by-step Validation
 * - Immutable Contract nach Erstellung
 * - Flexible und erweiterbare Konstruktion
 * - Self-documenting Code
 */
public class TelekomContract {
    
    // Final fields - Immutable nach Erstellung
    private final String contractId;
    private final String customerId;
    private final String contractType;
    private final String internetSpeed;
    private final String internetTechnology;
    private final String phoneNumber;
    private final String phoneFeatures;
    private final String tvPackage;
    private final String tvEquipment;
    private final double internetPrice;
    private final double phonePrice;
    private final double tvPrice;
    private final double installationFee;
    private final double discountPercentage;
    private final int contractDurationMonths;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String billingCycle;
    private final String installationAddress;
    private final String billingAddress;
    private final String technicalContact;
    private final String salesPerson;
    private final String specialConditions;
    private final boolean autoRenewal;
    private final boolean paperlessBilling;
    private final String paymentMethod;
    private final String bankAccount;
    private final String priorityLevel;
    private final Map<String, String> additionalServices;
    private final List<String> contractDocuments;
    private final String promotionCode;
    private final LocalDateTime creationDate;
    
    // Private Constructor - nur Builder kann instanzieren
    private TelekomContract(Builder builder) {
        this.contractId = builder.contractId;
        this.customerId = builder.customerId;
        this.contractType = builder.contractType;
        this.internetSpeed = builder.internetSpeed;
        this.internetTechnology = builder.internetTechnology;
        this.phoneNumber = builder.phoneNumber;
        this.phoneFeatures = builder.phoneFeatures;
        this.tvPackage = builder.tvPackage;
        this.tvEquipment = builder.tvEquipment;
        this.internetPrice = builder.internetPrice;
        this.phonePrice = builder.phonePrice;
        this.tvPrice = builder.tvPrice;
        this.installationFee = builder.installationFee;
        this.discountPercentage = builder.discountPercentage;
        this.contractDurationMonths = builder.contractDurationMonths;
        this.startDate = builder.startDate;
        this.endDate = builder.startDate.plusMonths(builder.contractDurationMonths);
        this.billingCycle = builder.billingCycle;
        this.installationAddress = builder.installationAddress;
        this.billingAddress = builder.billingAddress != null ? builder.billingAddress : builder.installationAddress;
        this.technicalContact = builder.technicalContact;
        this.salesPerson = builder.salesPerson;
        this.specialConditions = builder.specialConditions;
        this.autoRenewal = builder.autoRenewal;
        this.paperlessBilling = builder.paperlessBilling;
        this.paymentMethod = builder.paymentMethod;
        this.bankAccount = builder.bankAccount;
        this.priorityLevel = builder.priorityLevel;
        this.additionalServices = new HashMap<>(builder.additionalServices);
        this.contractDocuments = new ArrayList<>(builder.contractDocuments);
        this.promotionCode = builder.promotionCode;
        this.creationDate = LocalDateTime.now();
    }
    
    /**
     * Static Factory Method für Builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder Class - Inner Class mit Fluent Interface
     */
    public static class Builder {
        // Required Fields
        private String contractId;
        private String customerId;
        private String contractType;
        private LocalDate startDate;
        private String installationAddress;
        
        // Optional Fields mit Defaults
        private String internetSpeed;
        private String internetTechnology = "DSL";
        private String phoneNumber;
        private String phoneFeatures = "BASIC";
        private String tvPackage;
        private String tvEquipment;
        private double internetPrice = 0.0;
        private double phonePrice = 0.0;
        private double tvPrice = 0.0;
        private double installationFee = 0.0;
        private double discountPercentage = 0.0;
        private int contractDurationMonths = 24;
        private String billingCycle = "MONTHLY";
        private String billingAddress;
        private String technicalContact;
        private String salesPerson;
        private String specialConditions;
        private boolean autoRenewal = true;
        private boolean paperlessBilling = false;
        private String paymentMethod = "BANK_TRANSFER";
        private String bankAccount;
        private String priorityLevel = "STANDARD";
        private Map<String, String> additionalServices = new HashMap<>();
        private List<String> contractDocuments = new ArrayList<>();
        private String promotionCode;
        
        private Builder() {
            // Private constructor
        }
        
        // Required Fields - kein Default, müssen gesetzt werden
        public Builder withContractId(String contractId) {
            this.contractId = contractId;
            return this;
        }
        
        public Builder forCustomer(String customerId) {
            this.customerId = customerId;
            return this;
        }
        
        public Builder withContractType(String contractType) {
            this.contractType = contractType;
            return this;
        }
        
        public Builder startingOn(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }
        
        public Builder installedAt(String installationAddress) {
            this.installationAddress = installationAddress;
            return this;
        }
        
        // Internet Configuration - Fluent Methods
        public Builder withInternetSpeed(String speed) {
            this.internetSpeed = speed;
            return this;
        }
        
        public Builder withInternetTechnology(String technology) {
            this.internetTechnology = technology;
            return this;
        }
        
        public Builder withInternetPrice(double price) {
            this.internetPrice = price;
            return this;
        }
        
        // Phone Configuration
        public Builder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }
        
        public Builder withPhoneFeatures(String features) {
            this.phoneFeatures = features;
            return this;
        }
        
        public Builder withPhonePrice(double price) {
            this.phonePrice = price;
            return this;
        }
        
        // TV Configuration
        public Builder withTvPackage(String tvPackage) {
            this.tvPackage = tvPackage;
            return this;
        }
        
        public Builder withTvEquipment(String tvEquipment) {
            this.tvEquipment = tvEquipment;
            return this;
        }
        
        public Builder withTvPrice(double price) {
            this.tvPrice = price;
            return this;
        }
        
        // Pricing and Contract Terms
        public Builder withInstallationFee(double fee) {
            this.installationFee = fee;
            return this;
        }
        
        public Builder withDiscount(double discountPercentage) {
            this.discountPercentage = discountPercentage;
            return this;
        }
        
        public Builder forDuration(int months) {
            this.contractDurationMonths = months;
            return this;
        }
        
        public Builder withBillingCycle(String cycle) {
            this.billingCycle = cycle;
            return this;
        }
        
        // Addresses and Contacts
        public Builder billedAt(String billingAddress) {
            this.billingAddress = billingAddress;
            return this;
        }
        
        public Builder withTechnicalContact(String contact) {
            this.technicalContact = contact;
            return this;
        }
        
        public Builder soldBy(String salesPerson) {
            this.salesPerson = salesPerson;
            return this;
        }
        
        // Contract Options
        public Builder withAutoRenewal(boolean autoRenewal) {
            this.autoRenewal = autoRenewal;
            return this;
        }
        
        public Builder withPaperlessBilling(boolean paperless) {
            this.paperlessBilling = paperless;
            return this;
        }
        
        public Builder withPaymentMethod(String method) {
            this.paymentMethod = method;
            return this;
        }
        
        public Builder withBankAccount(String account) {
            this.bankAccount = account;
            return this;
        }
        
        public Builder withPriority(String priority) {
            this.priorityLevel = priority;
            return this;
        }
        
        public Builder withPromotionCode(String code) {
            this.promotionCode = code;
            return this;
        }
        
        public Builder withSpecialConditions(String conditions) {
            this.specialConditions = conditions;
            return this;
        }
        
        // Additional Services
        public Builder addAdditionalService(String serviceType, String details) {
            this.additionalServices.put(serviceType, details);
            return this;
        }
        
        public Builder addContractDocument(String document) {
            this.contractDocuments.add(document);
            return this;
        }
        
        // Convenience Methods für häufige Kombinationen
        public Builder asInternetOnlyContract(String speed, double price) {
            this.contractType = "INTERNET_ONLY";
            this.internetSpeed = speed;
            this.internetPrice = price;
            return this;
        }
        
        public Builder asTriplePlayContract(String internetSpeed, String phoneNumber, String tvPackage,
                                          double internetPrice, double phonePrice, double tvPrice) {
            this.contractType = "INTERNET_PHONE_TV";
            this.internetSpeed = internetSpeed;
            this.phoneNumber = phoneNumber;
            this.tvPackage = tvPackage;
            this.internetPrice = internetPrice;
            this.phonePrice = phonePrice;
            this.tvPrice = tvPrice;
            return this;
        }
        
        public Builder asPremiumBundle() {
            this.priorityLevel = "VIP";
            this.phoneFeatures = "PREMIUM";
            this.autoRenewal = true;
            this.paperlessBilling = true;
            this.discountPercentage = 15.0;
            return this;
        }
        
        /**
         * Build Method - erstellt das finale Contract
         * Validation happens here
         */
        public TelekomContract build() {
            validate();
            return new TelekomContract(this);
        }
        
        /**
         * Step-by-step Validation während des Builds
         */
        private void validate() {
            if (contractId == null || contractId.trim().isEmpty()) {
                throw new IllegalArgumentException("Contract ID is required");
            }
            if (customerId == null || customerId.trim().isEmpty()) {
                throw new IllegalArgumentException("Customer ID is required");
            }
            if (contractType == null || contractType.trim().isEmpty()) {
                throw new IllegalArgumentException("Contract type is required");
            }
            if (startDate == null) {
                throw new IllegalArgumentException("Start date is required");
            }
            if (startDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Start date cannot be in the past");
            }
            if (installationAddress == null || installationAddress.trim().isEmpty()) {
                throw new IllegalArgumentException("Installation address is required");
            }
            if (internetPrice < 0 || phonePrice < 0 || tvPrice < 0 || installationFee < 0) {
                throw new IllegalArgumentException("Prices and fees cannot be negative");
            }
            if (contractDurationMonths < 1 || contractDurationMonths > 36) {
                throw new IllegalArgumentException("Contract duration must be between 1 and 36 months");
            }
            if (discountPercentage < 0 || discountPercentage > 100) {
                throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
            }
            
            // Contract Type specific validation
            validateContractTypeSpecificFields();
        }
        
        private void validateContractTypeSpecificFields() {
            if ("INTERNET_ONLY".equals(contractType) || "INTERNET_PHONE".equals(contractType) 
                || "INTERNET_PHONE_TV".equals(contractType)) {
                if (internetSpeed == null || internetSpeed.trim().isEmpty()) {
                    throw new IllegalArgumentException("Internet speed is required for " + contractType);
                }
                if (internetPrice <= 0) {
                    throw new IllegalArgumentException("Internet price must be positive for " + contractType);
                }
            }
            
            if ("INTERNET_PHONE".equals(contractType) || "INTERNET_PHONE_TV".equals(contractType)) {
                if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                    throw new IllegalArgumentException("Phone number is required for " + contractType);
                }
            }
            
            if ("INTERNET_PHONE_TV".equals(contractType)) {
                if (tvPackage == null || tvPackage.trim().isEmpty()) {
                    throw new IllegalArgumentException("TV package is required for " + contractType);
                }
            }
        }
    }
    
    // Getters (alle final fields)
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
    public Map<String, String> getAdditionalServices() { return new HashMap<>(additionalServices); }
    public List<String> getContractDocuments() { return new ArrayList<>(contractDocuments); }
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