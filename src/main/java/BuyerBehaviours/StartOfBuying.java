package BuyerBehaviours;

import Etc.BehaviourKiller;
import Etc.Book;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class StartOfBuying extends OneShotBehaviour {
    private Agent agent;
    private List<Book> bookList;

    public StartOfBuying(Agent a, DataStore dataStore) {
        this.agent = a;
        bookList = (List<Book>) dataStore.get("bookList");
        setDataStore(dataStore);
    }

    @Override
    public void action() {
        DFAgentDescription buyerTemplate = new DFAgentDescription();
        ServiceDescription wantedBook = new ServiceDescription();
        wantedBook.setType("bookTrading");
        buyerTemplate.addServices(wantedBook);
        List<AID> receivers = new ArrayList<AID>();
        try {
            DFAgentDescription[] result = DFService.search(agent, buyerTemplate);
            if (result.length == 0){
                System.out.println("There are no booktraders!");
            }else {
                for (int i = 0; i < result.length; ++i) {
                    receivers.add(result[i].getName());
                }
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
            getDataStore().put("sellersFound", receivers);
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.setProtocol("bookBuying");
            request.setContent(bookList.get(0) + "");
            for (AID rec : receivers) {
                request.addReceiver(rec);
            }
            agent.send(request);
            WaitingForResponse behaviourToKill = new WaitingForResponse(agent, getDataStore());
            agent.addBehaviour(behaviourToKill);
            agent.addBehaviour(new BehaviourKiller(agent, 1000, behaviourToKill));
        }

    }
