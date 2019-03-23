package distributedLogic.net.messages;

import distributedLogic.net.Link;
import distributedLogic.net.ServiceBulk;
import distributedLogic.net.router.AbstractRouter;
import distributedLogic.net.router.RouterFactory;

import java.rmi.RemoteException;

public class AYARouter extends AbstractRouter {


    public AYARouter(Link link, RouterFactory rmaker) {
        super(link);
    }

    @Override
    public void run() { super.run(); }

    /**
     * Metodo che esegue una chiamata remota RMI sul nodo vicino
     * @param to
     */
    @Override
    protected void performCallHook(ServiceBulk to) {
        try {
            to.getBroadcast().checkNode();
        } catch (RemoteException re) {
            re.printStackTrace();
            System.out.println("Remote Exception");
        }
    }
}
