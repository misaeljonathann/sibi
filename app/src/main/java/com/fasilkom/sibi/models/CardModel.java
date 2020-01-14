package com.fasilkom.sibi.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CardModel implements Parcelable {
    private String cardName;
    private String cardDesc;
    private Integer cardImg;

    public CardModel(String name, String desc, Integer img){
        cardName = name;
        cardDesc = desc;
        cardImg = img;
    }

    protected CardModel(Parcel in) {
        cardName = in.readString();
        cardDesc = in.readString();
        if (in.readByte() == 0) {
            cardImg = null;
        } else {
            cardImg = in.readInt();
        }
    }

    public String getCardName() {
        return cardName;
    }

    public String getCardDesc() {
        return cardDesc;
    }

    public Integer getCardImg() {
        return cardImg;
    }

    public static final Creator<CardModel> CREATOR = new Creator<CardModel>() {
        @Override
        public CardModel createFromParcel(Parcel in) {
            return new CardModel(in);
        }

        @Override
        public CardModel[] newArray(int size) {
            return new CardModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cardName);
        dest.writeString(cardDesc);
        if (cardImg == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(cardImg);
        }
    }
}
