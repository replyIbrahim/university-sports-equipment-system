package model;

import java.time.LocalDateTime;

public class Feedback {
    private String feedbackID;
    private String submitterID;
    private String submitterName;
    private String message;
    private String type; // FEEDBACK, COMPLAINT
    private LocalDateTime submittedAt;
    private boolean isResolved;

    public Feedback(String submitterID, String submitterName, String message, String type) {
        this.feedbackID    = "FB-" + System.currentTimeMillis();
        this.submitterID   = submitterID;
        this.submitterName = submitterName;
        this.message       = message;
        this.type          = type;
        this.submittedAt   = LocalDateTime.now();
        this.isResolved    = false;
    }

    public void resolve()            { isResolved = true; }

    public String getFeedbackID()    { return feedbackID; }
    public String getSubmitterID()   { return submitterID; }
    public String getSubmitterName() { return submitterName; }
    public String getMessage()       { return message; }
    public String getType()          { return type; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public boolean isResolved()      { return isResolved; }
}
