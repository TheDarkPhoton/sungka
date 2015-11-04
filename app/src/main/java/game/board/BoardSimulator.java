package game.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import game.player.Human;
import game.player.Player;
import helpers.Node;

public class BoardSimulator extends Board {
    private Node<State> _current;
    private List<Node<State>> _leafs = new ArrayList<>();

    private enum Explore{MIN, MAX}

    /**
     * Constructs board with default attributes.
     */
    public BoardSimulator(Board board) {
        super(new Human("A"), new Human("B"));
        if (board.isPlayerA(board.getCurrentPlayer()))
            _currentPlayer = getPlayerA();
        else
            _currentPlayer = getPlayerB();

        _current = new Node<>(new State(0));
        _current.getElement().setPlayer(getCurrentPlayer());
        _current.getElement().setValue(0);
        _current.getElement().setState(getState());
    }

    private void minSort(List<Node<State>> state){
        Collections.sort(state, new Comparator<Node<State>>() {
            @Override
            public int compare(Node<State> lhs, Node<State> rhs) {
                return lhs.getElement().getValue().compareTo(rhs.getElement().getValue());
            }
        });
    }

    private void maxSort(List<Node<State>> state){
        Collections.sort(state, new Comparator<Node<State>>() {
            @Override
            public int compare(Node<State> lhs, Node<State> rhs) {
                Node<State> lhsLeaf = lhs;
                while (lhsLeaf.getChildrenCount() > 0)
                    lhsLeaf = lhsLeaf.getFirstChild();

                Node<State> rhsLeaf = rhs;
                while (rhsLeaf.getChildrenCount() > 0)
                    rhsLeaf = rhsLeaf.getFirstChild();

                return rhsLeaf.getElement().getValue().compareTo(lhsLeaf.getElement().getValue());
            }
        });
    }

    private boolean givesExtraMove(int index, Player player){
        if (isPlayerA(player))
            return (7 - (_cups[index].getCount() + 1) == index);
        else
            return (15 - (_cups[index].getCount() + 1) == index);
    }

    private boolean takesExtraMove(int index, Player player){
        if (isPlayerA(player))
            return (7 - _cups[index].getCount() == index);
        else
            return (15 - _cups[index].getCount() == index);
    }

    private Integer[] getState(){
        Integer[] state = new Integer[16];
        for (int i = 0; i < _cups.length; i++)
            state[i] = _cups[i].getCount();

        return state;
    }
    private void loadState(State state){
        _currentPlayer = state.getPlayer();
        _validMoveExists = !state.isGoal();
        for (int i = 0; i < state.getState().length; i++)
            _cups[i].setShells(state.getState()[i]);
    }

    private Node<State> exploreState(Node<State> parent, int index){
        Player player = getCurrentPlayer();
        Node<State> leaf = new Node<>(new State(index));
        int score = 0;

        HandOfShells hand = pickUpShells(index);
        if (hand != null)
            ++score;
        else
            leaf.getElement().setDeadEnd();

        while (hand != null && !hand.isEmpty()){
            int i = hand.getNextCup();

            if (player.isPlayersCup(_cups[i], true)){
                if (hand.getShellCount() == 1){
                    leaf.getElement().setExtraTurn();
                    score += 8;
                }
                score += 2;
            }
            else{
                if (givesExtraMove(i, player))
                    ++score;
                else if  (takesExtraMove(i, player))
                    score -= 2;
            }

            HandOfShells robbersHand = hand.dropShell();
            if (robbersHand != null){
                boolean handBelongsToPlayerA = isPlayerA(robbersHand.belongsToPlayer());
                robbersHand.setNextCup(handBelongsToPlayerA ? 15 : 7);
                score += robbersHand.getShellCount() * 2;
                robbersHand.dropAllShells();
            }
        }

        for (BoardState state : _state_messages){
            switch (state){
                case PLAYER_A_GETS_ANOTHER_TURN:
                case PLAYER_B_GETS_ANOTHER_TURN:
                    break;
                case PLAYER_A_WAS_ROBBED_OF_HIS_FINAL_MOVE:
                case PLAYER_B_WAS_ROBBED_OF_HIS_FINAL_MOVE:
                    score += 2;
                    break;
                case GAME_OVER:
                    leaf.getElement().setGoal();
                    break;
            }
        }
        _state_messages.clear();

        if (isPlayerA(player))
            leaf.getElement().setValue(parent.getElement().getValue() + score);
        else
            leaf.getElement().setValue(parent.getElement().getValue() - score);

        leaf.getElement().setPlayer(getCurrentPlayer());
        leaf.getElement().setState(getState());

        return leaf;
    }

    private void exploreNode(Node<State> state, List<Node<State>> newLeafs, int index){
        List<Node<State>> statesToBeExpanded = new ArrayList<>();
        statesToBeExpanded.add(state);

        for (int i = 0; i < statesToBeExpanded.size(); i++) {
            boolean noLeafsFound = true;
            for (int e = index; e < index + 7; e++) {
                Node<State> s = statesToBeExpanded.get(i);
                loadState(s.getElement());

                Node<State> node = exploreState(s, e);
                if (node.getElement().leadsToExtraTurn() && getOpponent().hasValidMove()) {
                    statesToBeExpanded.add(node);
                    s.addChild(node);
                    noLeafsFound = false;
                }
                else if (!node.getElement().isDeadEnd()) {
                    newLeafs.add(node);
                    s.addChild(node);
                    noLeafsFound = false;
                }
            }

            if (noLeafsFound)
                newLeafs.add(statesToBeExpanded.get(i));
        }
    }

    private void explore(List<Node<State>> initialLeafs, List<Node<State>> resultLeafs, Explore type){
        for (int i = 0; i < initialLeafs.size(); i++){
            List<Node<State>> newLeafs = new ArrayList<>();

            exploreNode(
                    initialLeafs.get(i),
                    newLeafs,
                    type == Explore.MAX ? 0 : 8);

            if (type == Explore.MIN) {
                minSort(newLeafs);
                newLeafs = newLeafs.subList(0, newLeafs.size() < 7 ? newLeafs.size() : 7);
                explore(newLeafs, resultLeafs, Explore.MAX);
            }
            else {
                maxSort(newLeafs);
                newLeafs = newLeafs.subList(0, newLeafs.size() < 1 ? newLeafs.size() : 1);
                resultLeafs.addAll(newLeafs);
            }
        }
    }

    public void explore(){
        _leafs = new ArrayList<>();
        List<Node<State>> initialLeafs = new ArrayList<>();
        initialLeafs.add(_current);

        explore(initialLeafs, _leafs, Explore.MIN);

        minSort(_leafs);
    }

    public int findBestMove() {
        Node<State> chosenState = _leafs.get(0);
        if (chosenState.getParent() != null){
            while (chosenState.getParent().getParent() != null){
                chosenState = chosenState.getParent();
            }
        }

        loadState(chosenState.getElement());
        chosenState.setParent(null);
        _current = chosenState;

        return _current.getElement().getIndex();
    }

    public void doMove(int index){
        _leafs = new ArrayList<>();
        _leafs.add(exploreState(_current, index));
        findBestMove();
    }
}
