package vn.yotel.admin.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="HEADER_CONFIG")
public class HeaderConfig  implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer threadId;

	private List<CDRColumn> fixedColumns;
	private List<CDRColumn> exportColumns;
	private List<CDRColumn> avaibleColumns;
	
	private List<String> selectedAdd;
	private List<String> selectedRemove;
	

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Basic
	@Column(name="thread_id")
	public Integer getThreadId() {
		return threadId;
	}
	public void setThreadId(Integer threadId) {
		this.threadId = threadId;
	}
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "fixed_column", joinColumns = {
			@JoinColumn(name = "header_conf_id", nullable = false, updatable = false) },
			inverseJoinColumns = { @JoinColumn(name = "column_id",
					nullable = false, updatable = false) })

	public List<CDRColumn> getFixedColumns() {
		return fixedColumns;
	}
	public void setFixedColumns(List<CDRColumn> fixedColumns) {
		this.fixedColumns = fixedColumns;
	}

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "export_column", joinColumns = {
			@JoinColumn(name = "header_conf_id", nullable = false, updatable = false) },
			inverseJoinColumns = { @JoinColumn(name = "column_id",
					nullable = false, updatable = false) })

	public List<CDRColumn> getExportColumns() {
		return exportColumns;
	}
	public void setExportColumns(List<CDRColumn> exportColumns) {
		this.exportColumns = exportColumns;
	}
	
	@Transient
	public List<CDRColumn> getAvaibleColumns() {
		if(avaibleColumns!=null){
			avaibleColumns.clear();
		}else{
			avaibleColumns = new ArrayList<CDRColumn>();
		}
		for(CDRColumn it:fixedColumns){
			boolean found = false;
			for(CDRColumn other : exportColumns){
				if(it.getId().intValue() == other.getId().intValue()){
					found = true;
					break;
				}
			}
			if(!found){
				avaibleColumns.add(it);
			}
		}
		return avaibleColumns;
	}
	public void setAvaibleColumns(List<CDRColumn> avaibleColumns) {
		this.avaibleColumns = avaibleColumns;
	}
	@Transient
	public List<String> getSelectedAdd() {
		return selectedAdd;
	}
	public void setSelectedAdd(List<String> selectedAdd) {
		this.selectedAdd = selectedAdd;
	}
	@Transient
	public List<String> getSelectedRemove() {
		return selectedRemove;
	}
	public void setSelectedRemove(List<String> selectedRemove) {
		this.selectedRemove = selectedRemove;
	}

	
}
