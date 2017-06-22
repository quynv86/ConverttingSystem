package vn.yotel.admin.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="CDR_LOG_DETAIL")
public class CDRLogDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private Date processDate;

	private CDRLog cdrLog;
	
	private Date fileDate;
	private String key;
	private Integer value;
	
	@Id
    @Column(name = "ID")
    @SequenceGenerator(allocationSize = 1, name = "CDRDetailSeqGen", initialValue = 1, sequenceName = "cdr_file_detail_seq")
    @GeneratedValue(generator = "CDRDetailSeqGen", strategy = GenerationType.SEQUENCE)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="PROCESS_DATE", insertable=false, updatable=false)
	public Date getProcessDate() {
		return processDate;
	}
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}
		
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="FILE_DATE")
	public Date getFileDate() {
		return fileDate;
	}
	public void setFileDate(Date fileDate) {
		this.fileDate = fileDate;
	}
	
	@Column(name="KEY")
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	@Column(name="VALUE")
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	
	@ManyToOne
	@JoinColumn(name="CDR_FILE_ID")
	public CDRLog getCdrLog() {
		return cdrLog;
	}
	public void setCdrLog(CDRLog cdrLog) {
		this.cdrLog = cdrLog;
	}
	
}	
