package it.smartcommunitylab.climb.domain.model;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PedibusPlayer extends BaseObject {

	private String nickname;
	private String classRoom;	
	private String pedibusGameId;

	public String getClassRoom() {
		return classRoom;
	}

	public void setClassRoom(String classRoom) {
		this.classRoom = classRoom;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPedibusGameId() {
		return pedibusGameId;
	}

	public void setPedibusGameId(String pedibusGameId) {
		this.pedibusGameId = pedibusGameId;
	}
	
}
