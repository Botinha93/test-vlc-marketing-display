/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tv;
 
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
 
// this watchs directory events.
class Usbwatcher implements Runnable {
 
    private Path path;;
    Thread player;
    Usbwatcher() {
        this.path = FileSystems.getDefault().getPath("/dev");
        startplayer();
        
    }
    private void startplayer(){
        player=null;
        Configs.KEY_USB=true;
        player = new Thread(new Runnable() {
            public void run() {
                new Tela().setVisible(true);
            }
        });
        player.start();
    }
 
    // watch for files in /dev
    private void printEvent(WatchEvent<?> event) {
        Kind<?> kind = event.kind();
        if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
            Path pathModified = (Path) event.context();
            if(pathModified.toString().matches("(.*)"+Configs.DRIVER_TO_WATCH+"(.*)")){
                startplayer();
            
            }
        } else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
            Path pathModified = (Path) event.context();
            if(pathModified.toString().matches("(.*)"+Configs.DRIVER_TO_WATCH+"(.*)")){
                Configs.KEY_USB=false;
            }
        } 
        
    }
 
    @Override
    public void run() {
        try {
            WatchService watchService = path.getFileSystem().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
 
            // loop forever to watch directory
            while (true) {
                WatchKey watchKey;
                watchKey = watchService.take(); // this call is blocking until events are present
 
                // poll for file system events on the WatchKey
                for (final WatchEvent<?> event : watchKey.pollEvents()) {
                    printEvent(event);
                }
 
                // if the watched directed gets deleted, get out of run method
                if (!watchKey.reset()) {
                    System.out.println("No longer valid");
                    watchKey.cancel();
                    watchService.close();
                    break;
                }
            }
 
        } catch (InterruptedException ex) {
            System.out.println("interrupted. Goodbye");
            return;
        } catch (IOException ex) {
            ex.printStackTrace();  
            return;
        }
    }
}
 
