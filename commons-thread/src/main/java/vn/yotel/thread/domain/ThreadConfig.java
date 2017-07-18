package vn.yotel.thread.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.json.JSONObject;


@Entity
@Table(name="threads_config")
public class ThreadConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String className;
	private boolean autoStart;
	private int delayTime;
	private int order;
	private String schedule;
	private int threadGroup;
	private String paramsAsString;
	
	private JSONObject params = new JSONObject();
	
    @Id
    @Column(name = "thread_id")
//    @SequenceGenerator(allocationSize = 1, name = "ThreadConfigsGen", initialValue = 1, sequenceName = "threads_config_seq")
//    @GeneratedValue(generator = "ThreadConfigsGen", strategy = GenerationType.SEQUENCE)
    @GeneratedValue(strategy=GenerationType.IDENTITY)	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name="thread_name",nullable=false)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Type(type="yes_no")
	@Column(name="auto_start", nullable=false)
	public boolean isAutoStart() {
		return autoStart;
	}
	
	public void setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
	}
	
	@Column(name="class_name", nullable=false)
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	@Column(name="delay_time", nullable=false)
	public int getDelayTime() {
		return delayTime;
	}
	
	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}
	@Column(name="thread_order", nullable=false)
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	
	@Column(name="schedule", nullable=true)
	public String getSchedule() {
		return schedule;
	}
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	@Column(name="thread_group", nullable=false)
	public int getThreadGroup() {
		return threadGroup;
	}
	public void setThreadGroup(int threadGroup) {
		this.threadGroup = threadGroup;
	}
	
	@Column(name="params", nullable=false)
	public String getParamsAsString() {
		return paramsAsString;
	}
	public void setParamsAsString(String paramsAsString) {
		//params = new JSONObject(paramsAsString);
		this.paramsAsString = paramsAsString;
	}
	@Transient
	public JSONObject getParams() {
		return params;
	}
	
	public void setParams(JSONObject params) {
		paramsAsString = params.toString();
		this.params = params;
	}
}
