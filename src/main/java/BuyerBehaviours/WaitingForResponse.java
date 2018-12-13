package BuyerBehaviours;


import Etc.BehaviourKiller;
import Etc.Book;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;


public class WaitingForResponse extends Behaviour {

    private Agent agent;
    private double bestPrice = 10000000;
    private AID bestSeller = null;
    private List<AID> receivers;
    private List<Book> bookList;
    private int receiversCounter;
    private boolean behaviourDone = false;

    public WaitingForResponse (Agent agent, DataStore ds){
        super();
        this.agent = agent;
        setDataStore(ds);
        this.receivers = (List<AID>) ds.get("sellersFound");
        this.bookList = (List<Book>) ds.get("bookList");
        receiversCounter = receivers.size();
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchProtocol("bookBuying"),
                MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.CANCEL),
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM)));
        ACLMessage response = agent.receive(mt);

        if (response != null) {
            if (response.getPerformative() == ACLMessage.INFORM) {
                receiversCounter--;
                System.out.println("Agent " + agent.getLocalName() + " said:" + "I've received the price " +
                        response.getContent() + " from " + response.getSender().getLocalName());
                double price = Double.parseDouble(response.getContent());

                if (price < bestPrice) {
                    bestSeller = response.getSender();
                    bestPrice = price;
                }
            } else if (response.getPerformative() == ACLMessage.CANCEL){
                receiversCounter--;
                System.out.println("Agent " + agent.getLocalName() + " said:" +
                        response.getSender().getLocalName() + " hasn't got "  +
                        bookList.get(0).getTitle());
            }
        } else {
            block();
        }
        if (receiversCounter == 0) {
            behaviourDone = true;
        }
    }

    @Override
    public boolean done() {
        return behaviourDone;
    }

    @Override
    public int onEnd() {
        if (behaviourDone && bestSeller != null) {
            System.out.println("Winner is "  + bestSeller.getLocalName());
            SendProposal behaviour = new SendProposal(agent, getDataStore(), bestSeller, bestPrice);
            agent.addBehaviour(behaviour);
            agent.addBehaviour(new BehaviourKiller(agent,2000, behaviour));
        } else {
            System.out.println(" Seller " + agent.getLocalName() + "not found!" );
            agent.addBehaviour(new WakerBehaviour(agent, 1000) {
                @Override
                protected void onWake() {
                    super.onWake();
                    agent.addBehaviour(new StartOfBuying(agent, getDataStore()));
                }
            });
        }
        return super.onEnd();
    }
}