package com.aaseya.AIS.dto;

public class TaskVariables {
  private String state;
  private boolean assigned;
  private String assignee;
  private String taskDefinitionId;
  private String[] candidateGroups;
  private String candidateUser;
  private String processDefinitionKey;
  private String processInstanceKey;
  private float pageSize;
  private String id;
  private String name;
  private String value;
  private String firstName;
  private String status;
  private String orderID;

  private String processName;
  private String creationDate;
  private String completionDate;
  private String taskState;
  private String formKey;
  private boolean isFirst;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProcessName() {
    return processName;
  }

  public void setProcessName(String processName) {
    this.processName = processName;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public String getCompletionDate() {
    return completionDate;
  }

  public void setCompletionDate(String completionDate) {
    this.completionDate = completionDate;
  }

  public String getTaskState() {
    return taskState;
  }

  public void setTaskState(String taskState) {
    this.taskState = taskState;
  }

  public String getFormKey() {
    return formKey;
  }

  public void setFormKey(String formKey) {
    this.formKey = formKey;
  }

  public boolean isFirst() {
    return isFirst;
  }

  public void setFirst(boolean isFirst) {
    this.isFirst = isFirst;
  }

  // Getter Methods

  public String getState() {
    return state;
  }

  public boolean getAssigned() {
    return assigned;
  }

  public String getAssignee() {
    return assignee;
  }

  public String getTaskDefinitionId() {
    return taskDefinitionId;
  }

  public String getCandidateUser() {
    return candidateUser;
  }

  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public String getProcessInstanceKey() {
    return processInstanceKey;
  }

  public float getPageSize() {
    return pageSize;
  }

  // Setter Methods

  public void setState(String state) {
    this.state = state;
  }

  public void setAssigned(boolean assigned) {
    this.assigned = assigned;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  public void setTaskDefinitionId(String taskDefinitionId) {
    this.taskDefinitionId = taskDefinitionId;
  }

  public void setCandidateUser(String candidateUser) {
    this.candidateUser = candidateUser;
  }

  public void setProcessDefinitionKey(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  public void setProcessInstanceKey(String processInstanceKey) {
    this.processInstanceKey = processInstanceKey;
  }

  public void setPageSize(float pageSize) {
    this.pageSize = pageSize;
  }

  public String getFirstName() {
    return firstName;
  }

  public String[] getCandidateGroups() {
    return candidateGroups;
  }

  public void setCandidateGroups(String[] candidateGroups) {
    this.candidateGroups = candidateGroups;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getOrderID() {
    return orderID;
  }

  public void setOrderID(String orderID) {
    this.orderID = orderID;
  }

  @Override
  public String toString() {
    return "TaskVariables [state="
        + state
        + ", assigned="
        + assigned
        + ", assignee="
        + assignee
        + ", taskDefinitionId="
        + taskDefinitionId
        + ", candidateUser="
        + candidateUser
        + ", processDefinitionKey="
        + processDefinitionKey
        + ", processInstanceKey="
        + processInstanceKey
        + ", pageSize="
        + pageSize
        + ", id="
        + id
        + ", name="
        + name
        + ", value="
        + value
        + ", firstName="
        + firstName
        + ", status="
        + status
        + ", orderID="
        + orderID
        + ", processName="
        + processName
        + ", creationDate="
        + creationDate
        + ", completionDate="
        + completionDate
        + ", taskState="
        + taskState
        + ", formKey="
        + formKey
        + ", isFirst="
        + isFirst
        + "]";
  }
}
