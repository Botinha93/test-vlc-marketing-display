/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tv;

import com.sun.jna.NativeLibrary;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

/**
 *
 * @author botinha
 */
public class ComponenteVideo {
    private MediaPlayerFactory mediaPlayerFactory;

    
    private EmbeddedMediaPlayer mediaPlayer;

    private Canvas canvas;
    private File folder;
    private int id=0;
    private ArrayList <String> Lista= new ArrayList <String> ();
    private JFrame frame;

    
    public ComponenteVideo(JPanel panel, JFrame frame){
        this.frame=frame; //we need the frame to be able to close it after the usbevent
        panel.setBackground(Color.BLACK);
        canvas = new Canvas();
        canvas.setBounds(0, 0, 0, 0);
        panel.add(canvas);
        panel.revalidate();
        panel.repaint();
        panel.setLocation(0, 0);
        panel.setLayout(new GridLayout(1, 1));
        panel.setPreferredSize( new Dimension( 640, 480 ) );
        //Creation a media player :
        cretplayer();



    }
    private void cretplayer(){
        mediaPlayerFactory = new MediaPlayerFactory();
        mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
        mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() { 
        //this event watches if the movie as ended to release the player
        //and to close it after usbevent
        //the player component locks it swing parent...
            @Override
            public void finished(MediaPlayer mediaPlayer){
                if (Configs.KEY_USB==false){
                    
                    frame.dispose();
                }
                id+=1;
                mediaPlayer.release();
                mediaPlayer=null;
                cretplayer();
                if (id<Lista.size()){
                    Playnow();
                    
                }else{
                    id=0; //restart play loop
                    Playnow();
                    
                }
            }
        });
       
        CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
        mediaPlayer.setVideoSurface(videoSurface);
        NativeLibrary.addSearchPath("vlc", Configs.VLC); //needs vlc-dev
    }
    public void Play(String URL){
        //play a single file, for test only
        mediaPlayer.playMedia("URL");
    }
    private void MakeList(){
        //makes the play list for folder
        Lista.clear();
        for (final File fileEntry : folder.listFiles()) {
        if (!fileEntry.isDirectory()) {
            Lista.add(fileEntry.getName());
        }
    }
    }
    public void setFolder(String url){
        folder = new File(url);
    }
    public String getFolder(){
        return folder.getName();
    }
    public void PlayList(){
        MakeList();
        Playnow();
    }
    private void Playnow(){
        //starts the loop
        mediaPlayer.prepareMedia(folder.getPath()+"/"+Lista.get(id));
        mediaPlayer.start();
        
        
    }
    
    
}
