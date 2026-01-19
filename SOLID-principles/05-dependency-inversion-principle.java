// Dependency Inversion Principle (DIP)
// Don't depend on concrete classes, depend on interfaces
//The Dependency Inversion Principle (DIP) is a principle in object-oriented design that states that High-level modules should not depend on low-level modules.
// Both should depend on abstractions. Additionally, abstractions should not depend on details. Details should depend on abstractions.

// BAD EXAMPLE - Hard to change
class Light {
    public void turnOn() {
        System.out.println("Light is ON");
    }
}

class BadSwitch {
    private Light light;
    
    public BadSwitch() {
        this.light = new Light(); // Stuck with only Light
    }
    
    public void press() {
        light.turnOn();
    }
}

// GOOD EXAMPLE - Easy to change
interface Device {
    void turnOn();
}

class GoodLight implements Device {
    @Override
    public void turnOn() {
        System.out.println("Light is ON");
    }
}

class Fan implements Device {
    @Override
    public void turnOn() {
        System.out.println("Fan is ON");
    }
}

class GoodSwitch {
    private Device device;
    
    public GoodSwitch(Device device) {
        this.device = device; // Can control any device
    }
    
    public void press() {
        device.turnOn();
    }
}

/*
Dependency Inversion Principle - Simple Explanation:

Don't depend on specific things, depend on general rules.

BAD EXAMPLE:
- BadSwitch can only control a Light
- If you want to control a Fan, you need a new switch

GOOD EXAMPLE:
- GoodSwitch can control any Device (Light, Fan, etc.)
- Same switch works with different devices
- Just pass the device you want to control

Think of it like a universal remote - it works with any TV brand
because it follows the "TV interface" rules, not tied to one specific TV.

Benefits:
- More flexible code
- Easy to add new devices
- Same switch works with anything
*/
