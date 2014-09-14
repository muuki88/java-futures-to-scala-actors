package services.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemService {

    private final Random random = new Random();
    
    /** random prices */
    private final double[] prices = {
      10.0,
      20.0,
      30.0,
      40.0,
      50.0,
      60.0,
      70.0,
      80.0,
      90.0
    };
    
    public List<Item> getItems(int client) {
        List<Item> items = new ArrayList<>();
        
        for(int i = 1; i <= 50; i++) {
            String id = Long.toHexString(random.nextLong());
            double price = prices[random.nextInt(9)];
            
            Item item = new Item(id, client, price);
            items.add(item);
            
            // Throw in some randoming waiting.
            // DB, webservices or shitty hardware
            try {
                Thread.sleep((long) (random.nextDouble() * 100L));
            } catch (InterruptedException e) {
                throw new RuntimeException("Why wake up?", e);
            }
        }
        System.out.println(Thread.currentThread().getName() + " : finished");
        return items;
    }
}