
public class Player {
	
	private String fullName;
	private String fullTeamName;
	private String leagueName;
	
	public Player(String fullName, String fullTeamName, String leagueName) {
		this.fullName = fullName;
		this.fullTeamName = fullTeamName;
		this.leagueName = leagueName;
	}

	public String getLeagueName() {
		return leagueName;
	}

	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullTeamName() {
		return fullTeamName;
	}

	public void setFullTeamName(String fullTeamName) {
		this.fullTeamName = fullTeamName;
	}

}
