package com.mysema.edith.ui.components;

import com.sun.xml.internal.ws.api.PropertySet.Property;

public class InfoMessage {

    @Inject
    @Property
    private Block infoMessageBlock;

    @Inject
    @Property
    private Messages messages;

    @Property
    private String info;

    @Property
    private String error;

    public Block getBlock() {
        return infoMessageBlock;
    }

    public void addInfoMsg(String key) {
        info = key;
    }

    public String getInfoMsg() {
        return info != null ? messages.get(info) : "";
    }

    public String getErrorMsg() {
        return error != null ? messages.get(error) : "";
    }

    public void addErrorMsg(String key) {
        error = key;
    }

}
