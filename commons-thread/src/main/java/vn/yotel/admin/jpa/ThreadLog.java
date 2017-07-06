package vn.yotel.admin.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="THREADS_LOG")
@NamedNativeQueries({
	@NamedNativeQuery(
	name = "loadLogByThread",
	query = "select * from THREADS_LOG t where t.thread_id=:thread_id and log_date >= sysdate -30 and rownum <2000 order by log_date desc",
        resultClass = ThreadLog.class)	
})
public class ThreadLog implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long logId;
	private Long threadId;
	private Integer logLevel;
	private Date logDate;
	private String content;
	
	@Id
	@Column(name="LOG_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	@Column(name="THREAD_ID")
	public Long getThreadId() {
		return threadId;
	}
	public void setThreadId(Long threadId) {
		this.threadId = threadId;
	}

	@Column(name="LOG_LEVEL")
	public Integer getLogLevel() {
		return logLevel;
	}
	public void setLogLevel(Integer logLevel) {
		this.logLevel = logLevel;
	}
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOG_DATE")
	public Date getLogDate() {
		return logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

	@Column(name="LOG_CONTENT")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
