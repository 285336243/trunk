package com.jiujie8.choice.persioncenter.entity;

import com.jiujie8.choice.home.entity.ChoiceItem;

import java.io.Serializable;

/**
 * Created by 51wanh on 2015/1/23.
 */
public class PostAgreeModel implements Serializable {
    private ChoiceItem choice;
    private int postAgreeStatus;

    public ChoiceItem getChoice() {
        return choice;
    }

    public void setChoice(ChoiceItem choice) {
        this.choice = choice;
    }

    public int getPostAgreeStatus() {
        return postAgreeStatus;
    }

    public void setPostAgreeStatus(int postAgreeStatus) {
        this.postAgreeStatus = postAgreeStatus;
    }
}
