package com.example.marit.serietrackerapplication;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Contains methodes that mark an episode as seen or as unseen in Firebase
 */

public class FireBaseHelper {

    /**
     * Deletes the episode from Firebase and thereby marks it as unseen
     */
    public static void deleteEpisodeFromFirebase(final String seasonnumber,
                                                 final String episodenumber,
                                                 FirebaseUser user,
                                                 String imdbid) {
        if (user != null) {
            FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
            String userid = user.getUid();
            DatabaseReference dbref = fbdb.getReference("User/" + userid + "/SerieWatched/" +
                    imdbid + "/Season " + seasonnumber + "/E-" + episodenumber);
            dbref.removeValue();
        }
    }

    /**
     * Puts the episode in Firebase and thereby marks it as seen
     */
    public static void markAsSeen(String episodeTitle, String seasonNumber, String episodeNumber,
                                  FirebaseUser user, String imdbid) {
        if (user != null) {
            FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
            String userid = user.getUid();
            final DatabaseReference dbref = fbdb.getReference("User/" + userid);
            dbref.addListenerForSingleValueEvent(new FirebaseValueEventListener(episodeTitle,
                    seasonNumber, episodeNumber, user, imdbid, dbref));

        }

    }

    /**
     *  Puts the episode in Firebase
     */
    public static class FirebaseValueEventListener implements ValueEventListener {
        private String episodeTitle;
        private String seasonNumber;
        private String episodeNumber;
        FirebaseUser user;
        private String imdbid;
        private DatabaseReference dbref;

        public FirebaseValueEventListener(String episodeTitle, String seasonNumber,
                                          String episodeNumber, FirebaseUser user, String imdbid,
                                          DatabaseReference dbref) {
            this.episodeTitle = episodeTitle;
            this.seasonNumber=seasonNumber;
            this.episodeNumber = episodeNumber;
            this.user = user;
            this.imdbid = imdbid;
            this.dbref = dbref;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            DataSnapshot value = dataSnapshot.child("SerieWatched");
            HashMap<String, HashMap<String, HashMap<String, String>>> seen = (HashMap<String, HashMap<String, HashMap<String, String>>>) value.getValue();

            // If this is the first episode ever added to firebase, create a new
            // new hashmap for all the episodes
            if (seen == null) {
                seen = new HashMap<>();
                seen = firstTimeAdd(episodeNumber, episodeTitle, seasonNumber, imdbid, seen);
            } else {
                // Check if there is an episode of the serie that needs to be added
                // in the database, by checking if there is a key with the serie title
                DataSnapshot serieTitle = dataSnapshot.child("SerieWatched").child(imdbid);
                HashMap<String, HashMap<String, String>> seriefb = (HashMap<String, HashMap<String, String>>) serieTitle.getValue();

                // If not, create a new hashmap for the serie with the episode and season in it
                if (seriefb == null) {
                    seen = firstTimeAdd(episodeNumber, episodeTitle, seasonNumber, imdbid, seen);
                } else {
                    // If there is a episode from a specific serie in the database,
                    // check if there is already an episode added from the season
                    DataSnapshot season = dataSnapshot.child("SerieWatched").child(imdbid).child("Season " + seasonNumber);
                    HashMap<String, String> episodeHashmap = (HashMap<String, String>) season.getValue();

                    // If there isn't, add the season and the episode to the hashmap
                    // with watched episodes
                    if (episodeHashmap == null) {
                        episodeHashmap = new HashMap<>();
                        seen = addToExistingSerie(episodeHashmap, episodeNumber, episodeTitle, seasonNumber, imdbid, seen, seriefb);
                    }

                    // If there is, add the episode to the hasmap of the season and
                    // to the hashmap with watched episodes
                    else {
                        seen = addToExistingSerie(episodeHashmap, episodeNumber, episodeTitle, seasonNumber, imdbid, seen, seriefb);
                    }
                }
            }

            // Update the database by inserting the hashmap with watched episodes
            try {
                dbref.child("SerieWatched").setValue(seen);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // This error can only occur when there is an server-side reason to do so
            System.out.println("FIREBASE ERROR");
        }
    }

    /**
     * Adds the episode to a structure of hashmaps to insert in to Firebase, if there is no episode
     * of the serie in the database yet
     */
    public static HashMap firstTimeAdd(String episodeNumber, String episodeTitle,
                                       String seasonNumber, String imdbid, HashMap seen) {
        HashMap<String, HashMap<String, String>> season = new HashMap<>();
        HashMap<String, String> episodeHashmap = new HashMap<>();
        episodeHashmap.put("E-" + episodeNumber, episodeTitle);
        season.put("Season " + seasonNumber, episodeHashmap);
        seen.put(imdbid, season);
        return seen;
    }

    /**
     * Adds the episode to a structure of hashmaps to insert in to Firebase, if there is a episode
     * of the serie in firebase already
     */
    public static HashMap addToExistingSerie(HashMap episodeHashmap, String episodeNumber,
                                             String episodeTitle, String seasonNumber,
                                             String imdbid, HashMap seen, HashMap seriefb) {
        episodeHashmap.put("E-" + episodeNumber, episodeTitle);
        seriefb.put("Season " + seasonNumber, episodeHashmap);
        seen.put(imdbid, seriefb);
        return seen;
    }
}
