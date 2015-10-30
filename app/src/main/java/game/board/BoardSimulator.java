package game.board;

import game.player.Human;
import game.player.Player;
import helpers.Node;
import helpers.Tree;

public class BoardSimulator extends Board {
    private Tree<State> _states;
    private Node<State> _current;

    /**
     * Constructs board with default attributes.
     */
    public BoardSimulator(Board board) {
        super(new Human("A"), new Human("B"));
        if (board.isPlayerA(board.getCurrentPlayer()))
            _currentPlayer = getPlayerA();
        else
            _currentPlayer = getPlayerB();

        _states = new Tree<>(new State(_currentPlayer, 0, 0, getState()));
        _current = _states.getRoot();
    }

    private boolean givesExtraMove(int index){
        if (isPlayerA(getCurrentPlayer()))
            return (7 - (_cups[index].getCount() + 1) == index);
        else
            return (15 - (_cups[index].getCount() + 1) == index);
    }

    private boolean takesExtraMove(int index){
        if (isPlayerA(getCurrentPlayer()))
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
        for (int i = 0; i < state.getState().length; i++)
            _cups[i].setShells(state.getState()[i]);
    }

    private void exploreState(int index){
        int score = 0;

        HandOfShells hand = pickUpShells(index);
        if (hand != null)
            ++score;

        while (hand != null && !hand.isEmpty()){
            int i = hand.getNextCup();

            if (_currentPlayer.isPlayersCup(_cups[i], true))
                score += 2;
            else{
                if (givesExtraMove(i))
                    ++score;
                else if  (takesExtraMove(i))
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
                    score += 8;
                    break;
                case PLAYER_A_WAS_ROBBED_OF_HIS_FINAL_MOVE:
                case PLAYER_B_WAS_ROBBED_OF_HIS_FINAL_MOVE:
                    score += 2;
                    break;
            }
        }
        _state_messages.clear();

        _current.addChild(new Node<>(new State(getCurrentPlayer(), index, score, getState())));
    }

    public void explore(){
        int startIndex = 0;
        if (isPlayerB(getCurrentPlayer()))
            startIndex = 8;

        for (int i = startIndex; i < startIndex + 7; i++) {
            exploreState(i);
            loadState(_current.getElement());
        }

        _current.printDebug();
    }

    public int findBestMove(){
        Node<State> firstState = _current.getChild(0);
        for (int i = 1; i < _current.getChildrenCount(); i++) {
            Node<State> secondState = _current.getChild(i);
            if (firstState.getElement().getValue() < secondState.getElement().getValue())
                firstState = secondState;
        }

        loadState(firstState.getElement());
        _current = firstState;
        return firstState.getElement().getIndex();
    }

    public void doMove(int index){
        exploreState(index);
        findBestMove();
    }
}
