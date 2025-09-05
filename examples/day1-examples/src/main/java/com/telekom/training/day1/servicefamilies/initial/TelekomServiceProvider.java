package com.telekom.training.day1.servicefamilies.initial;

/**
 * ANTI-PATTERN: Hard-coded Service Creation
 * 
 * Diese Klasse verletzt das Open/Closed Principle und macht es schwer,
 * neue Service-Familien hinzuzufügen oder bestehende zu ändern.
 * 
 * Code-Smells:
 * - Hard-coded Service Creation
 * - Giant Switch Statements
 * - Tight Coupling zwischen Services
 * - Keine Abstraktion für Service-Familien
 * - Schwer erweiterbar für neue Produkt-Lines
 * - Code Duplication in Service Creation Logic
 */
public class TelekomServiceProvider {
    
    // HARD-CODED SERVICE CREATION - schlecht erweiterbar
    public InternetService createInternetService(String serviceType) {
        switch (serviceType.toUpperCase()) {
            case "DSL":
                // Hard-coded DSL Service Creation
                DslService dslService = new DslService();
                dslService.setTechnology("ADSL2+");
                dslService.setMaxSpeed(250);
                dslService.setInstallationType("TECHNICIAN_REQUIRED");
                dslService.setEquipment("Fritz!Box 7590");
                dslService.setAvailabilityCheck("LINE_BASED");
                return dslService;
                
            case "MOBILE":
                // Hard-coded Mobile Internet Creation
                MobileInternetService mobileService = new MobileInternetService();
                mobileService.setTechnology("LTE/5G");
                mobileService.setMaxSpeed(1000);
                mobileService.setInstallationType("SELF_ACTIVATION");
                mobileService.setEquipment("SIM Card");
                mobileService.setAvailabilityCheck("COVERAGE_BASED");
                mobileService.setNetworkType("MOBILE");
                return mobileService;
                
            case "FIBER":
                // Hard-coded Fiber Service Creation
                FiberService fiberService = new FiberService();
                fiberService.setTechnology("FTTH");
                fiberService.setMaxSpeed(1000);
                fiberService.setInstallationType("FIBER_INSTALLATION");
                fiberService.setEquipment("Glasfaser Modem");
                fiberService.setAvailabilityCheck("FIBER_INFRASTRUCTURE");
                return fiberService;
                
            default:
                throw new IllegalArgumentException("Unknown internet service type: " + serviceType);
        }
    }
    
    // DUPLICATE LOGIC - ähnliche Patterns wie oben
    public PhoneService createPhoneService(String serviceType) {
        switch (serviceType.toUpperCase()) {
            case "DSL":
                // DSL Family Phone Service
                LandlineService landlineService = new LandlineService();
                landlineService.setTechnology("ISDN/VoIP");
                landlineService.setFeatures("CALLER_ID,VOICEMAIL,CALL_WAITING");
                landlineService.setInstallationType("WITH_DSL_INSTALLATION");
                landlineService.setEquipment("Included in Router");
                landlineService.setNumberType("LANDLINE");
                return landlineService;
                
            case "MOBILE":
                // Mobile Family Phone Service
                MobilePhoneService mobilePhoneService = new MobilePhoneService();
                mobilePhoneService.setTechnology("GSM/LTE");
                mobilePhoneService.setFeatures("SMS,MMS,DATA,ROAMING");
                mobilePhoneService.setInstallationType("SIM_ACTIVATION");
                mobilePhoneService.setEquipment("SIM Card + Phone");
                mobilePhoneService.setNumberType("MOBILE");
                return mobilePhoneService;
                
            case "FIBER":
                // Fiber Family Phone Service
                VoipService voipService = new VoipService();
                voipService.setTechnology("VoIP over Fiber");
                voipService.setFeatures("HD_VOICE,VIDEO_CALLS,CONFERENCE");
                voipService.setInstallationType("WITH_FIBER_INSTALLATION");
                voipService.setEquipment("VoIP Adapter");
                voipService.setNumberType("VIRTUAL");
                return voipService;
                
            default:
                throw new IllegalArgumentException("Unknown phone service type: " + serviceType);
        }
    }
    
