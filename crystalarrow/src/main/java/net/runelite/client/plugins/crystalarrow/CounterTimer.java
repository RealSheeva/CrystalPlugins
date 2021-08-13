package net.runelite.client.plugins.crystalarrow;

import java.time.Instant;
import java.time.Duration;

public class CounterTimer {
    public Instant last_poke = Instant.now();

    public long seconds_passed(){
        Instant curr_time = Instant.now();
        return(Duration.between(this.last_poke, curr_time).getSeconds());
    }

    public void poke(){
        Instant current_time = Instant.now();
        long seconds = Duration.between(last_poke, current_time).getSeconds();
        if(seconds < 11){
            return;
        }
        this.last_poke = Instant.now();
        return;
    }

}
