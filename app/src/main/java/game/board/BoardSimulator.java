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
                if (_cups[i].isEmpty()){
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

    private List<Node<State>> exploreNode(Node<State> state, int index, Explore type){
        List<Node<State>> extraTurnStates = new ArrayList<>();
        for (int i = index; i < index + 7; i++) {
            Node<State> node = exploreState(state, i);
            if (node.getElement().leadsToExtraTurn() && !node.getElement().isGoal()) {
                extraTurnStates.add(node);
                state.addChild(node);
            }
            else if (!node.getElement().isDeadEnd()) {
                if (type == Explore.MIN) _leafs.add(node);
                state.addChild(node);
            }

            loadState(state.getElement());
        }

        return extraTurnStates;
    }

    private void minExplore(){
        List<Node<State>> leafs = _leafs;
        _leafs = new ArrayList<>();

        for (int i = 0; i < leafs.size(); i++){
            loadState(leafs.get(i).getElement());
            leafs.addAll(exploreNode(
                            leafs.get(i),
                            isPlayerA(getCurrentPlayer()) ? 0 : 8,
                            Explore.MIN));
        }

        loadState(_current.getElement());

        minSort(_leafs);
        _leafs = _leafs.subList(0, _leafs.size() < 7 ? _leafs.size() : 7);
    }

    private void maxExplore(){
        List<Node<State>> leafs = new ArrayList<>(_leafs);
        for (int i = 0; i < leafs.size(); i++) {
            loadState(leafs.get(i).getElement());
            leafs.addAll(exploreNode(
                    leafs.get(i),
                    isPlayerA(getCurrentPlayer()) ? 0 : 8,
                    Explore.MAX));
        }

        loadState(_current.getElement());

        leafs = _leafs;
        _leafs = new ArrayList<>();
        for (int i = 0; i < leafs.size(); i++){
            List<Node<State>> leafChildren = new ArrayList<>();
            List<Node<State>> children = leafs.get(i).getChildren();
            for (int e = 0; e < children.size(); e++) {
                if (children.get(e).getChildrenCount() == 0)
                    leafChildren.add(children.get(e));
                else
                    children.addAll(children.get(e).getChildren());
            }

            if (leafChildren.size() > 0){
                maxSort(leafChildren);
                _leafs.add(leafChildren.get(0));
            }
        }

        if (_leafs.size() > 0)
            minSort(_leafs);
        else
            _leafs.addAll(leafs);
    }

    public void explore(){
        _leafs = new ArrayList<>();
        _leafs.add(_current);

        minExplore();
        maxExplore();
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