    // NOCH MEHR DUPLICATE LOGIC
    public TvService createTvService(String serviceType) {
        switch (serviceType.toUpperCase()) {
            case "DSL":
                // DSL Family TV Service
                IptvService iptvService = new IptvService();
                iptvService.setTechnology("IPTV over DSL");
                iptvService.setChannelPackage("BASIC_HD");
                iptvService.setInstallationType("SET_TOP_BOX");
                iptvService.setEquipment("MagentaTV Box");
                iptvService.setStreamingCapability("LIMITED_BY_BANDWIDTH");
                return iptvService;
                
            case "MOBILE":
                // Mobile TV würde normalerweise nicht angeboten, aber...
                MobileTvService mobileTvService = new MobileTvService();
                mobileTvService.setTechnology("Streaming via Mobile Data");
                mobileTvService.setChannelPackage("MOBILE_OPTIMIZED");
                mobileTvService.setInstallationType("APP_DOWNLOAD");
                mobileTvService.setEquipment("Smartphone/Tablet App");
                mobileTvService.setStreamingCapability("MOBILE_DATA_DEPENDENT");
                return mobileTvService;
                
            case "FIBER":
                // Fiber Family TV Service
                FiberTvService fiberTvService = new FiberTvService();
                fiberTvService.setTechnology("IPTV over Fiber");
                fiberTvService.setChannelPackage("PREMIUM_4K");
                fiberTvService.setInstallationType("ADVANCED_SET_TOP_BOX");
                fiberTvService.setEquipment("4K MagentaTV Box");
                fiberTvService.setStreamingCapability("UNLIMITED_4K");
                return fiberTvService;
                
            default:
                throw new IllegalArgumentException("Unknown TV service type: " + serviceType);
        }
    }
    
    // HARD-CODED BUNDLE CREATION - nicht flexibel
    public ServiceBundle createBundle(String bundleType) {
        ServiceBundle bundle = new ServiceBundle(bundleType);
        
        switch (bundleType.toUpperCase()) {
            case "DSL_COMPLETE":
                bundle.addService(createInternetService("DSL"));
                bundle.addService(createPhoneService("DSL"));
                bundle.addService(createTvService("DSL"));
                bundle.setDiscountPercentage(15.0);
                break;
                
            case "MOBILE_FAMILY":
                bundle.addService(createInternetService("MOBILE"));
                bundle.addService(createPhoneService("MOBILE"));
                // Mobile TV meist nicht gewünscht
                bundle.setDiscountPercentage(10.0);
                break;
                
            case "FIBER_PREMIUM":
                bundle.addService(createInternetService("FIBER"));
                bundle.addService(createPhoneService("FIBER"));
                bundle.addService(createTvService("FIBER"));
                bundle.setDiscountPercentage(20.0);
                break;
                
            default:
                throw new IllegalArgumentException("Unknown bundle type: " + bundleType);
        }
        
        return bundle;
    }
    
    // VALIDATION LOGIC - auch hard-coded
    public boolean isValidServiceCombination(String internetType, String phoneType, String tvType) {
        // Hard-coded Validation Rules
        if ("DSL".equals(internetType)) {
            return "DSL".equals(phoneType) && ("DSL".equals(tvType) || tvType == null);
        } else if ("MOBILE".equals(internetType)) {
            return "MOBILE".equals(phoneType) && (tvType == null || "MOBILE".equals(tvType));
        } else if ("FIBER".equals(internetType)) {
            return "FIBER".equals(phoneType) && ("FIBER".equals(tvType) || tvType == null);
        }
        return false;
    }
}

// BASE INTERFACES - wären besser in separaten Files
interface InternetService {
    void setTechnology(String technology);
    void setMaxSpeed(int speedMbps);
    void setInstallationType(String type);
    void setEquipment(String equipment);
    void setAvailabilityCheck(String checkType);
    String getServiceInfo();
}

interface PhoneService {
    void setTechnology(String technology);
    void setFeatures(String features);
    void setInstallationType(String type);
    void setEquipment(String equipment);
    void setNumberType(String numberType);
    String getServiceInfo();
}

interface TvService {
    void setTechnology(String technology);
    void setChannelPackage(String packageName);
    void setInstallationType(String type);
    void setEquipment(String equipment);
    void setStreamingCapability(String capability);
    String getServiceInfo();
}

// CONCRETE IMPLEMENTATIONS - sollten auch separate Files sein
class DslService implements InternetService {
    private String technology, installationType, equipment, availabilityCheck;
    private int maxSpeed;
    
    public void setTechnology(String technology) { this.technology = technology; }
    public void setMaxSpeed(int speedMbps) { this.maxSpeed = speedMbps; }
    public void setInstallationType(String type) { this.installationType = type; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public void setAvailabilityCheck(String checkType) { this.availabilityCheck = checkType; }
    
    public String getServiceInfo() {
        return String.format("DSL Internet: %s, bis zu %d Mbps, Equipment: %s", 
                           technology, maxSpeed, equipment);
    }
}

class MobileInternetService implements InternetService {
    private String technology, installationType, equipment, availabilityCheck, networkType;
    private int maxSpeed;
    
