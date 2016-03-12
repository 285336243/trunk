package com.jiujie8.choice.home.entity;

import java.io.Serializable;

/**
 * Created by wlanjie on 14/12/4.
 */
public class ChoiceMode implements Serializable{
    private static final long serialVersionUID = -4082968723822447363L;
    private ChoiceItem choice;
    private Vote vote;
    private Favorite favorite;
    private boolean isAnimatorRunning;
    private int postAgreeStatus;

    public ChoiceItem getChoice() {
        return choice;
    }

    public void setChoice(ChoiceItem choice) {
        this.choice = choice;
    }

    public Vote getVote() {
        return vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }

    public Favorite getFavorite() {
        return favorite;
    }

    public void setFavorite(Favorite favorite) {
        this.favorite = favorite;
    }

    public boolean isAnimatorRunning() {
        return isAnimatorRunning;
    }

    public void setAnimatorRunning(boolean isAnimatorRunning) {
        this.isAnimatorRunning = isAnimatorRunning;
    }

    public int getPostAgreeStatus() {
        return postAgreeStatus;
    }

    public void setPostAgreeStatus(int postAgreeStatus) {
        this.postAgreeStatus = postAgreeStatus;
    }
}
