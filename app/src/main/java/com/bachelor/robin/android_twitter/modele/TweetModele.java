package com.bachelor.robin.android_twitter.modele;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bachelor.robin.android_twitter.R;
import com.bachelor.robin.android_twitter.tweet.Tweet;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rorod on 14/03/2016.
 */
public class TweetModele extends ArrayAdapter<Tweet> {


    //tweets est la liste des models Ã  afficher
    public TweetModele(Context context, ArrayList<Tweet> tweets) {
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
        viewHolder.pseudo.setText(tweet.getName());
        viewHolder.name.setText("@"+tweet.getScreen_name());
        viewHolder.text.setText(tweet.getText());
        //viewHolder.avatar.setImageDrawable(new ColorDrawable(Color.BLUE));
        try {

            URL myFileUrl = new URL (tweet.getAvatar());
            HttpURLConnection conn =
                    (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            viewHolder.avatar.setImageBitmap(BitmapFactory.decodeStream(is));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

