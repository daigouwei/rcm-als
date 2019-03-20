package com.gg.rcmals.service.guide.support;

import java.io.Serializable;

/**
 * @author guowei
 * @date 2019/2/23
 */
@SuppressWarnings("serial")
public class JavaDocument implements Serializable {
    private long id;
    private String text;

    public JavaDocument(long id, String text) {
        this.id = id;
        this.text = text;
    }

    public long getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }
}
