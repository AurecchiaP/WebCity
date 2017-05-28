package utils;


/**
 * utility class to keep track of the progress of the download (and parsing) of a repository
 */
public class DownloadProgress {

    private double percentage;
    private String taskName;
    private double parsingPercentage;

    public DownloadProgress() {
        this.percentage = 0;
        this.taskName = "none";
        this.parsingPercentage = 0;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public double getParsingPercentage() {
        return parsingPercentage;
    }

    public void setParsingPercentage(double parsingPercentage) {
        this.parsingPercentage = parsingPercentage;
    }
}
