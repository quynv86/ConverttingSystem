package vn.yotel.admin.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Time;


/**
 * The persistent class for the schedule_detail database table.
 * 
 */
@Entity
@Table(name="schedule_detail")
@NamedQuery(name="ScheduleDetail.findAll", query="SELECT s FROM ScheduleDetail s")
public class ScheduleDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="day_id")
	private byte dayId;

	@Column(name="end_time")
	private Time endTime;

	@Column(name="start_time")
	private Time startTime;

	//bi-directional many-to-one association to Schedule
	@ManyToOne(fetch=FetchType.LAZY)
	private Schedule schedule;

	public ScheduleDetail() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte getDayId() {
		return this.dayId;
	}

	public void setDayId(byte dayId) {
		this.dayId = dayId;
	}

	public Time getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	public Time getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Schedule getSchedule() {
		return this.schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

}