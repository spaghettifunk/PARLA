package davideberdin.goofing.controllers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import davideberdin.goofing.FullscreenImageActivity;
import davideberdin.goofing.R;


public class HistoryCardsAdapter extends RecyclerView.Adapter<HistoryCardsAdapter.CardViewHolder> {

    private ArrayList<HistoryCard> cardsList;

    public HistoryCardsAdapter(ArrayList<HistoryCard> contactList) {
        this.cardsList = contactList;
    }

    @Override
    public int getItemCount() {
        return cardsList.size();
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {
        HistoryCard cl = cardsList.get(i);
        cardViewHolder.cardId.setText(cl.getCardId());
        cardViewHolder.cardDate.setText(cl.getCardDate());

        byte[] imageByte = cl.getImageByteArray();
        Bitmap cardBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        cardViewHolder.image.setImageBitmap(cardBitmap);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card, parent, false);
        itemView.setMinimumWidth(parent.getMeasuredWidth());

        return new CardViewHolder(itemView);
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        protected TextView cardId;
        protected TextView cardDate;
        protected ImageView image;

        public CardViewHolder(View v) {
            super(v);
            cardId = (TextView) v.findViewById(R.id.cardId);
            cardDate = (TextView) v.findViewById(R.id.cardDate);
            image = (ImageView) v.findViewById(R.id.cardImage);

            image.setDrawingCacheEnabled(true);
            image.buildDrawingCache();

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // go full screen
                    Bitmap bm = image.getDrawingCache();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    Intent fullscreen = new Intent(v.getContext(), FullscreenImageActivity.class);
                    fullscreen.putExtra("isPitch", false);
                    fullscreen.putExtra("vowelchart", byteArray);

                    v.getContext().startActivity(fullscreen);
                }
            });
        }
    }
}
