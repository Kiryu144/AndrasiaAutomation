package net.andrasia.kiryu144.andrasiaautomation.util;

import java.util.ArrayList;

public class WeightedRandomList<T> {
    protected ArrayList<T> data;

    public WeightedRandomList(){
        data = new ArrayList<>();
    }

    public void add(T t, int chance){
        for(int i = 0; i < chance; ++i){
            data.add(t);
        }
    }

    public T getRandom() {
        return data.get((int) (Math.random() * data.size()));
    }
}
