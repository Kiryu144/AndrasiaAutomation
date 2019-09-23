package net.andrasia.kiryu144.andrasiaautomation.util;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class LocationMap<T> {
    protected HashMap<World, HashMap<Double, HashMap<Double, HashMap<Double, T>>>> data;

    public LocationMap() {
        data = new HashMap<>();
    }

    public void put(Location location, T t){
        HashMap<Double, HashMap<Double, HashMap<Double, T>>> x = data.get(location.getWorld());
        if(x == null){
            data.put(location.getWorld(), new HashMap<>());
            x = data.get(location.getWorld());
        }

        HashMap<Double, HashMap<Double, T>> y = x.get(location.getX());
        if(y == null){
            x.put(location.getX(), new HashMap<>());
            y = x.get(location.getX());
        }

        HashMap<Double, T> z = y.get(location.getY());
        if(z == null){
            y.put(location.getY(), new HashMap<>());
            z = y.get(location.getY());
        }

        z.put(location.getZ(), t);
    }

    public T get(Location location){
        HashMap<Double, HashMap<Double, HashMap<Double, T>>> x = data.get(location.getWorld());
        if(x != null){
            HashMap<Double, HashMap<Double, T>> y = x.get(location.getX());
            if(y != null){
               HashMap<Double, T> z = y.get(location.getY());
               if(z != null){
                   return z.get(location.getZ());
               }
            }
        }
        return null;
    }

    public T remove(Location location){
        HashMap<Double, HashMap<Double, HashMap<Double, T>>> x = data.get(location.getWorld());
        if(x != null){
            HashMap<Double, HashMap<Double, T>> y = x.get(location.getX());
            if(y != null){
                HashMap<Double, T> z = y.get(location.getY());
                if(z != null){
                    return z.remove(location.getZ());
                }
            }
        }
        return null;
    }
}
