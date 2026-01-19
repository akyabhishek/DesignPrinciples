// Dependency Inversion Principle (DIP)
// The Dependency Inversion Principle (DIP) is a principle in object-oriented design that
// states that High-level modules should not depend on low-level modules.
// Both should depend on abstractions. Additionally, abstractions should not depend on details. Details should depend on abstractions.

// ============================================
// BAD EXAMPLE - Violates DIP
// ============================================

// Low-level module
class EmailSender {
    public void sendEmail(String email, String message) {
        System.out.println("Sending email to " + email + ": " + message);
    }
}

// Another low-level module
class SMSSender {
    public void sendSMS(String phone, String message) {
        System.out.println("Sending SMS to " + phone + ": " + message);
    }
}

// High-level module directly depends on low-level modules
class BadOrderService {
    private EmailSender emailSender;
    private SMSSender smsSender;
    
    public BadOrderService() {
        // Creating concrete instances - tight coupling!
        this.emailSender = new EmailSender();
        this.smsSender = new SMSSender();
    }
    
    public void processOrderWithEmail(Order order) {
        System.out.println("Processing order: " + order.getOrderId());
        // Directly coupled to EmailSender
        emailSender.sendEmail(order.getCustomerEmail(), "Order confirmed");
    }
    
    public void processOrderWithSMS(Order order) {
        System.out.println("Processing order: " + order.getOrderId());
        // Directly coupled to SMSSender
        smsSender.sendSMS(order.getCustomerPhone(), "Order confirmed");
    }
}

// Usage of BAD example
class BadExample {
    public void demonstrate() {
        System.out.println("=== BAD EXAMPLE (Violates DIP) ===");
        
        Order order = new Order("customer@example.com", "+1234567890", "ORD-001");
        
        // Can only use the hardcoded implementations
        BadOrderService service = new BadOrderService();
        service.processOrderWithEmail(order);
        
        // Problem: Can't easily switch implementations or test with mocks
        // Problem: Want Slack notifications? Must modify BadOrderService!
        
        System.out.println();
    }
}


// ============================================
// GOOD EXAMPLE - Follows DIP
// ============================================

// Abstraction (interface) - both high and low-level modules depend on this
interface Notifier {
    void send(String recipient, String message);
}

// Low-level modules implement the abstraction
class EmailNotifier implements Notifier {
    @Override
    public void send(String recipient, String message) {
        System.out.println("Sending email to " + recipient + ": " + message);
    }
}

class SMSNotifier implements Notifier {
    @Override
    public void send(String recipient, String message) {
        System.out.println("Sending SMS to " + recipient + ": " + message);
    }
}

class SlackNotifier implements Notifier {
    @Override
    public void send(String recipient, String message) {
        System.out.println("Sending Slack message to " + recipient + ": " + message);
    }
}

// High-level module depends on abstraction, not concrete implementations
class GoodOrderService {
    private Notifier notifier;
    
    // Dependency injected through constructor
    public GoodOrderService(Notifier notifier) {
        this.notifier = notifier;
    }
    
    public void processOrder(Order order) {
        System.out.println("Processing order: " + order.getOrderId());
        // Uses abstraction - doesn't care about the concrete implementation
        notifier.send(order.getCustomerEmail(), "Order confirmed");
    }
}

// Usage of GOOD example - Example 1: Using different implementations
class GoodExample {
    public void demonstrate() {
        System.out.println("=== GOOD EXAMPLE (Follows DIP) ===");
        
        Order order = new Order("customer@example.com", "+1234567890", "ORD-002");
        
        // Can easily switch implementations at runtime!
        
        System.out.println("--- Using Email Notifier ---");
        Notifier emailNotifier = new EmailNotifier();
        GoodOrderService emailService = new GoodOrderService(emailNotifier);
        emailService.processOrder(order);
        
        System.out.println("\n--- Using SMS Notifier ---");
        Notifier smsNotifier = new SMSNotifier();
        GoodOrderService smsService = new GoodOrderService(smsNotifier);
        smsService.processOrder(order);
        
        System.out.println("\n--- Using Slack Notifier ---");
        Notifier slackNotifier = new SlackNotifier();
        GoodOrderService slackService = new GoodOrderService(slackNotifier);
        slackService.processOrder(order);
        
        System.out.println();
    }
}


// ============================================
// ADVANCED USAGE: Multi-Channel Notifications
// ============================================

class MultiChannelNotifier implements Notifier {
    private Notifier[] notifiers;
    
    public MultiChannelNotifier(Notifier... notifiers) {
        this.notifiers = notifiers;
    }
    
    @Override
    public void send(String recipient, String message) {
        for (Notifier notifier : notifiers) {
            notifier.send(recipient, message);
        }
    }
}

class MultiChannelExample {
    public void demonstrate() {
        System.out.println("=== MULTI-CHANNEL NOTIFICATIONS ===");
        
        Order order = new Order("customer@example.com", "+1234567890", "ORD-003");
        
        // Send to multiple channels at once
        Notifier multiNotifier = new MultiChannelNotifier(
            new EmailNotifier(),
            new SMSNotifier(),
            new SlackNotifier()
        );
        
        GoodOrderService service = new GoodOrderService(multiNotifier);
        service.processOrder(order);
        
        System.out.println();
    }
}


// ============================================
// TESTING USAGE: Using Mock
// ============================================

class MockNotifier implements Notifier {
    private boolean wasCalled = false;
    private String lastRecipient;
    private String lastMessage;
    
    @Override
    public void send(String recipient, String message) {
        this.wasCalled = true;
        this.lastRecipient = recipient;
        this.lastMessage = message;
        System.out.println("Mock: Captured notification to " + recipient);
    }
    
    public boolean wasNotificationSent() {
        return wasCalled;
    }
    
    public String getLastRecipient() {
        return lastRecipient;
    }
}

class TestingExample {
    public void demonstrate() {
        System.out.println("=== TESTING WITH MOCK ===");
        
        Order order = new Order("test@example.com", "+1111111111", "TEST-001");
        
        // Use mock for testing - no real notifications sent!
        MockNotifier mockNotifier = new MockNotifier();
        GoodOrderService service = new GoodOrderService(mockNotifier);
        service.processOrder(order);
        
        // Verify the notification was sent
        if (mockNotifier.wasNotificationSent()) {
            System.out.println("✓ Test passed: Notification was sent to " + 
                             mockNotifier.getLastRecipient());
        }
        
        System.out.println();
    }
}


// ============================================
// DOMAIN MODEL
// ============================================

class Order {
    private String customerEmail;
    private String customerPhone;
    private String orderId;
    
    public Order(String customerEmail, String customerPhone, String orderId) {
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.orderId = orderId;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public String getOrderId() {
        return orderId;
    }
}
