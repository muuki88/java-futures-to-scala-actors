package futures;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import services.java.Item;
import services.java.ItemService;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Using java 7 java.util.concurrent and google guava utilities to distribute
 * work with futures.
 *
 */
public class Java7Futures {

    public void process(List<Integer> clients) throws InterruptedException, ExecutionException {
        Stopwatch watch = Stopwatch.createStarted();
        int parallelism = 4;
        ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newWorkStealingPool(parallelism));

        // Submit all the futures
        List<ListenableFuture<List<Item>>> itemFutures = new ArrayList<>();
        for (Integer client : clients) {
            ListenableFuture<List<Item>> future = pool.submit(new ItemLoader(client));
            itemFutures.add(future);
        }

        // convert list of futures to future of results
        ListenableFuture<List<List<Item>>> resultFuture = Futures.allAsList(itemFutures);

        // blocking until finished
        List<List<Item>> itemResults = resultFuture.get();

        // flatten
        Iterable<Item> items = Iterables.concat(itemResults);

        // group by price
        ListMultimap<Double, Item> groupedByPrice = Multimaps.index(items, new Function<Item, Double>() {

            @Override
            public Double apply(Item item) {
                return item.getPrice();
            }
        });

        for (Double price : groupedByPrice.keySet()) {
            List<Item> itemsPerPrice = groupedByPrice.get(price);
            Set<Integer> clientsPerPrice = new HashSet<>();
            for (Item item : itemsPerPrice) {
                clientsPerPrice.add(item.getClient());
            }
            System.out.println("PRICE: " + price);
            System.out.println(" Items  : " + itemsPerPrice.size());
            System.out.println(" Clients: " + clientsPerPrice.size());
        }

        pool.shutdown();
        System.out.println("Took " + watch.stop().elapsed(MILLISECONDS) + "ms");
    }

    public static class ItemLoader implements Callable<List<Item>> {

        private final int clientId;

        public ItemLoader(int clientId) {
            this.clientId = clientId;
        }

        @Override
        public List<Item> call() throws Exception {
            ItemService service = new ItemService();
            return service.getItems(clientId);
        }

    }

    /**
     * Running the program
     * 
     * @param args
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        new Java7Futures().process(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }
}
