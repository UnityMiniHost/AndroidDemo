package com.u3d.appwithhostsdkdemo.home.model;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private String id;
    private String appId;
    private String gameType;
    private String name;
    private List<String> tags;
    private String iconUrl;
    private String briefIntro;
    private String url;

    public GameModel() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String var1) {
        this.id = var1;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setGameType(String var1) {
        this.gameType = var1;
    }

    public String getGameType() {
        return this.gameType;
    }

    public void setAppId(String var1) {
        this.appId = var1;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String var1) {
        this.name = var1;
    }

    public List<String> getTags() {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }

        return this.tags;
    }

    public void setTags(List<String> var1) {
        this.tags = var1;
    }

    public String getBriefIntro() {
        return this.briefIntro;
    }

    public void setIconUrl(String var1) {
        this.iconUrl = var1;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setBriefIntro(String var1) {
        this.briefIntro = var1;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String var1) {
        this.url = var1;
    }
}
