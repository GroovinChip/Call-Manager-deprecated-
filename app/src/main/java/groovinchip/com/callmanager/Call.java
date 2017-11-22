package groovinchip.com.callmanager;

import java.util.Date;

public class Call{
    //---Instance Data--\\
    protected String name;
    protected String number;
    protected String callDescription;
    protected Date timeCreated;
    protected Date reminderTime;

    //---Getters and Setters--\\
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescription() {
        return callDescription;
    }

    public void setDescription(String callDescription) {
        this.callDescription = callDescription;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(Date reminderTime) {
        this.reminderTime = reminderTime;
    }
}
