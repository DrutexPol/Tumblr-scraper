import java.io.File;
import java.io.FileNotFoundException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

public class ProxyService {
    private ArrayList<ScrapingProxy> proxyList;
    private ScheduledExecutorService scheduler;
    private BlockingQueue<ScrapingProxy> proxyQueue;
    private int delay;

    public ArrayList<ScrapingProxy> getProxyList() {
        return proxyList;
    }

    public ProxyService(int delay) {
        this.delay = delay;
        proxyList = new ArrayList<ScrapingProxy>();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        proxyQueue = new ArrayBlockingQueue<ScrapingProxy>(1024);
    }


    public void addProxy(String proxy) {
        String[] tab = proxy.split(":");
        if (tab.length == 2 | tab.length == 3) {
            ScrapingProxy sp = new ScrapingProxy(tab[0], tab[1], "", "");
            try {
                proxyQueue.put(sp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            proxyList.add(sp);
        } else {
            ScrapingProxy sp = new ScrapingProxy(tab[0], tab[1], tab[2], tab[3]);
            try {
                proxyQueue.put(sp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            proxyList.add(sp);
        }
    }

    public ProxyService(File file) {
        try {
            proxyList = new ArrayList<ScrapingProxy>();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String[] tab = scanner.nextLine().split(":");
                if (tab.length == 2 | tab.length == 3) {
                    proxyList.add(new ScrapingProxy(tab[0], tab[1], "", ""));
                } else {
                    proxyList.add(new ScrapingProxy(tab[0], tab[1], tab[2], tab[3]));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        scheduler = Executors.newSingleThreadScheduledExecutor();
        proxyQueue = new ArrayBlockingQueue<ScrapingProxy>(99999);
        for (ScrapingProxy proxy : proxyList) {
            try {
                proxyQueue.put(proxy);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized ScrapingProxy getProxy() {
        ScrapingProxy sproxy2 = null;
        try {
            final ScrapingProxy sproxy = proxyQueue.take();
            scheduler.schedule(new Runnable() {
                                   @Override
                                   public void run() {
                                       try {
                                           proxyQueue.put(sproxy);
                                       } catch (InterruptedException e) {
                                           e.printStackTrace();
                                       }
                                   }
                               }
                    , delay, TimeUnit.MILLISECONDS);
            sproxy2 = sproxy;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sproxy2;
    }

    public void end() {
        scheduler.shutdownNow();
    }
}
