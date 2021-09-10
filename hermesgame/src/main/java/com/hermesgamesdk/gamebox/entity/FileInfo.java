package com.hermesgamesdk.gamebox.entity;

import java.io.Serializable;
import java.sql.Date;

/**
 *
 * 封裝所下載文件的信息 將FileInfo序列化，可用於Intent傳遞對象
 *
 */
public class FileInfo implements Serializable {
	private int id;
	private String url;
	private String fileName;
	private int length;
	private int finished;
	private String fileKey;
	private String icon;
	private String showName;

	public FileInfo() {
		super();
	}

	/**
	 *
	 * @param id   文件的ID
	 * @param url  文件的下載地址
	 * @param fileName  文件的名字
	 * @param length  文件的總大小
	 * @param finished  文件已經完成了多少
	 */
	public FileInfo(int id, String url, String fileName,String fileKey, int length, int finished,String icon,String showName) {
		super();
		this.id = id;
		this.url = url;
		this.fileName = fileName;
		this.length = length;
		this.finished = finished;
		this.fileKey = fileKey;
		this.icon = icon;
		this.showName = showName;
	}

	public String getIcon(){
		return icon;
	}

	public void setIcon(String s){
		this.icon = s;
	}

	public String getShowName(){
		return showName;
	}

	public void setShowName(String s){
		this.showName = s;
	}

	public String getPackageName(){
		return fileKey;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", url=" + url + ", fileName=" + fileName + ", length=" + length + ", finished="
				+ finished + "]";
	}

}
