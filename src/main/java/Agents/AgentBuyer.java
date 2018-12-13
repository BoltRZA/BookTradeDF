package Agents;

import BuyerBehaviours.StartOfBuying;
import Etc.Book;
import Etc.BookTitle;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;

import java.util.ArrayList;
import java.util.List;

public class AgentBuyer extends Agent {
    private List<Book> buyingBooks;
    private AID bestSeller;
    private double bestPrice;


    @Override
    protected void setup() {
        super.setup();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        buyingBooks = new ArrayList<Book>();
        buyingBooks.add(new Book(BookTitle.TheTaleofGoldenChicken));
        buyingBooks.add(new Book(BookTitle.CrimeAndPunishment));
        DataStore dataStore = new DataStore();
        dataStore.put("bookList", buyingBooks);
        addBehaviour(new StartOfBuying(this, dataStore));

    }
}
