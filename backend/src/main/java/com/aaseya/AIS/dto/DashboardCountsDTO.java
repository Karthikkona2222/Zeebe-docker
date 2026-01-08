package com.aaseya.AIS.dto;

public class DashboardCountsDTO {
    private long totalCount;
    private long newCount;
    private long pendingCount;
    private long reopenedCount;
    private long completedCount;

    public long getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
    public long getNewCount() {
        return newCount;
    }
    public void setNewCount(long newCount) {
        this.newCount = newCount;
    }
    public long getPendingCount() {
        return pendingCount;
    }
    public void setPendingCount(long pendingCount) {
        this.pendingCount = pendingCount;
    }
    public long getReopenedCount() {
        return reopenedCount;
    }
    public void setReopenedCount(long reopenedCount) {
        this.reopenedCount = reopenedCount;
    }
    public long getCompletedCount() {
        return completedCount;
    }
    public void setCompletedCount(long completedCount) {
        this.completedCount = completedCount;
    }
}
