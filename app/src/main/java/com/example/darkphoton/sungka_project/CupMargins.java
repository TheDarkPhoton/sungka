package com.example.darkphoton.sungka_project;

/**
 * Created by darkphoton on 24/10/15.
 */
public class CupMargins {
    public final float scale;
    public final int store, cup, spaceTop, spaceLeft, spaceSmall, spaceStoreTop;

    CupMargins(int screenWidth, int screenHeight, int cupSize){
        store = (int) (screenWidth * 0.156);                                                        //Store cups are 15.6% of the screen width
        cup = (int) (screenWidth * 0.078);                                                          //Small cups are 7.8% of the screen width
        scale = cupSize / 199.0f;                                                                   // A scale factor for text sizes

        //Calculates spaces between cups
        spaceSmall = (int) (screenWidth * 0.005);
        spaceStoreTop = (int) (screenHeight * 0.05);
        spaceLeft = (screenWidth - (((store * 2) + (cupSize * 7) + (spaceSmall * 14)))) / 2;
        spaceTop = ((screenHeight - ((store + (cupSize * 2) + (spaceStoreTop * 2)))) / 2) - cupSize / 2;
    }
}
