package org.gephi.entity;

import java.util.ArrayList;

public class Node implements Comparable {
	/**
	 * actived 是否激活 activeTime 激活时间
	 */
	public boolean isolated;
	public int nid;
	public String nodeName;
	public boolean actived;
	public int activeTime;
	public int activeTimeAfter;
	public ArrayList<Node> list;

	public boolean isIsolated() {
		return isolated;
	}

	public void setIsolated(boolean isolated) {
		this.isolated = isolated;
	}

	public int getNid() {
		return nid;
	}

	public void setNid(int nid) {
		this.nid = nid;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public boolean isActived() {
		return actived;
	}

	public void setActived(boolean actived) {
		this.actived = actived;
	}

	public int getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(int activeTime) {
		this.activeTime = activeTime;
	}

	public int getActiveTimeAfter() {
		return activeTimeAfter;
	}

	public void setActiveTimeAfter(int activeTimeAfter) {
		this.activeTimeAfter = activeTimeAfter;
	}

	public ArrayList<Node> getList() {
		return list;
	}

	public void setList(ArrayList<Node> list) {
		this.list = list;
	}

	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		Node s = (Node) o;
		if (this.getActiveTime() > s.getActiveTime())
			return 1;
		else if (this.getActiveTime() < s.getActiveTime())
			return -1;
		else {
			if (this.getNodeName().compareTo(s.getNodeName()) > 0)
				return 1;
			else
				return -1;

		}
	}

}
