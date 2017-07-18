package vn.yotel.admin.jpa;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="CDR_FILE")
public class CDRLog implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//cdr_file_id
	private Long logId;
	@Id
	@Column(name="CDR_FILE_ID")
//	@SequenceGenerator(name="CDR_LOG_SEQ_GEN", sequenceName="CDR_LOG_SEQ")
//	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CDR_LOG_SEQ_GEN")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getLogId() {
		return logId;
	}
	public void setLogId(Long logId) {
		this.logId = logId;
	}
	
	//file_id
	private Long fileId;
	@Column(name="FILE_ID")
	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	// file_name
	private String fileName;

	@Column(name="FILE_NAME")
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	// file_seq
	private Integer fileSeq;

	@Column(name="FILE_SEQ")
	public Integer getFileSeq() {
		return fileSeq;
	}

	public void setFileSeq(Integer fileSeq) {
		this.fileSeq = fileSeq;
	}
	//file_date
	private Date fileDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="FILE_DATE")
	public Date getFileDate() {
		return fileDate;
	}
	
	public void setFileDate(Date fileDate) {
		this.fileDate = fileDate;
	}
	
	private Integer totalRec;

	@Column(name="TOTAL_RECORDS")
	public Integer getTotalRec() {
		return totalRec;
	}

	public void setTotalRec(Integer totalRec) {
		this.totalRec = totalRec;
	}
	
	private String action;

	@Column(name="ACTION")
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	private String dataSource;

	@Column(name="DATASOURCE")
	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
	private Integer status;
	
	@Column(name="STATUS")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	
	private Date processDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="PROCESS_DATE", updatable=false, insertable=false)
	public Date getProcessDate() {
		return processDate;
	}

	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}
	
	private String errorReason;
	
	@Column(name="ERROR_REASON")
	public String getErrorReason() {
		return errorReason;
	}
	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}
	
	
	private Long fileSize;
	@Column(name="FILE_SIZE")
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	private List<CDRLogDetail> details;
	
	@OneToMany(fetch=FetchType.LAZY,mappedBy="cdrLog", cascade=CascadeType.ALL)
	public List<CDRLogDetail> getDetails() {
		return details;
	}
	public void setDetails(List<CDRLogDetail> details) {
		this.details = details;
	}
	
}
