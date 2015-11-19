package davideberdin.goofing.controllers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import davideberdin.goofing.R;


public class HistoryCardsAdapter extends RecyclerView.Adapter<HistoryCardsAdapter.CardViewHolder> {

    private List<HistoryCard> cardsList;

    public HistoryCardsAdapter(List<HistoryCard> contactList) {
        this.cardsList = contactList;
    }

    @Override
    public int getItemCount() {
        return cardsList.size();
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {
        HistoryCard cl = cardsList.get(i);
        cardViewHolder.cardDate.setText(cl.getCardDate());

        byte[] imageByte = cl.getImageByteArray();
        Bitmap cardBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        cardViewHolder.image.setImageBitmap(cardBitmap);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_card, viewGroup, false);

        return new CardViewHolder(itemView);
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        protected TextView cardDate;
        protected ImageView image;

        public CardViewHolder(View v) {
            super(v);
            cardDate = (TextView) v.findViewById(R.id.cardDate);
            image = (ImageView) v.findViewById(R.id.cardImage);
        }
    }
}
