package name.shamansir.sametimed.wave.model.base.atom;

public class InboxElement {

	private String waveID;
	private boolean isCurrent;
	private boolean isNew;
	private String digest;
	
	public InboxElement(String waveID, String digest, boolean isCurrent, boolean isNew) {
		this.waveID = waveID;
		this.digest = digest;
		this.isCurrent = isCurrent;
		this.isNew = isNew;
	}
	
	public InboxElement(String waveID) {
		this(waveID, "", false, true);
	}
	
	public String getWaveID() {
		return waveID;
	}
	
	public boolean isOpened() {
		return isCurrent;
	}
	
	public boolean isNew() {
		return isNew;
	}
	
	public String getDigest() {
		return digest;
	}	

}
