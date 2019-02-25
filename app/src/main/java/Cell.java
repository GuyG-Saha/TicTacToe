import android.net.wifi.p2p.WifiP2pManager;

import com.condinginflow.saywhat.cellState;

public class Cell {
    private cellState state;
    private WifiP2pManager.ActionListener myListener;

    public Cell() {
        this.state = cellState.FREE;
    }

    public Cell(WifiP2pManager.ActionListener listener) {
        this.state = cellState.FREE;
        this.myListener = listener;
    }

    public void setState(cellState newState) {
        this.state = newState;
    }

    public cellState getState() {
        return state;
    }

    public String toString() {
        return "Cell is " + this.state;
    }
}
