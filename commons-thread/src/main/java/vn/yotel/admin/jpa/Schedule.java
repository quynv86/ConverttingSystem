package vn.yotel.admin.jpa;

import java.io.Serializable;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;


/**
 * The persistent class for the schedule database table.
 * 
 */
@Entity
@Table(name="schedule")
@NamedQuery(name="Schedule.findAll", query="SELECT s FROM Schedule s")
public class Schedule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String name;

	private byte status;

	private String type;

	//bi-directional many-to-one association to ScheduleDetail
	@OneToMany(mappedBy="schedule")
	private List<ScheduleDetail> scheduleDetails = new ArrayList<ScheduleDetail>();
	
	public Schedule() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getStatus() {
		return this.status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ScheduleDetail> getScheduleDetails() {
		return this.scheduleDetails;
	}

	public void setScheduleDetails(List<ScheduleDetail> scheduleDetails) {
		this.scheduleDetails = scheduleDetails;
	}

	public ScheduleDetail addScheduleDetail(ScheduleDetail scheduleDetail) {
		getScheduleDetails().add(scheduleDetail);
		scheduleDetail.setSchedule(this);

		return scheduleDetail;
	}

	public ScheduleDetail removeScheduleDetail(ScheduleDetail scheduleDetail) {
		getScheduleDetails().remove(scheduleDetail);
		scheduleDetail.setSchedule(null);

		return scheduleDetail;
	}

}