package com.wec.community.entity;

import java.util.Date;

public class LoginTicket {
    private int id;
    private int UserId;
    private String ticket;
    private int status;
    private Date expired; //到期时间

    @Override
    public String toString() {
        return "LoginTicket{" +
                "id=" + id +
                ", UserId=" + UserId +
                ", ticket='" + ticket + '\'' +
                ", status=" + status +
                ", expired=" + expired +
                '}';
    }

    public LoginTicket() {
    }

    public LoginTicket(int id, int userId, String ticket, int status, Date expired) {
        this.id = id;
        UserId = userId;
        this.ticket = ticket;
        this.status = status;
        this.expired = expired;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }
}
