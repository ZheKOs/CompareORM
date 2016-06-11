package io.github.zhekos.compareorm.events;


public class TrialCompletedEvent {
    private final String trialName;

    public TrialCompletedEvent(String testName) {
        this.trialName = testName;
    }

    public String getTrialName() {
        return trialName;
    }
}
