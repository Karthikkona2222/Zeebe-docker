package com.aaseya.AIS.dto;

import java.util.List;

public class ReviewDTO {

	private String questionId;
	private String question;
	private String answer;
	private String comments;
	private List<String> correctiveActions;
	{

	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public List<String> getCorrectiveActions() {
		return correctiveActions;
	}
	public void setCorrectiveActions(List<String> correctiveActions) {
		this.correctiveActions = correctiveActions;
	}
	@Override
	public String toString() {
		return "ReviewDTO [questionId=" + questionId + ", question=" + question + ", answer=" + answer + ", comments="
				+ comments + ", correctiveActions=" + correctiveActions + "]";
	}

		}