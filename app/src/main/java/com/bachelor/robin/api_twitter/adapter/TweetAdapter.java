package com.bachelor.robin.api_twitter.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bachelor.robin.api_twitter.R;
import com.bachelor.robin.api_twitter.tweet.Tweet;

import java.util.List;

/**
 * Created by rorod on 14/03/2016.
 */
public class TweetAdapter extends ArrayAdapter<Tweet> {


        //tweets est la liste des models Ã  afficher
        public TweetAdapter(Context context, List<Tweet> tweets) {
            super(context, 0, tweets);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_tweet,parent, false);
            }

            TweetViewHolder viewHolder = (TweetViewHolder) convertView.getTag();
            if(viewHolder == null){

                viewHolder = new TweetViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.pseudo = (TextView) convertView.findViewById(R.id.pseudo);
                viewHolder.text = (TextView) convertView.findViewById(R.id.text);
                viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
                convertView.setTag(viewHolder);

            }

            //getItem(position) va rÃ©cupÃ©rer l'item [position] de la List<Tweet> tweets
            Tweet tweet = getItem(position);

            //complet view
            viewHolder.pseudo.setText(tweet.getScreen_name());
            viewHolder.name.setText(tweet.getName());
            viewHolder.text.setText(tweet.getText());
            viewHolder.avatar.setImageDrawable(new ColorDrawable(Color.BLUE));
;
            return convertView;
        }

        private class TweetViewHolder{
            public TextView name;
            public TextView pseudo;
            public TextView text;
            public ImageView avatar;
        }
}

