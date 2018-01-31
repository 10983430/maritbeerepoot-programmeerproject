package com.example.marit.serietrackerapplication;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * TODO omschrijving
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
    public static void markAsSeen(final String episodeTitle, final String seasonNumber,
                                  final String episodeNumber, FirebaseUser user,
                                  final String imdbid) {
        if (user != null) {
            FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
            String userid = user.getUid();
            final DatabaseReference dbref = fbdb.getReference("User/" + userid);
            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot value = dataSnapshot.child("SerieWatched");
                    HashMap<String, HashMap<String, HashMap<String, String>>> seen =
                            (HashMap<String, HashMap<String, HashMap<String, String>>>) value.getValue();

                    // If this is the first episode ever added to firebase, create a new
                    // new hashmap for all the episodes
                    if (seen == null) {
                        seen = new HashMap<>();
                        seen = firstTimeAdd(episodeNumber, episodeTitle, seasonNumber, imdbid, seen);
                    } else {
                        // Check if there is an episode of the serie that needs to be added
                        // in the database, by checking if there is a key with the serie title
                        DataSnapshot serietitle = dataSnapshot.child("SerieWatched").child(imdbid);
                        HashMap<String, HashMap<String, String>> seriefb = (HashMap<String, HashMap<String, String>>) serietitle.getValue();

                        // If not, create a new hashmap for the serie with the episode and season in it
                        if (seriefb == null) {
                            seen = firstTimeAdd(episodeNumber, episodeTitle, seasonNumber, imdbid, seen);
                        } else {
                            // If there is a episode from a specific serie in the database,
                            // check if there is already an episode added from the season
                            DataSnapshot seasontje = dataSnapshot.child("SerieWatched").child(imdbid).child("Season " + seasonNumber);
                            HashMap<String, String> episodeHashmap = (HashMap<String, String>) seasontje.getValue();

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
                        // TODO errorhandelen
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // TODO errorhandelen
                }
            });
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
