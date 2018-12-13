package Agents;

import Etc.BehaviourKiller;
import Etc.Book;
import Etc.BookTitle;
import SellerBehaviours.WaitingForRequest;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.ArrayList;
import java.util.List;

public class AgentSeller extends Agent {
    private List<Book> bookList;

    private void createSettingForSeller (List<Book>  bookList) {
        if (this.getLocalName().equals("Seller1")){
            bookList.add(new Book(BookTitle.CrimeAndPunishment, 280));
            bookList.add(new Book(BookTitle.TheTaleofGoldenChicken,290));
        }
            else if (this.getLocalName().equals("Seller2")){
            bookList.add(new Book(BookTitle.CrimeAndPunishment, 285));
            bookList.add(new Book(BookTitle.WarAndPeace, 1300));
            bookList.add(new Book(BookTitle.TheTaleofGoldenChicken, 250));
        }
        else if (this.getLocalName().equals("Seller3")){
            bookList.add(new Book(BookTitle.WardN06, 150));
            bookList.add(new Book(BookTitle.WarAndPeace, 1260));
        }
        else{
            System.err.println( "Danger! Wrong Agent name: " + this.getLocalName());
        }
    }

    @Override
    protected void setup() {
        super.setup();
        bookList = new ArrayList<Book>();
        createSettingForSeller(bookList);
        DataStore dataStore = new DataStore();
        dataStore.put("bookList", bookList);
        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(getAID());
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("bookTrading");
        serviceDescription.setName(getName() + "-BT");
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        WaitingForRequest behaviour = new WaitingForRequest(this, dataStore);
        addBehaviour(behaviour);
        addBehaviour(new BehaviourKiller(this, 15000, behaviour));
    }
}
