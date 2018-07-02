package org.gephi.entity;

import java.lang.Thread.State;

public class Edge {
	/**
	 * consumeTime 该路径传播所需时间
	 * probability 沿该路径传播概率
	 * state  概率转换的真值
	 */
	public int eid;
	public String edgeName;
	public int consumeTime;
	public float probability;
	public Node startPoint;
	public Node endPoint;
	public boolean state;
	public boolean afterState;
	public int getEid() {
		return eid;
	}
	public void setEid(int eid) {
		this.eid = eid;
	}
	public String getEdgeName() {
		return edgeName;
	}
	public void setEdgeName(String edgeName) {
		this.edgeName = edgeName;
	}
	public int getConsumeTime() {
		return consumeTime;
	}
	public void setConsumeTime(int consumeTime) {
		this.consumeTime = consumeTime;
	}
	public float getProbability() {
		return probability;
	}
	public void setProbability(float probability) {
		this.probability = probability;
	}
	public Node getStartPoint() {
		return startPoint;
	}
	public void setStartPoint(Node startPoint) {
		this.startPoint = startPoint;
	}
	public Node getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(Node endPoint) {
		this.endPoint = endPoint;
	}
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	public boolean isAfterState() {
		return afterState;
	}
	public void setAfterState(boolean afterState) {
		this.afterState = afterState;
	}
	

}
