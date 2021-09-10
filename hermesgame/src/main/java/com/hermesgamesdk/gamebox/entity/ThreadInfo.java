package com.hermesgamesdk.gamebox.entity;
/**
 *
 * 綫程信息類，封裝綫程的ID，綫程的url，綫程開始位置，結束位置，以及已經完成的位置
 *
 */
public class ThreadInfo {
	private int id;
	private String url;
	private int start;
	private int end;
	private int finished;
	private String packageName;
	private String savePath = "";
	private int isDownFinish;
	private String icon = "";
	private String showName = "";
	private int lastChangeTime = 0;

	public ThreadInfo() {
		super();
	}
	/**
	 *
	 * @param id 綫程的ID
	 * @param url 下載文件的網絡地址
	 * @param start 綫程下載的開始位置
	 * @param end 綫程下載的結束位置
	 * @param finished	綫程已經下載到哪個位置
	 */
	public ThreadInfo(int id, String url, int start, int end, int finished,String packName,String icon,String showName) {
		super();
		this.id = id;
		this.url = url;
		this.start = start;
		this.end = end;
		this.finished = finished;
		this.packageName = packName;
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

	public int getId() {
		return id;
	}

	public String getPackageName(){
		return this.packageName;
	}

	public void setPackName(String pack){
		this.packageName = pack;
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

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	public void setSavePath(String s) {
		this.savePath = s;
	}

	public String getSavePath() {
		return this.savePath;
	}

	public void setIsDownloaded(int s) {
		this.isDownFinish = s;
	}

	public int getIsDownloaded() {
		return this.isDownFinish;
	}

	public void setLastChangeTime(int s){
		this.lastChangeTime = s;
	}

	@Override
	public String toString() {

		float downLoadRate = ((float)finished / end)*100;
		String rate = String.format("%.2f", downLoadRate);
		return "{\"id\":\"" + String.valueOf(id) + "\", \"url\":\"" + url + "\", \"start\":\"" + start + "\", \"end\":\"" + end + "\", \"finished\":\"" + finished + "\",\"packageName\":\"" + packageName
				+ "\",\"isDownFinish\":\""+isDownFinish+"\",\"rate\":\""+rate+"\",\"showName\":\""+showName+"\",\"icon\":\""+icon+"\",\"lastChangeTime\":"+String.valueOf(lastChangeTime)+"}";
	}

}
