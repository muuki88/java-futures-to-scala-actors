package futures;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import services.java.Item;
import services.java.ItemService;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

public class Java8Streams {

    public void process(List<Integer> clients) throws InterruptedException, ExecutionException {
        Stopwatch watch = Stopwatch.createStarted();
        //
        Stream<List<Item>> serviceResults = clients.parallelStream() //
                .map(client -> new ItemService().getItems(client));

        Stream<Item> items = serviceResults.flatMap(itemList -> itemList.stream());

        Map<Double, List<Item>> groupedByPrice = items.collect(groupingBy(item -> item.getPrice()));

        for (Entry<Double, List<Item>> e : groupedByPrice.entrySet()) {
            Double price = e.getKey();
            List<Item> itemsPerPrice = e.getValue();
            Map<Integer, List<Item>> itemsPerClient = itemsPerPrice.stream().collect(groupingBy(item -> item.getClient()));

            System.out.println("PRICE: " + price);
            System.out.println(" Items  : " + itemsPerPrice.size());
            System.out.println(" Clients: " + itemsPerClient.size());
        }

        System.out.println("Took " + watch.stop().elapsed(MILLISECONDS) + "ms");
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        new Java8Streams().process(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }
}
