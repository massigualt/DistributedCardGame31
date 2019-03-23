package distributedLogic.net;

import distributedLogic.net.remote.IBroadcast;

public class ServiceBulk {
    private IBroadcast broadcast;
    private int id;
    private boolean anyCrash;

    public ServiceBulk(IBroadcast broadcast, int id, boolean anyCrash) {
        this.broadcast = broadcast;
        this.id = id;
        this.anyCrash = anyCrash;
    }

    public IBroadcast getBroadcast() {
        return broadcast;
    }
}
