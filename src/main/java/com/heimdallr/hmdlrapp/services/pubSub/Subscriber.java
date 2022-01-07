package com.heimdallr.hmdlrapp.services.pubSub;

public interface Subscriber {
    abstract void newContent(String info);
}
