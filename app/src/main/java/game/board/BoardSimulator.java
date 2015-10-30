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
        boolean result = false;

        if (isPlayerA(getCurrentPlayer()))
            result = 7 - _cups[index].getCount() == index;
        else
            result = 15 - _cups[index].getCount() == index;

        return result;
    }

    private boolean takesExtraMove(int index){
        boolean result = false;

        if (isPlayerA(getCurrentPlayer()))
            result = 15 - (_cups[index].getCount() - 1) == index;
        else
            result = 7 - (_cups[index].getCount() - 1) == index;

        return result;
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
        Player player = _currentPlayer;
        int score = 0;

        HandOfShells hand = pickUpShells(index);
        if (hand != null)
            ++score;

        while (hand != null && !hand.isEmpty()){
            int i = hand.getNextCup();

            if (_currentPlayer.isPlayersCup(_cups[i], true))
                score += 2;

            if (givesExtraMove(i) || takesExtraMove(i));
                score += 1;

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

        _current.addChild(new Node<>(new State(player, index, score, getState())));
    }

    public void explore(){
        explore(false);
    }
    public void explore(boolean playerB){
        int startIndex = 0;
        if (playerB)
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
