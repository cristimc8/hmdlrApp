package com.heimdallr.hmdlrapp.services.pubSub;

public abstract class Subscriber {
    protected abstract void newContent(String info);
}
