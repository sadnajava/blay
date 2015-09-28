package com.squeaker.squeaker;

/**
 * Keeps track of the link to the Squeaker server.
 */
public class ServerState {
    private final Session session;
    private final ServerApi api;

    public ServerState(Session session, ServerApi api) {
        this.session = session;
        this.api = api;
    }

    public Session getSession() {
        return session;
    }

    public ServerApi getApi() {
        return api;
    }
}
