package com.jiujie8.choice.setting.entity;


/**
 * 设置bean
 */
public class SetBean {
    private String text;
    private int color;

    public SetBean(String text) {
        this.text = text;
    }

    public SetBean(String text, int color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