    public void setTechnology(String technology) { this.technology = technology; }
    public void setMaxSpeed(int speedMbps) { this.maxSpeed = speedMbps; }
    public void setInstallationType(String type) { this.installationType = type; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public void setAvailabilityCheck(String checkType) { this.availabilityCheck = checkType; }
    public void setNetworkType(String networkType) { this.networkType = networkType; }
    
    public String getServiceInfo() {
        return String.format("Mobile Internet: %s, bis zu %d Mbps, Equipment: %s", 
                           technology, maxSpeed, equipment);
    }
}

class FiberService implements InternetService {
    private String technology, installationType, equipment, availabilityCheck;
    private int maxSpeed;
    
    public void setTechnology(String technology) { this.technology = technology; }
    public void setMaxSpeed(int speedMbps) { this.maxSpeed = speedMbps; }
    public void setInstallationType(String type) { this.installationType = type; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public void setAvailabilityCheck(String checkType) { this.availabilityCheck = checkType; }
    
    public String getServiceInfo() {
        return String.format("Fiber Internet: %s, bis zu %d Mbps, Equipment: %s", 
                           technology, maxSpeed, equipment);
    }
}

// Phone Service Implementations
class LandlineService implements PhoneService {
    private String technology, features, installationType, equipment, numberType;
    
    public void setTechnology(String technology) { this.technology = technology; }
    public void setFeatures(String features) { this.features = features; }
    public void setInstallationType(String type) { this.installationType = type; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public void setNumberType(String numberType) { this.numberType = numberType; }
    
    public String getServiceInfo() {
        return String.format("Landline Phone: %s, Features: %s", technology, features);
    }
}

class MobilePhoneService implements PhoneService {
    private String technology, features, installationType, equipment, numberType;
    
    public void setTechnology(String technology) { this.technology = technology; }
    public void setFeatures(String features) { this.features = features; }
    public void setInstallationType(String type) { this.installationType = type; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public void setNumberType(String numberType) { this.numberType = numberType; }
    
    public String getServiceInfo() {
        return String.format("Mobile Phone: %s, Features: %s", technology, features);
    }
}

class VoipService implements PhoneService {
    private String technology, features, installationType, equipment, numberType;
    
    public void setTechnology(String technology) { this.technology = technology; }
    public void setFeatures(String features) { this.features = features; }
    public void setInstallationType(String type) { this.installationType = type; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public void setNumberType(String numberType) { this.numberType = numberType; }
    
    public String getServiceInfo() {
        return String.format("VoIP Phone: %s, Features: %s", technology, features);
    }
}

// TV Service Implementations
class IptvService implements TvService {
    private String technology, channelPackage, installationType, equipment, streamingCapability;
    
    public void setTechnology(String technology) { this.technology = technology; }
    public void setChannelPackage(String packageName) { this.channelPackage = packageName; }
    public void setInstallationType(String type) { this.installationType = type; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public void setStreamingCapability(String capability) { this.streamingCapability = capability; }
    
    public String getServiceInfo() {
        return String.format("IPTV: %s, Package: %s, Equipment: %s", 
                           technology, channelPackage, equipment);
    }
}

class MobileTvService implements TvService {
    private String technology, channelPackage, installationType, equipment, streamingCapability;
    
    public void setTechnology(String technology) { this.technology = technology; }
    public void setChannelPackage(String packageName) { this.channelPackage = packageName; }
    public void setInstallationType(String type) { this.installationType = type; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public void setStreamingCapability(String capability) { this.streamingCapability = capability; }
    
    public String getServiceInfo() {
        return String.format("Mobile TV: %s, Package: %s", technology, channelPackage);
    }
}

class FiberTvService implements TvService {
    private String technology, channelPackage, installationType, equipment, streamingCapability;
    
    public void setTechnology(String technology) { this.technology = technology; }
    public void setChannelPackage(String packageName) { this.channelPackage = packageName; }
    public void setInstallationType(String type) { this.installationType = type; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public void setStreamingCapability(String capability) { this.streamingCapability = capability; }
    
    public String getServiceInfo() {
        return String.format("Fiber TV: %s, Package: %s, Equipment: %s", 
                           technology, channelPackage, equipment);
    }
}

// Service Bundle
class ServiceBundle {
    private String bundleType;
    private java.util.List<Object> services = new java.util.ArrayList<>();
    private double discountPercentage;
    
    public ServiceBundle(String bundleType) {
        this.bundleType = bundleType;
    }
    
    public void addService(Object service) {
        services.add(service);
    }
    
    public void setDiscountPercentage(double percentage) {
        this.discountPercentage = percentage;
    }
    
    public String getBundleInfo() {
        return String.format("Bundle: %s, Services: %d, Discount: %.1f%%", 
                           bundleType, services.size(), discountPercentage);
    }
    
    public java.util.List<Object> getServices() {
        return services;
    }
}