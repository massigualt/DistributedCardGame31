package distributedLogic.net;

import distributedLogic.net.remote.IBroadcast;

public class ServiceBulk {
    private IBroadcast broadcast;
    private int id;

    public ServiceBulk(IBroadcast broadcast, int id) {
        this.broadcast = broadcast;
        this.id = id;
    }

    public IBroadcast getBroadcast() {
        return broadcast;
    }
}
